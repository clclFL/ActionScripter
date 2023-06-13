package club.pineclone.process.executor;

import club.pineclone.process.executor.impl.ExecutorImpl;
import club.pineclone.process.executor.impl.KeyExecutorImpl;
import club.pineclone.process.executor.impl.MouseExecutorImpl;
import club.pineclone.process.executor.interfaces.Executor;

import java.awt.*;

public final class ExecutorFactory {

    private ExecutorFactory(){}

    public static Executor newMouseActionExecutor() throws AWTException {
        return new MouseExecutorImpl();
    }

    public static Executor newKeyActionExecutor() throws AWTException {
        return new KeyExecutorImpl();
    }

    public static Executor newExecutorImpl() throws AWTException {
        return new ExecutorImpl();
    }

}
