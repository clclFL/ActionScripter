package club.pineclone.process.monitor.impl;

import club.pineclone.process.action.ActionFactory;
import club.pineclone.process.action.ActionList;
import club.pineclone.process.monitor.interfaces.KeyActionMonitor;
import org.jnativehook.keyboard.NativeKeyEvent;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class KeyActionMonitorImpl extends KeyActionMonitor {

    /**
     * The map could transform keycode to vk code
     */
    private static final Map<Integer , Integer> KEYCODE_VK_CODE_MAP = new HashMap<>();
    static {
        //1 59 60 61 62 63 64 65 66 67 68 87 88 3666 3639 3667
        KEYCODE_VK_CODE_MAP.put(1 , KeyEvent.VK_ESCAPE);
        KEYCODE_VK_CODE_MAP.put(59 , KeyEvent.VK_F1);
        KEYCODE_VK_CODE_MAP.put(60 , KeyEvent.VK_F2);
        KEYCODE_VK_CODE_MAP.put(61 , KeyEvent.VK_F3);
        KEYCODE_VK_CODE_MAP.put(62 , KeyEvent.VK_F4);
        KEYCODE_VK_CODE_MAP.put(63 , KeyEvent.VK_F5);
        KEYCODE_VK_CODE_MAP.put(64 , KeyEvent.VK_F6);
        KEYCODE_VK_CODE_MAP.put(65 , KeyEvent.VK_F7);
        KEYCODE_VK_CODE_MAP.put(66 , KeyEvent.VK_F8);
        KEYCODE_VK_CODE_MAP.put(67 , KeyEvent.VK_F9);
        KEYCODE_VK_CODE_MAP.put(68 , KeyEvent.VK_F10);
        KEYCODE_VK_CODE_MAP.put(87 , KeyEvent.VK_F11);
        KEYCODE_VK_CODE_MAP.put(88 , KeyEvent.VK_F12);
        KEYCODE_VK_CODE_MAP.put(3666 , KeyEvent.VK_INSERT);
        KEYCODE_VK_CODE_MAP.put(3639 , KeyEvent.VK_PRINTSCREEN);
        KEYCODE_VK_CODE_MAP.put(3667 , KeyEvent.VK_DELETE);
        //`1234567890- : 41 2 3 4 5 6 7 8 9 10 11 12 13 14
        KEYCODE_VK_CODE_MAP.put(41 , 192); //~`
        KEYCODE_VK_CODE_MAP.put(2 , KeyEvent.VK_1);
        KEYCODE_VK_CODE_MAP.put(3 , KeyEvent.VK_2);
        KEYCODE_VK_CODE_MAP.put(4 , KeyEvent.VK_3);
        KEYCODE_VK_CODE_MAP.put(5 , KeyEvent.VK_4);
        KEYCODE_VK_CODE_MAP.put(6 , KeyEvent.VK_5);
        KEYCODE_VK_CODE_MAP.put(7 , KeyEvent.VK_6);
        KEYCODE_VK_CODE_MAP.put(8 , KeyEvent.VK_7);
        KEYCODE_VK_CODE_MAP.put(9 , KeyEvent.VK_8);
        KEYCODE_VK_CODE_MAP.put(10 , KeyEvent.VK_9);
        KEYCODE_VK_CODE_MAP.put(11 , KeyEvent.VK_0);
        KEYCODE_VK_CODE_MAP.put(12 , KeyEvent.VK_MINUS); //-_
        KEYCODE_VK_CODE_MAP.put(13 , KeyEvent.VK_EQUALS);
        KEYCODE_VK_CODE_MAP.put(14 , KeyEvent.VK_BACK_SPACE);
        //  qwertyuiop[]\ : 15 16 17 18 19 20 21 22 23 24 25 26 27 43
        KEYCODE_VK_CODE_MAP.put(15 , KeyEvent.VK_TAB);
        KEYCODE_VK_CODE_MAP.put(16 , KeyEvent.VK_Q);
        KEYCODE_VK_CODE_MAP.put(17 , KeyEvent.VK_W);
        KEYCODE_VK_CODE_MAP.put(18 , KeyEvent.VK_E);
        KEYCODE_VK_CODE_MAP.put(19 , KeyEvent.VK_R);
        KEYCODE_VK_CODE_MAP.put(20 , KeyEvent.VK_T);
        KEYCODE_VK_CODE_MAP.put(21 , KeyEvent.VK_Y);
        KEYCODE_VK_CODE_MAP.put(22 , KeyEvent.VK_U);
        KEYCODE_VK_CODE_MAP.put(23 , KeyEvent.VK_I);
        KEYCODE_VK_CODE_MAP.put(24 , KeyEvent.VK_O);
        KEYCODE_VK_CODE_MAP.put(25 , KeyEvent.VK_P);
        KEYCODE_VK_CODE_MAP.put(26 , KeyEvent.VK_OPEN_BRACKET);// [{
        KEYCODE_VK_CODE_MAP.put(27 , KeyEvent.VK_CLOSE_BRACKET);// ]}
        KEYCODE_VK_CODE_MAP.put(43 , KeyEvent.VK_BACK_SLASH);// \|
        //ASDFGHJKL;' : 58 30 31 32 33 34 35 36 37 38 39 40 28
        KEYCODE_VK_CODE_MAP.put(58 , KeyEvent.VK_CAPS_LOCK);
        KEYCODE_VK_CODE_MAP.put(30 , KeyEvent.VK_A);
        KEYCODE_VK_CODE_MAP.put(31 , KeyEvent.VK_S);
        KEYCODE_VK_CODE_MAP.put(32 , KeyEvent.VK_D);
        KEYCODE_VK_CODE_MAP.put(33 , KeyEvent.VK_F);
        KEYCODE_VK_CODE_MAP.put(34 , KeyEvent.VK_G);
        KEYCODE_VK_CODE_MAP.put(35 , KeyEvent.VK_H);
        KEYCODE_VK_CODE_MAP.put(36 , KeyEvent.VK_J);
        KEYCODE_VK_CODE_MAP.put(37 , KeyEvent.VK_K);
        KEYCODE_VK_CODE_MAP.put(38 , KeyEvent.VK_L);
        KEYCODE_VK_CODE_MAP.put(39 , KeyEvent.VK_SEMICOLON);// :;
        KEYCODE_VK_CODE_MAP.put(40 , KeyEvent.VK_QUOTE);// '"
        KEYCODE_VK_CODE_MAP.put(28 , KeyEvent.VK_ENTER);
        //ZXCVBNM,./ : 42 44 45 46 47 48 49 50 51 52 53 3638
        KEYCODE_VK_CODE_MAP.put(42 , KeyEvent.VK_SHIFT);
        KEYCODE_VK_CODE_MAP.put(44 , KeyEvent.VK_Z);
        KEYCODE_VK_CODE_MAP.put(45 , KeyEvent.VK_X);
        KEYCODE_VK_CODE_MAP.put(46 , KeyEvent.VK_C);
        KEYCODE_VK_CODE_MAP.put(47 , KeyEvent.VK_V);
        KEYCODE_VK_CODE_MAP.put(48 , KeyEvent.VK_B);
        KEYCODE_VK_CODE_MAP.put(49 , KeyEvent.VK_N);
        KEYCODE_VK_CODE_MAP.put(50 , KeyEvent.VK_M);
        KEYCODE_VK_CODE_MAP.put(51 , KeyEvent.VK_COMMA); // <,
        KEYCODE_VK_CODE_MAP.put(52 , KeyEvent.VK_PERIOD); // >.
            KEYCODE_VK_CODE_MAP.put(53 ,KeyEvent.VK_SLASH); // ?/
        KEYCODE_VK_CODE_MAP.put(3638 , KeyEvent.VK_SHIFT);
        // : 29 ?(Fn) 3675 56 57 56 29
        KEYCODE_VK_CODE_MAP.put(29 , KeyEvent.VK_CONTROL);
//        KEYCODE_VK_CODE_MAP.put(2002721 , KeyEvent.VK_FN);
        KEYCODE_VK_CODE_MAP.put(3675 , KeyEvent.VK_WINDOWS);
        KEYCODE_VK_CODE_MAP.put(56 , KeyEvent.VK_ALT);
        KEYCODE_VK_CODE_MAP.put(57 , KeyEvent.VK_SPACE);
//        KEYCODE_VK_CODE_MAP.put(56 , KeyEvent.VK_ALT);
//        KEYCODE_VK_CODE_MAP.put(29 , KeyEvent.VK_CONTROL);
        ///*- : 3655 3663 3657 3665 69 53 3639 3658
        KEYCODE_VK_CODE_MAP.put(3655 , KeyEvent.VK_HOME);
        KEYCODE_VK_CODE_MAP.put(3663 , KeyEvent.VK_END);
        KEYCODE_VK_CODE_MAP.put(3657 , KeyEvent.VK_PAGE_UP);
        KEYCODE_VK_CODE_MAP.put(3665 , KeyEvent.VK_PAGE_DOWN);
        KEYCODE_VK_CODE_MAP.put(69 , KeyEvent.VK_NUM_LOCK);
//        KEYCODE_VK_CODE_MAP.put(53 , KeyEvent.VK_/);
//        KEYCODE_VK_CODE_MAP.put(3639 , KeyEvent.VK_*);
        KEYCODE_VK_CODE_MAP.put(3658 , KeyEvent.VK_MINUS); // -
        //789+456123
        //0.         : 8 9 10 3662 5 6 7 2 3 4 28 11 83
        //up down left right : 57416 57424 57419 57421
        KEYCODE_VK_CODE_MAP.put(3662 , KeyEvent.VK_PLUS);
        KEYCODE_VK_CODE_MAP.put(83 , KeyEvent.VK_PERIOD);// .
        KEYCODE_VK_CODE_MAP.put(57416 , KeyEvent.VK_UP);
        KEYCODE_VK_CODE_MAP.put(57424 , KeyEvent.VK_DOWN);
        KEYCODE_VK_CODE_MAP.put(57419 , KeyEvent.VK_LEFT);
        KEYCODE_VK_CODE_MAP.put(57421 , KeyEvent.VK_RIGHT);
    }

    private long begin;

    @Override
    public void launch() {
        super.subscribe();
        actions.clear();
        begin = System.currentTimeMillis();
    }

    @Override
    public void setCanceled() {
        super.unsubscribe();
    }

    @Override
    public ActionList getActions() {
        return actions;
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
        //void
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        actions.add(ActionFactory.keyPress(System.currentTimeMillis() - begin,
                KEYCODE_VK_CODE_MAP.get(e.getKeyCode())));
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        actions.add(ActionFactory.keyRelease(System.currentTimeMillis() - begin,
                KEYCODE_VK_CODE_MAP.get(e.getKeyCode())));
    }

    @Override
    public String getName() {
        return "KeyTag Action MonitorImpl";
    }
}
