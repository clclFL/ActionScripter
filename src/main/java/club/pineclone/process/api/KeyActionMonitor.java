package club.pineclone.process.api;

import club.pineclone.process.action.ActionList;
import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyListener;

public abstract class KeyActionMonitor
        extends Monitor
        implements NativeKeyListener {

    /**
     * This is the basic method for registering the key action listener.
     */
    public void subscribe() {
        GlobalScreen.addNativeKeyListener(this);
    }

    public void unsubscribe() {
        GlobalScreen.removeNativeKeyListener(this);
    }

    @Override
    public abstract ActionList getActions();
}
