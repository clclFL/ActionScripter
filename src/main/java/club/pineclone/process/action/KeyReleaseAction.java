package club.pineclone.process.action;

import club.pineclone.process.api.KeyAction;

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
