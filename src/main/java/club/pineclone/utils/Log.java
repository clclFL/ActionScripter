package club.pineclone.utils;

import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {

    private Log(){}

    private static final Logger LOGGER = Logger.getLogger("PineCloneRobot");

    public static void log(Level level, String info) {
        LOGGER.log(level , info);
    }

    public static void info(String info) {
        LOGGER.info(info);
    }

    public static void infoExceptionally(String info, Exception e) {
        LOGGER.info(info);
        e.printStackTrace();
    }

    public static void infoExceptionally(String info, Exception e, Consumer<Exception> consumer) {
        LOGGER.info(info);
        consumer.accept(e);
    }

    public static void infoThenThrow(String info, Exception e) {
        LOGGER.info(info);
        try {
            throw e;
        } catch (Exception ex) {
            throw new RuntimeException(e);
        }
    }

    public static void infoThenRun(String info, Runnable runnable) {
        LOGGER.info(info);
        new Thread(runnable).start();
    }

    public static void conf(String conf) {
        LOGGER.config(conf);
    }

    public static void warn(String warn) {
        LOGGER.warning(warn);
    }

    public static void fine(String fine) {
        LOGGER.fine(fine);
    }
}
