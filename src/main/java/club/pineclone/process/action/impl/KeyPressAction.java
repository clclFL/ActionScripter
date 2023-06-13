package club.pineclone.process.action.impl;

import club.pineclone.process.action.interfaces.KeyAction;

import java.awt.*;

public class KeyPressAction extends KeyAction {

    public KeyPressAction(long delay, int keycode) {
        super(delay, keycode);
    }

    @Override
    public void act(Robot robot) {
        robot.keyPress(keycode);
    }
}
