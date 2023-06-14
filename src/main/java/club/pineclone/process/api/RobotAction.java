package club.pineclone.process.api;

import java.awt.*;
import java.io.Serializable;

/**
 * The basic interface of action, this class's instance will then be submitted to
 * an executor and then run.
 */
public abstract class RobotAction implements Serializable {

    protected final long delay;

    public RobotAction(long delay) {
        this.delay = delay;
    }

    /**
     * This method will run the action toward the event instance.
     */
    public abstract void act(Robot robot);

    public long getDelay() {
        return delay;
    }
}
