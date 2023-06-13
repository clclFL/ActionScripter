package club.pineclone.process.monitor;

import club.pineclone.process.monitor.impl.KeyActionMonitorImpl;
import club.pineclone.process.monitor.impl.MonitorImpl;
import club.pineclone.process.monitor.impl.MouseActionMonitorImpl;
import club.pineclone.process.monitor.impl.MouseMotionMonitorImpl;
import club.pineclone.process.monitor.interfaces.Monitor;

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
