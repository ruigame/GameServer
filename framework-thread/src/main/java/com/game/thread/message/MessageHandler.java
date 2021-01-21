package com.game.thread.message;

import com.game.timer.ITriggerTaskExecutor;
import com.game.timer.TriggerFuture;
import com.game.timer.impl.TriggerTaskExecutor;
import com.game.util.*;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 该类及其子类销毁前需要调用destroy方法
 * @author liguorui
 * @date 2018/1/7 18:45
 */
public class MessageHandler<H extends IMessageHandler<?>> implements Runnable, IMessageHandler<H> {

    private static Logger profileLog = LoggerFactory.getLogger("profileLogger");

    private static ExecutorService DEFAULT_EXECUTOR_SERVICE = Executors.newFixedThreadPool(RunTimeUtils.TWICE_CPU,
                                                                                    new SimpleThreadFactory("MessageHandler-Worker"));

    private static ITriggerTaskExecutor DEFAULT_TRIGGER_TASK_EXECUTOR = new TriggerTaskExecutor(5, "MessageHandler-Scheduler");

    private ExecutorService executorService;

    private ITriggerTaskExecutor triggerTaskExecutor;

    private Queue<IMessage<H>> messages = new ConcurrentLinkedDeque<>();

    private AtomicInteger size = new AtomicInteger();

    private volatile Thread currentThread;

    private LinkedBlockingQueue<TriggerTask> futureQueue = new LinkedBlockingQueue<>();

    private static volatile LinkedBlockingQueue<WeakReference<MessageHandler<?>>> handlerQueue = new LinkedBlockingQueue<>();

    private volatile boolean close;

    static {
        DEFAULT_TRIGGER_TASK_EXECUTOR.scheduleWithFixedDelay("定时清除完成任务future", new Runnable() {
            @Override
            public void run() {
                clearFinishFuture();
            }
        }, 5, 2, TimeUnit.MINUTES);
    }

    public MessageHandler(ExecutorService executorService, ITriggerTaskExecutor triggerTaskExecutor) {
        super();
        this.executorService = executorService;
        this.triggerTaskExecutor = triggerTaskExecutor;
        handlerQueue.add(new WeakReference<>(this));
    }

    public MessageHandler() {
        this(DEFAULT_EXECUTOR_SERVICE, DEFAULT_TRIGGER_TASK_EXECUTOR);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        this.currentThread = Thread.currentThread();
        long startTime = System.currentTimeMillis();
        boolean continueTask = false;
        while(true) {
            IMessage<H> message = messages.poll();
            if (message == null) {
                break;
            }
            try {
                UseTimer useTimer = new UseTimer("MessageHandler" + message.name(), 500);
                message.execute((H)this);
                useTimer.printUseTime();
            } catch (Exception e) {
                e.printStackTrace();
                ExceptionUtils.log("{}", getIdent(), e);
            }

            int curSize = size.decrementAndGet();
            if (curSize <= 0) {
                break;
            }
            if (System.currentTimeMillis() - startTime > 30000) { //时间片30000ms，防止一直占用线程
                continueTask = true;
                break;
            }
        }
        this.currentThread = null;
        if (continueTask) {
            executorService.execute(this);
        }
    }

    @Override
    public void addMessage(IMessage<H> message) {
        if (isClose()) {
            ExceptionUtils.log("已经销毁 {} {}", getIdent(), message.name());
        }
        messages.add(message);
        int curSize = size.incrementAndGet();
        if (curSize == 1) {
            executorService.execute(this);
        }
    }

    public TriggerFuture schedule(String taskName, final IMessage<H> message,
                                  long delay, TimeUnit unit) {
        Preconditions.checkArgument(!isClose());
        Runnable task = new Runnable() {
            @Override
            public void run() {
                addMessage(message);
            }
        };
        RefRunnable softRefRunnable;
        String errorMsg = taskName + "==" + message.getClass().toString();
        if (unit.toMinutes(delay) < 60) {
            softRefRunnable = RefRunnable.createStrongRefRun(task, errorMsg);
        } else if (unit.toHours(delay) < 24) {
            softRefRunnable = RefRunnable.createSoftRefRun(task, errorMsg);
        } else {
            softRefRunnable = RefRunnable.createWeakRefRun(task, errorMsg);
        }
        TriggerFuture future = triggerTaskExecutor.schedule(taskName, softRefRunnable, delay, unit);
        softRefRunnable.setTriggerFuture(future);
        addFuture(task, future);
        return future;
    }

    public TriggerFuture schedule(String taskName, final IMessage<H> message, long executeTime) {
        Preconditions.checkArgument(!isClose());
        Runnable task = new Runnable() {
            @Override
            public void run() {
                addMessage(message);
            }
        };
        RefRunnable softRefRunnable;
        String errorMsg = taskName + "==" + message.getClass().toString();
        long delay = executeTime - System.currentTimeMillis();
        if (delay < TimeUtils.TimeMillisOneHour) {//一小时
            softRefRunnable = RefRunnable.createStrongRefRun(task, errorMsg);
        } else if (delay < TimeUtils.TimeMillisOneDay) {//一天
            softRefRunnable = RefRunnable.createSoftRefRun(task, errorMsg);
        } else {
            softRefRunnable = RefRunnable.createWeakRefRun(task, errorMsg);
        }
        TriggerFuture future = triggerTaskExecutor.schedule(taskName, softRefRunnable, executeTime);
        softRefRunnable.setTriggerFuture(future);
        addFuture(task, future);
        return future;
    }

