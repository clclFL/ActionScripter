package club.pineclone.process.monitor;

import club.pineclone.process.api.Monitor;

public final class MonitorFactory {

    private MonitorFactory(){}

    public static Monitor newMouseMotionMonitor() {
        return new MouseMotionMonitorImpl();
    }

    public static Monitor newMouseActionMonitor() {
        return new MouseActionMonitorImpl();
    }

    public static Monitor newKeyActionMonitor() {
        return new KeyActionMonitorImpl();
    }

    public static Monitor newMonitorImpl() {
        return new MonitorImpl();
    }

}
