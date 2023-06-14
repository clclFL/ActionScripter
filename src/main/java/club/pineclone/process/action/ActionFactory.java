package club.pineclone.process.action;

import java.awt.*;

public final class ActionFactory {

    private ActionFactory(){}

    public static KeyPressAction keyPress(long delay, int buttons) {
        return new KeyPressAction(delay, buttons);
    }

    public static KeyReleaseAction keyRelease(long delay, int buttons) {
        return new KeyReleaseAction(delay, buttons);
    }

    public static MouseMoveAction mouseMove(long delay, Point to) {
        return new MouseMoveAction(delay, to);
    }

    public static MousePressAction mousePress(long delay, int buttons) {
        return new MousePressAction(delay , buttons);
    }

    public static MouseReleaseAction mouseRelease(long delay, int buttons) {
        return new MouseReleaseAction(delay , buttons);
    }

    public static MouseWheelAction mouseWheel(long delay, int rotation) {
        return new MouseWheelAction(delay , rotation);
    }

}
