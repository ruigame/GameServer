package com.game.thread.message;

import com.game.timer.TriggerFuture;
import com.game.util.ExceptionUtils;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * @Author: liguorui
 * @Date: 2020/11/30 下午10:32
 */
public class RefRunnable implements Runnable{

    private Reference<Runnable> runnableReference;
    private String errorMsg;
    private TriggerFuture triggerFuture;

    private RefRunnable(Reference<Runnable> runnableReference, String errorMsg) {
        this.runnableReference = runnableReference;
        this.errorMsg = errorMsg;
    }

    public static RefRunnable createSoftRefRun(Runnable runnable, String errorMsg) {
        return new RefRunnable(new SoftReference<>(runnable), errorMsg);
    }

    public static RefRunnable createWeakRefRun(Runnable runnable, String errorMsg) {
        return new RefRunnable(new WeakReference<>(runnable), errorMsg);
    }

    public static RefRunnable createStrongRefRun(Runnable runnable, String errorMsg) {
        return new RefRunnable(new StrongReference<>(runnable), errorMsg);
    }

    @Override
    public void run() {
        Runnable runnable = runnableReference.get();
        if (runnable != null) {
            runnable.run();
        } else {
            ExceptionUtils.log("调度饮用为空{}", errorMsg);
            if (triggerFuture != null) {
                triggerFuture.cancel();
                triggerFuture = null;
            }
        }
    }

    public TriggerFuture getTriggerFuture() {
        return triggerFuture;
    }

    public void setTriggerFuture(TriggerFuture triggerFuture) {
        this.triggerFuture = triggerFuture;
    }
}
