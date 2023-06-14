package club.pineclone.process.action;

import club.pineclone.process.api.KeyAction;

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
