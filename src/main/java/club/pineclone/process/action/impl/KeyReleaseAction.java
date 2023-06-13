package club.pineclone.process.action.impl;

import club.pineclone.process.action.interfaces.KeyAction;

import java.awt.*;

public class KeyReleaseAction extends KeyAction {

    public KeyReleaseAction(long delay, int keycode) {
        super(delay, keycode);
    }

    @Override
    public void act(Robot robot) {
        robot.keyRelease(keycode);
    }
}
