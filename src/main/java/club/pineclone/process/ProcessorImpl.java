package club.pineclone.process;

import club.pineclone.api.CallBack;
import club.pineclone.process.action.ActionList;
import club.pineclone.process.action.ActionSerializer;
import club.pineclone.process.executor.ExecutorFactory;
import club.pineclone.process.executor.impl.ExecutorImpl;
import club.pineclone.process.monitor.MonitorFactory;
import club.pineclone.process.monitor.interfaces.Monitor;

import java.awt.*;
import java.io.File;

public class ProcessorImpl implements Processor {

    private final Monitor monitor;
    private final ExecutorImpl executor;
    private boolean isMonitorRunning = false;
    private boolean isExecutorRunning = false;

    public ProcessorImpl() throws AWTException {
        this.monitor = MonitorFactory.newMonitorImpl();
        this.executor = (ExecutorImpl) ExecutorFactory.newExecutorImpl();
    }

    public void launchMonitor() {
        isMonitorRunning = true;
        monitor.launch();
    }

    public void setCanceled() {
        if (isMonitorRunning) {
            monitor.setCanceled();
            isMonitorRunning = false;
        }
        if (isExecutorRunning) {
            executor.setCanceled();
            isExecutorRunning = false;
        };
    }

    public void submit() {
        executor.submit(monitor.getActions());
    }

    public void launchExecutor() {
        isExecutorRunning = true;
        executor.launch();
    }

    public void launchExecutor(int times, long initialDelay, long delay,
                               CallBack<Void> pre,
                               CallBack<Void> post,
                               CallBack<Void> terminal) {
        isExecutorRunning = true;
        executor.launch(times , initialDelay , delay , pre , post , terminal);
    }

    public void saveOpAsFile(File file) {
        if (!isSavable()) return;
        ActionSerializer.code(monitor.getActions() , file);
    }

    public void saveOpAsFile(String filePath) {
        if (!isSavable()) return;
        ActionSerializer.code(monitor.getActions() , new File(filePath));
    }

    public boolean isSavable() {
        return monitor.hasActions();
    }

    public boolean isExecutable() {
        return executor.hasActions();
    }

    @Override
    public String getMonitorName() {
        return monitor.getName();
    }

    @Override
    public String getExecutorName() {
        return executor.getName();
    }

    public void submit(String filePath) {
        submit(new File(filePath));
    }

    public void submit(File file) {
        ActionList decode = ActionSerializer.decode(file);
        executor.submit(decode);
    }

    //for test
    public static void main(String[] args) throws AWTException, InterruptedException {

    }


}
