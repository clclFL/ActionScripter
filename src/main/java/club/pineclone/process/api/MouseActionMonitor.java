package club.pineclone.process.api;

import club.pineclone.process.action.ActionList;
import org.jnativehook.GlobalScreen;
import org.jnativehook.mouse.NativeMouseListener;
import org.jnativehook.mouse.NativeMouseWheelListener;

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
