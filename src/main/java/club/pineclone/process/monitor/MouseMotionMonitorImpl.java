package club.pineclone.process.monitor;

import club.pineclone.process.action.ActionList;
import club.pineclone.process.action.MouseMoveAction;
import club.pineclone.process.api.Monitor;

import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MouseMotionMonitorImpl extends Monitor {

    private boolean isCanceled = false;

    @Override
    public void launch() {
        this.isCanceled = false;
        actions.clear();
        long begin = System.currentTimeMillis();
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(10);
        pool.scheduleWithFixedDelay(() -> {
                    if (isCanceled) {
                        pool.shutdown();
                        return;
                    }
                    actions.add(new MouseMoveAction(System.currentTimeMillis() - begin,
                            MouseInfo.getPointerInfo().getLocation()));
                },
                0, 1, TimeUnit.MILLISECONDS);
    }

    @Override
    public void setCanceled() {
        this.isCanceled = true;
    }

    @Override
    public ActionList getActions() {
        return actions;
    }

    @Override
    protected void subscribe() {

    }

    @Override
    protected void unsubscribe() {

    }

    @Override
    public String getName() {
        return "Mouse MotionImpl";
    }
}
