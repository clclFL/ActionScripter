package club.pineclone.process.action.interfaces;

public abstract class KeyAction extends RobotAction {

    protected final int keycode;

    public KeyAction(long delay, int keycode) {
        super(delay);
        this.keycode = keycode;
    }

}
