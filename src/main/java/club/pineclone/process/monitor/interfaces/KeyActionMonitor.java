package club.pineclone.process.monitor.interfaces;

import club.pineclone.process.action.ActionList;
import club.pineclone.process.action.interfaces.KeyAction;
import club.pineclone.process.action.interfaces.RobotAction;
import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.List;

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
