package club.pineclone.process.executor.impl;

import club.pineclone.process.action.ActionList;
import club.pineclone.process.action.interfaces.MouseAction;
import club.pineclone.process.executor.interfaces.MouseExecutor;

import java.awt.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MouseExecutorImpl extends MouseExecutor {

    private boolean isCanceled = false;
    private final Robot robot;

    public MouseExecutorImpl() throws AWTException {
        this.robot = new Robot();
    }

    @Override
    public void submit(ActionList actions) {
        this.actions.clear();
        this.actions.addAll(actions);
    }

    @Override
    public void setCanceled() {
        this.isCanceled = true;
    }

    @Override
    public void launch() {
        isCanceled = false;
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(10);
        actions.stream().map(a -> pool.schedule(() -> {
                    if (isCanceled) {
                        pool.shutdownNow();
                        return;
                    }
                    a.act(robot);
                }, a.getDelay(),
                TimeUnit.MILLISECONDS)).collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return "Mouse ExecutorImpl";
    }
}
