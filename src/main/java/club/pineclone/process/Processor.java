package club.pineclone.process;

import club.pineclone.process.action.ActionSerializer;
import club.pineclone.process.executor.ExecutorFactory;
import club.pineclone.process.executor.ExecutorImpl;
import club.pineclone.process.api.Monitor;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

public interface Processor {

    /**
     * The method to launch the monitor, a monitor should have the ability to monitor user's action, this is not only
     * single aspect but multiple, for example the mouse motion, clicking, key input and ect.
     */
    void launchMonitor();

    /**
     * The method to launch the executor, an executor should have the ability to execute the given action list.
     */
    void launchExecutor();

    /**
     * This method will cancel the imitater, usually an imitater will include single or multiple executors or monitors,
     * this method should be able to stop all of those monitors and executors.
     */
    void setCanceled();

    /**
     * This method will submit the recorded action in the monitor into the executor.
     */
    void submit();

    /**
     * This method will read the file's information, and transform the information into executable action list
     * and then submit into the executor, this will allow the executor to run the task in the future.
     */
    void submit(File file);

    /**
     * This method has the same function with the last one.
     */
    void submit(String filePath);

    /**
     * This method will code the action list in the monitor into the pattern of file, and then write them into the given
     * file, usually this method will need a serializer to help.
     */
    void saveOpAsFile(File file);

    /**
     * This method got the very same function with the last one.
     */
    void saveOpAsFile(String filePath);

    /**
     * This method will return if the monitor in the imitater has action objects in its action list, this method will usually
     * call the method {@link Monitor#hasActions()} to detect if the monitor has recorded action in it.
     */
    boolean isSavable();

    /**
     * This method will return the boolean value to show if the imitater could run, imitater could run means the executor
     * in the imitater has action objects in its action list, to make the imitater's executor enable to be executed, use
     * the method {@link Processor#submit()} to submit task queue into the executor.
     */
    boolean isExecutable();

    String getMonitorName();

    String getExecutorName();

    class FutureSet {
        private final HashMap<Type, List<? extends ScheduledFuture<?>>> futureMap = new HashMap<>();
        private final List<? extends ScheduledFuture<?>> futures1;
        private final List<? extends ScheduledFuture<?>> futures2;
        private final List<? extends ScheduledFuture<?>> futures3;

        private enum Type {
            MOUSE_MOTION, MOUSE_ACTION, KEY_ACTION;
        }

        public FutureSet(List<? extends ScheduledFuture<?>> futures1,
                         List<? extends ScheduledFuture<?>> futures2,
                         List<? extends ScheduledFuture<?>> futures3) {

            this.futures1 = futures1;
            this.futures2 = futures2;
            this.futures3 = futures3;

            this.futureMap.put(Type.MOUSE_MOTION, futures1);
            this.futureMap.put(Type.MOUSE_ACTION, futures2);
            this.futureMap.put(Type.KEY_ACTION, futures3);
        }

        public List<? extends ScheduledFuture<?>> getFutures(Type type) {
            return futureMap.get(type);
        }

        public boolean isAllDone() {
            boolean flag1 = futures1.stream().allMatch(Future::isDone);
            boolean flag2 = futures2.stream().allMatch(Future::isDone);
            boolean flag3 = futures3.stream().allMatch(Future::isDone);
            return (flag1 && flag2 && flag3);
        }

        public boolean isDone(Type type) {
            return futureMap.get(type).stream().allMatch(Future::isDone);
        }
    }

    static void main(String[] args) throws InterruptedException, AWTException {

        ExecutorImpl executorBoost = (ExecutorImpl) ExecutorFactory.newExecutorImpl();
        executorBoost.submit(ActionSerializer.decode("D:\\Desktop\\test.imit"));
        executorBoost.launch(3, 3000,
                var -> System.out.println("pre"),
                var -> System.out.println("post"),
                var -> System.out.println("terminal"));
    }
}
