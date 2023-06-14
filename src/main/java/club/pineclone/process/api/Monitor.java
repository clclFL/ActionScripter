package club.pineclone.process.api;

import club.pineclone.process.action.ActionList;
import club.pineclone.utils.Log;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Monitor {

    protected final ActionList actions = new ActionList();

    static {
        Logger.getLogger(GlobalScreen.class.getPackage().getName()).setLevel(Level.OFF);
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            Log.infoExceptionally("Exception occur while registry native hook, monitor may not work." , e);
        }
    }

    public abstract void launch();

    public abstract void setCanceled();

    public abstract ActionList getActions();

    /**
     * This method should be called if the monitor is a listener, which means this monitor
     * has to be registry into nation listener, then make sure this method is called and remove
     * this instance from nation listener in a right place.
     */
    protected abstract void subscribe();

    /**
     * This method is used for removing a monitor from registry list, if any monitor should be
     * stopped and not used anymore, then this method should be called to stop the monitor.
     */
    protected abstract void unsubscribe();

    public final boolean hasActions() {
        return !this.actions.isEmpty();
    }

    public abstract String getName();

}
