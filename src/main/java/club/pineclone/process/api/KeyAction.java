package club.pineclone.process.api;

public abstract class KeyAction extends RobotAction {

    protected final int keycode;

    public KeyAction(long delay, int keycode) {
        super(delay);
        this.keycode = keycode;
    }

}
