package com.game.thread.gate;

import com.game.thread.message.IMessage;
import com.game.thread.message.MessageHandler;
import com.game.timer.ITriggerTaskExecutor;
import com.game.timer.impl.TriggerTaskExecutor;
import com.game.util.GameSession;
import com.game.util.RunTimeUtils;
import com.game.util.SimpleThreadFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 门卫，用于执行登陆之前的请求
 * @Author: liguorui
 * @Date: 2020/12/4 下午4:23
 */
public class GateKeeper extends MessageHandler<GateKeeper> {

    private static ExecutorService DEFAULT_EXECUTOR_SERVICE = Executors.newFixedThreadPool(RunTimeUtils.TWICE_CPU,
            new SimpleThreadFactory("GateKeeper-Worker"));

    private static ITriggerTaskExecutor DEFAULT_TRIGGER_TASK_EXECUTOR = new TriggerTaskExecutor(5, "GateKeeper-Scheduler");

    private static int NUM = 10;

    private static GateKeeper[] gateKeepers = new GateKeeper[NUM];

    static {
        for (int i = 0; i < gateKeepers.length; i++) {
            gateKeepers[i] = new GateKeeper();
        }
    }

    public GateKeeper() {
        super(DEFAULT_EXECUTOR_SERVICE, DEFAULT_TRIGGER_TASK_EXECUTOR);
    }

    public GateKeeper(ExecutorService executorService, ITriggerTaskExecutor triggerTaskExecutor) {
        super(executorService, triggerTaskExecutor);
    }

    public static void executeMessage(final GameSession session, final IMessage<GateKeeper> message) {
        gateKeepers[hashIndex(session)].addMessage(message);
    }

    public static void schedule(GameSession session, String taskName, IMessage<GateKeeper> message,
                                long delay, TimeUnit unit) {
        gateKeepers[hashIndex(session)].schedule(taskName, message, delay, unit);
    }

    private static int hashIndex(GameSession session) {
        int index = 0;
        if (StringUtils.isBlank(session.getAccount())) {
            index = (int)(session.getIpHashCode() % NUM);
        } else {
            index = session.getAccount().hashCode() % NUM;
        }
        return Math.abs(index);
    }
}
