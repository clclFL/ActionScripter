package club.pineclone.process.monitor.impl;

import club.pineclone.process.action.ActionFactory;
import club.pineclone.process.action.ActionList;
import club.pineclone.process.monitor.interfaces.MouseActionMonitor;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseWheelEvent;

import java.awt.event.InputEvent;
import java.util.HashMap;

public class MouseActionMonitorImpl extends MouseActionMonitor {

    private static final HashMap<Integer, Integer> BUTTONS_ID_MAP = new HashMap<>();

    static {
        BUTTONS_ID_MAP.put(1, InputEvent.BUTTON1_DOWN_MASK);
        BUTTONS_ID_MAP.put(2, InputEvent.BUTTON3_DOWN_MASK);
        BUTTONS_ID_MAP.put(3, InputEvent.BUTTON2_DOWN_MASK);
    }

    private long begin;

    @Override
    public void launch() {
        subscribe();
        actions.clear();
        begin = System.currentTimeMillis();
    }

    @Override
    public void setCanceled() {
        unsubscribe();
    }

    @Override
    public ActionList getActions() {
        return actions;
    }

    @Override
    public void nativeMouseClicked(NativeMouseEvent nativeMouseEvent) {
        //void
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent e) {
        actions.add(ActionFactory.mousePress(System.currentTimeMillis() - begin, BUTTONS_ID_MAP.get(e.getButton())));
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent e) {
        actions.add(ActionFactory.mouseRelease(System.currentTimeMillis() - begin, BUTTONS_ID_MAP.get(e.getButton())));
    }

    @Override
    public void nativeMouseWheelMoved(NativeMouseWheelEvent e) {
        actions.add(ActionFactory.mouseWheel(System.currentTimeMillis() - begin, e.getWheelRotation()));
    }

    @Override
    public String getName() {
        return "Mouse ActionMonitor Impl";
    }
}
