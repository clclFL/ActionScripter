package club.pineclone.process.action;

import club.pineclone.process.api.MouseAction;

import java.awt.*;

public class MousePressAction extends MouseAction {

    /**
     * The buttons are the key to the mouse, usually the
     */
    protected final int buttons;

    public MousePressAction(long delay, int buttons) {
        super(delay);
        this.buttons = buttons;
    }

    @Override
    public void act(Robot robot) {
        robot.mousePress(buttons);
    }
}
