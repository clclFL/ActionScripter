package club.pineclone.process.action.impl;

import club.pineclone.process.action.interfaces.MouseAction;

import java.awt.*;

/**
 * The basic class of mouse moving, this class define an action about the way how client's
 * mouse moving.
 */
public class MouseMoveAction extends MouseAction {

    protected final Point to;

    public MouseMoveAction(long delay, Point to) {
        super(delay);
        this.to = to;
    }

    @Override
    public void act(Robot robot) {
        robot.mouseMove(to.x , to.y);
    }
}
