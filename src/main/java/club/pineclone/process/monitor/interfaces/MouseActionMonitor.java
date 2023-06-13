package club.pineclone.process.monitor.interfaces;

import club.pineclone.process.action.ActionList;
import club.pineclone.process.action.interfaces.MouseAction;
import org.jnativehook.GlobalScreen;
import org.jnativehook.mouse.NativeMouseListener;
import org.jnativehook.mouse.NativeMouseWheelListener;

import java.util.List;

public abstract class MouseActionMonitor
        extends Monitor
        implements NativeMouseListener, NativeMouseWheelListener {

    public void subscribe() {
        GlobalScreen.addNativeMouseListener(this);
        GlobalScreen.addNativeMouseWheelListener(this);
    }

    public void unsubscribe() {
        GlobalScreen.removeNativeMouseListener(this);
        GlobalScreen.removeNativeMouseWheelListener(this);
    }

    @Override
    public abstract ActionList getActions();
}