    public TriggerFuture scheduleAtFixedRate(String taskName, final IMessage<H> message,
                                           long initialDelay, long period, TimeUnit unit) {
        Preconditions.checkArgument(!isClose());
        Runnable task = new Runnable() {
            @Override
            public void run() {
                addMessage(message);
            }
        };
        String errorMsg = taskName + "==" + message.getClass().toString();
        RefRunnable softRefRunnable = RefRunnable.createWeakRefRun(task, errorMsg);
        TriggerFuture future = triggerTaskExecutor.scheduleAtFixedRate(taskName, softRefRunnable, initialDelay, period, unit);
        softRefRunnable.setTriggerFuture(future);
        addFuture(task, future);
        return future;
    }

    public TriggerFuture scheduleAtFixedDelay(String taskName, final IMessage<H> message,
                                            long initialDelay, long delay, TimeUnit unit ) {
        Preconditions.checkArgument(!isClose());
        Runnable task = new Runnable() {
            @Override
            public void run() {
                addMessage(message);
            }
        };
        String errorMsg = taskName + "==" + message.getClass().toString();
        RefRunnable softRefRunnable = RefRunnable.createWeakRefRun(task, errorMsg);
        TriggerFuture future = triggerTaskExecutor.scheduleWithFixedDelay(taskName, softRefRunnable, initialDelay, delay, unit);
        softRefRunnable.setTriggerFuture(future);
        addFuture(task, future);
        return future;
    }

    private static void clearFinishFuture() {
        Iterator<WeakReference<MessageHandler<?>>> iterator = handlerQueue.iterator();
        while(iterator.hasNext()) {
            WeakReference<MessageHandler<?>> handlerRef = iterator.next();
            MessageHandler<?> messageHandler = handlerRef.get();
            if (messageHandler == null) {
                iterator.remove();
                continue;
            }
            messageHandler.clearInvalidFuture();
        }
    }

    private void clearInvalidFuture() {
        Iterator<TriggerTask> iterator = futureQueue.iterator();
        while(iterator.hasNext()) {
            TriggerFuture triggerFuture = iterator.next().future;
            if (triggerFuture.isFinish() || triggerFuture.isCanceled()) {
                iterator.remove();
            }
        }
    }

    public void addFuture(Runnable runnable, TriggerFuture future) {
        futureQueue.add(new TriggerTask(runnable, future));
    }

    public boolean isInThread() {
        return Thread.currentThread() == currentThread;
    }

    public void cancleAllSchedule() {
        for (TriggerTask triggerTask : futureQueue) {
            triggerTask.future.cancel();
        }
        futureQueue.clear();
    }

    public int getMessageSize() {
        return size.get();
    }

    /**
     * 该类销毁前调用该方法
     */
    public void destroy() {
        this.close = true;
        cancleAllSchedule();
    }

    public String printTopMessage() {
        Map<Class<?>, MessageNum> messageClazzMap = new HashMap<>(getMessageSize());
        for (IMessage<H> message : messages) {
            messageClazzMap.computeIfAbsent(message.getClass(), c -> new MessageNum(c)).incNum();
        }
        List<MessageNum> messageNumList = new ArrayList<>();
        messageNumList.addAll(messageClazzMap.values());
        messageNumList.sort((m1,m2)->Integer.compare(m2.getNum(), m1.getNum()));
        StringBuilder builder = new StringBuilder(128);
        for (int i = 0, lenth = messageNumList.size(); i < 5 && i < lenth; i++) {
            MessageNum messageNum = messageNumList.get(i);
            if (i > 0) {
                builder.append(",");
            }
            builder.append(messageNum.getMessageClazz()).append("=").append(messageNum.getNum());
        }
        return builder.toString();
    }

    /**
     * 标识，报错打印日志使用，最好是该对象唯一标识，（例如PlayerActor,打印账号）
     * @return
     */
    protected String getIdent() {
        return getClass().getSimpleName();
    }

    public boolean isClose() {
        return close;
    }
    private static class MessageNum {
        private Class<?> messageClazz;
        private int num;

        public MessageNum(Class<?> messageClazz) {
            super();
            this.messageClazz = messageClazz;
        }

        public Class<?> getMessageClazz() {
            return messageClazz;
        }

        public int getNum() {
            return num;
        }

        public void incNum() {
            num++;
        }
    }

    private static class TriggerTask {
        private Runnable runnable; //作用为了防止回收
        private TriggerFuture future;

        public TriggerTask(Runnable runnable, TriggerFuture future) {
            super();
            this.runnable = runnable;
            this.future = future;
        }
    }

    public Queue<IMessage<H>> getMessages() {
        return messages;
    }
}
