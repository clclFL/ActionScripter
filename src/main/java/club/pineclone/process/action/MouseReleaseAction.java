package club.pineclone.process.action;

import club.pineclone.process.api.MouseAction;

import java.awt.*;

public class MouseReleaseAction extends MouseAction {

    protected final int buttons;

    public MouseReleaseAction(long delay, int buttons) {
        super(delay);
        this.buttons = buttons;
    }

    @Override
    public void act(Robot robot) {
        robot.mouseRelease(buttons);
    }
}
