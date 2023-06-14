package club.pineclone.process.action;

import club.pineclone.process.api.MouseAction;

import java.awt.*;

public class MouseWheelAction extends MouseAction {

    /**
     * This field should only be -1 or +1.
     */
    private final int rotation;

    public MouseWheelAction(long delay, int rotation) {
        super(delay);
        this.rotation = rotation;
    }

    @Override
    public void act(Robot robot) {
        robot.mouseWheel(rotation);
    }
}
