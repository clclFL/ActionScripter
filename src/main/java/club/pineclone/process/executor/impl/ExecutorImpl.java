package club.pineclone.process.executor.impl;

import club.pineclone.api.CallBack;
import club.pineclone.process.action.ActionList;
import club.pineclone.process.executor.interfaces.Executor;
import club.pineclone.utils.Log;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ExecutorImpl extends Executor {

    private static final int DEF_THREAD_POOL_SIZE = 10;

    private final Robot robot;
    private boolean isCanceled = false;

    /**
     * This is a list which should include all the running scheduled task's futures, which means if the executor
     * is stopped by outside, then will simply cancel all the future task in the list and clear this list at the same time.
     */
    protected final List<ScheduledFuture<?>> buffer = new LinkedList<>();
    protected final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(DEF_THREAD_POOL_SIZE);


    public ExecutorImpl() throws AWTException {
        this.robot = new Robot();
    }

    /**
     * This method will allow you to add an action to the end of list, after the cycle is done,
     * then this hook will be automatically called.
     *
     * @param hook the hook to be executed in the end of action list.
     */
    private void setTailHook(CallBack<Void> hook) {
        ((ActionList.ActionProxy) this.actions.get(actions.size() - 1)).setHook(hook);
    }
    
    private void setHeadHook(CallBack<Void> hook) {
        ((ActionList.ActionProxy)this.actions.get(0)).setHook(hook);
    }

    @Override
    public void submit(ActionList actions) {
        this.actions.clear();
        this.actions.addAll(actions);
        ActionList.ActionProxy.asTail(this.actions, r -> {
        });
        ActionList.ActionProxy.asHead(this.actions, r -> {
        });
    }



    public boolean isDone() {
        return this.buffer.stream().allMatch(Future::isDone);
    }

    /**
     * The basic launching method of the executor, after calling this method this executor will simply start executing.
     * For the class ExecutorImpl this method may better not be used, considering the method {@link ExecutorImpl#launch(int)}
     */
    @Override
    public void launch() {
        List<? extends ScheduledFuture<?>> futures = actions.parallelStream()
                .map(a -> executorService.schedule(() ->
                        a.act(this.robot), a.getDelay(), TimeUnit.MILLISECONDS)).collect(Collectors.toList());
        this.buffer.addAll(futures);
    }

    @Override
    public String getName() {
        return "Executor Impl";
    }

    public void launch(int times) {
        launch(times, 0, 0, var -> {
        }, var -> {
        }, var -> {
        });
    }

    public void launch(int times, long delay) {
        launch(times, 0, delay, var -> {
        }, var -> {
        }, var -> {
        });
    }

    public void launch(int times, long initialDelay, long delay) {
        launch(times, initialDelay, delay, var -> {
        }, var -> {
        }, var -> {
        });
    }

    public void launch(int times, long delay,
                       CallBack<Void> pre,
                       CallBack<Void> mid,
                       CallBack<Void> post) {
        launch(times, 0, delay, pre, mid, post);
    }

    public void launch(int times, long initialDelay, long delay,
                       //The Anchors
                       CallBack<Void> pre,
                       CallBack<Void> post,
                       CallBack<Void> terminal) {
        boolean flag1 = actions.isEmpty();
        boolean flag2 = times <= 0 || delay < 0;
        boolean flag3 = pre == null || post == null || terminal == null;
        if (flag1 || flag2 || flag3) return;
        isCanceled = false;
        prepHook4Loop(new AtomicInteger(times), delay, pre ,post, terminal);
        if (initialDelay != 0) {
            try {
                Thread.sleep(initialDelay);
            } catch (InterruptedException e) {
                Log.infoExceptionally("Cannot correctly set up the executor before launching.", e);
            }
        }
        launch();
    }

    /**
     * This method will stop the executing task in any time.
     */
    @Override
    public void setCanceled() {
        //This will immediately stop the running task.
        this.buffer.parallelStream().forEach(f -> f.cancel(true));
        //however, if the task is running, then simply stopping the future will not work because the
        //buffer has remove all task from itself, then we need to set the param isCanceled to true to stop
        //the next round.
        this.isCanceled = true;
        buffer.clear();
    }

    private void prepHook4Loop(AtomicInteger times, long delay,
                               CallBack<Void> pre,
                               CallBack<Void> post,
                               CallBack<Void> terminal) {
        setHeadHook(pre);
        setTailHook(r -> {
            //cancel all the task, and prepare for the new loop.
            buffer.forEach(f -> f.cancel(true));
            buffer.clear();
            post.callBack(null);

            int t = times.decrementAndGet();
            if (t == 0) terminal.callBack(null);

            if (t > 0 && !isCanceled) {
                executorService.schedule(() -> launch(), delay, TimeUnit.MILLISECONDS);
            }
        });
    }
}
