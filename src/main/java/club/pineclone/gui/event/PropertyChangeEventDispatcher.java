package club.pineclone.gui.event;

import club.pineclone.concurrent.GuiThreadPool;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class PropertyChangeEventDispatcher {

    private final List<PropertyChangeListener> listeners = new LinkedList<>();

    public void subscribe(Supplier<PropertyChangeListener> listener) {
        listeners.add(listener.get());
    }

    public void dispatch(PropertyChangeEvent e) {
        CompletableFuture.runAsync(() -> this.listeners.parallelStream().forEach(
                l -> l.propertyChange(e)), GuiThreadPool.getDefPool());
    }
}
