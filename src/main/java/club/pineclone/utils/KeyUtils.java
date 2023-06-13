package club.pineclone.utils;

import club.pineclone.api.CallBack;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.*;
import java.util.function.ToIntFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class KeyUtils {

    public enum KeyTag {
        BEGIN_RECORD(1, "Begin Record"),
        EXECUTE(2, "Begin Executing"),
        STOP(3, "Stop Progress");

        private final int identifier;
        private final String description;

        KeyTag(int identifier, String description) {
            this.identifier = identifier;
            this.description = description;
        }

        public int getIdentifier() {
            return this.identifier;
        }

        public String getDescription() {
            return this.description;
        }
    }

    private static final Map<String, Integer> STRING_KEYCODE_MAP = new HashMap<>();

    /**
     * The map storing keycode to their vk code, this will help transforming the data from pressing key to keystroke data.
     * This map will handler the first mapping, ***  from properties to vk_code, and from key listener to vk code. ***
     */
    private static final Map<Integer, Integer> BASIC_MAP = new HashMap<>();
    /**
     * This map storing the entry from vk_code to down_mask code, this map will handler the upper level mapping, which
     * should from vk_code to down_mask code, with this map it will be easy to construct the right key stroke.
     */
    private static final Map<Integer, Integer> UPPER_MAP = new HashMap<>();

    /**
     * This map will handler the problem with showing text, for the method {@link KeyUtils#toShowingText(KeyStroke)}
     * the text will not correct display if given specific vk code such as the vk_code of '[' , in the {@link KeyEvent}
     * this is {@link KeyEvent#VK_SLASH}, however, if we wanna use {@link GlobalListener}, this vk_code will not work
     * correctly, in the same time its keycode will help to fix this problem, which is 219, so this map will help to
     * build the connection between simple vk code to their text.
     */
    private static final Map<Integer, String> TEXT_MAP = new HashMap<>();
    /**
     * This strategy will transform those keycode from listener to the vk code, if simply use the keycode from listener
     * then some hotkey or shortcut will not work.
     */
    private static final ToIntFunction<Integer> TRANSFORM_STRATEGY = i -> {
        if (BASIC_MAP.containsKey(i))
            return BASIC_MAP.get(i);
        return i;
    };

    static {
        BASIC_MAP.put(91, 219); // [{
        BASIC_MAP.put(93, 221); // ]}
        BASIC_MAP.put(92, 220); // \|
        BASIC_MAP.put(45, 189); // -_
        BASIC_MAP.put(61, 187); // =+
        BASIC_MAP.put(59, 186); // ;:
        BASIC_MAP.put(222, 222); // '"
        BASIC_MAP.put(44, 188);  // <,
        BASIC_MAP.put(46, 190); // >.
        BASIC_MAP.put(47, 191); // /?
        //These were used for transform those modifier key to their vk code.
        BASIC_MAP.put(16, KeyEvent.VK_SHIFT);
        BASIC_MAP.put(17, KeyEvent.VK_CONTROL);
        BASIC_MAP.put(18, KeyEvent.VK_ALT);

        UPPER_MAP.put(KeyEvent.VK_ALT, KeyEvent.ALT_DOWN_MASK);
        UPPER_MAP.put(KeyEvent.VK_SHIFT, KeyEvent.SHIFT_DOWN_MASK);
        UPPER_MAP.put(KeyEvent.VK_CONTROL, KeyEvent.CTRL_DOWN_MASK);

        TEXT_MAP.put(219, "[{"); // [{
        TEXT_MAP.put(221, "]}"); // ]}
        TEXT_MAP.put(220, "\\|"); // \|
        TEXT_MAP.put(189, "-_"); // -_
        TEXT_MAP.put(187, "=+"); // =+
        TEXT_MAP.put(186, ";:"); // ;:
        TEXT_MAP.put(22, "'\""); // '"
        TEXT_MAP.put(188, "<,");  // <,
        TEXT_MAP.put(190, ">."); // >.
        TEXT_MAP.put(191, "/?"); // /?

        STRING_KEYCODE_MAP.put("Alt", KeyEvent.VK_ALT);
        STRING_KEYCODE_MAP.put("Shift", KeyEvent.VK_SHIFT);
        STRING_KEYCODE_MAP.put("Ctrl", KeyEvent.VK_CONTROL);
    }

    @Deprecated
    public static void reloadKeyConf() {
        FileUtils.getKeyBundle().save();
    }

    public static void resetHotkey(KeyTag key, KeyStroke keyStroke) {
        String keyProp = getPropFromHotkey(keyStroke);
        FileUtils.PropertiesBundle keyBundle = FileUtils.getKeyBundle();
        Object o = keyBundle.setProp(key.name(), keyProp);
        if (o == null) return;
        keyBundle.save();
    }

    public static void resetHotkey(Map<KeyTag, KeyStroke> map) {
        if (map.isEmpty()) return;
        FileUtils.PropertiesBundle keyBundle = FileUtils.getKeyBundle();
        map.forEach((k, v) -> keyBundle.setProp(k.name(), getPropFromHotkey(v)));
        keyBundle.save();
    }

    public static boolean checkIfHasRepeatHotkey(Map<KeyTag, KeyStroke> map) {
        if (map.isEmpty()) return false;
        Set<String> set = map.values().stream().map(KeyUtils::getPropFromHotkey).collect(Collectors.toSet());
        if (set.size() < map.size()) return true;
        if (map.size() < 3) {
            Set<KeyTag> keys = map.keySet();
            Set<KeyTag> allKeys = Arrays.stream(KeyTag.values()).collect(Collectors.toSet());
            allKeys.removeAll(keys);

            List<String> input = map.values().stream().map(KeyUtils::getPropFromHotkey).collect(Collectors.toList());
            List<String> exist = new ArrayList<>();
            allKeys.forEach(k -> exist.add(FileUtils.getKeyBundle().getProp(k.name())));

            for (String ex : exist) {
                for (String in : input) {
                    if (Objects.equals(in, ex)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean isModifierVKCode(int vkCode) {
        return UPPER_MAP.containsKey(vkCode);
    }

    private static KeyStroke getKeyStroke(int[] keycodes) {
        //Check the amount of given keycodes array, this array cannot be longer then three or shorter than one.
        int targetLen = keycodes.length;
        if (targetLen > 3 || targetLen < 1)
            throw new IllegalArgumentException("Cannot correctly handle the keycodes");
        //Check if all the keycode are in the right range.
        int[] vkCodes = new int[targetLen];
        for (int i = 0; i < targetLen; i++) {
            int keycode = keycodes[i];
            if (keycode < 0 || keycode > KeyEvent.KEY_LAST)
                throw new IllegalArgumentException("Wrong given keycode in the array with : " + keycode);
            if (BASIC_MAP.containsKey(keycode)) vkCodes[i] = fromKeycode2VKCode(keycode);
            vkCodes[i] = keycode;
        }

        switch (targetLen) {
            //if the given keycode array's length is simply one, then return the right keystroke.
            case 1 : {
                boolean flag1 = isModifierVKCode(vkCodes[0]);
                //if given keycode belongs to modifier keycode list, then return null keystroke.
                if (flag1) return getEmptyKeyStroke();
                else return getUnitHotkey(keycodes);
            }
            //if given keycode array's length is two, there is three situation we need to judge.
            case 2 : {
                int keycode1 = keycodes[0];
                int keycode2 = keycodes[1];
                boolean flag1 = isModifierVKCode(keycode1);
                boolean flag2 = isModifierVKCode(keycode2);
                //if keycode1 is not modifier, then set this to be keystroke, simply forget the second.
                if (!flag1) return getUnitHotkey(keycodes);
                //keycode1 is modifier keycode.
                if (flag2) return getEmptyKeyStroke();
                    //keycode1 is modifier and keycode2 is not.
                else return getBinaryHotkey(keycodes);
            }
            //if given keycode array's length is three, then the front tow has to be modifier key.
            case 3 : {
                int keycode1 = keycodes[0];
                int keycode2 = keycodes[1];
                int keycode3 = keycodes[2];
                if (!isModifierVKCode(keycode1)) return getUnitHotkey(keycodes);
                if (!isModifierVKCode(keycode2)) return getBinaryHotkey(keycodes);
                if (isModifierVKCode(keycode3)) return getEmptyKeyStroke();
                else return getTernaryHotkey(keycodes);
            }
            default : {
                return getEmptyKeyStroke();
            }
        }
    }

    private static KeyStroke getEmptyKeyStroke() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_UNDEFINED, 0, false);
    }

    private static int fromVKCode2Modifier(int vkCode) {
        return UPPER_MAP.get(vkCode);
    }

    private static int fromKeycode2VKCode(int keycode) {
        return BASIC_MAP.get(keycode);
    }

    private static String fromVKCode2Str(int vkCode) {
        return TEXT_MAP.get(vkCode);
    }

    private static KeyStroke getUnitHotkey(int[] vkCodes) {
        int keycode = vkCodes[0];
        return KeyStroke.getKeyStroke(keycode, 0);
    }

    /**
     * the vkCodes contains two elements, and the first is the modifier, this comes from the event
     * listener's new created list.
     */
    @SuppressWarnings("MagicConstant")
    private static KeyStroke getBinaryHotkey(int[] vkCodes) {
        int vkCode0 = vkCodes[1];
        int vkCode1 = vkCodes[0];
        return KeyStroke.getKeyStroke(vkCode0,
                fromVKCode2Modifier(vkCode1));
    }

    @SuppressWarnings("MagicConstant")
    private static KeyStroke getTernaryHotkey(int[] vkCodes) {
        int vkCode0 = vkCodes[2];
        int vkCode1 = vkCodes[0];
        int vkCode2 = vkCodes[1];
        return KeyStroke.getKeyStroke(vkCode0,
                fromVKCode2Modifier(vkCode1)
                        | fromVKCode2Modifier(vkCode2));
    }

    @Deprecated
    private static KeyStroke getKeyStrokeFromStr(String str) {
        int[] keycodes = Arrays.stream(str.split("\\+")).mapToInt(Integer::parseInt).toArray();
        return getKeyStroke(keycodes);
    }

    public void test(String[] args) {
        FileUtils.initializeFile();
//        initKeyMap();
        KeyStroke key = getHotkeyFromProp(KeyTag.BEGIN_RECORD);
//        System.out.println(keyStrokeToString(getKeyStroke(KeyTag.BEGIN_RECORD)));
        System.out.println(toShowingText(key));
    }


    public static KeyStroke getHotkeyFromProp(KeyTag key) {
        if (!FileUtils.hasInit()) return getEmptyKeyStroke();
        FileUtils.PropertiesBundle keyBundle = FileUtils.getKeyBundle();
        String value = keyBundle.getProp(key.name());
        if (value == null) {
            try {
                String keycodeCombo = FileUtils.DEF_HOTKEY_MAP.get(key);
                keyBundle.setProp(key.name(), keycodeCombo);
                keyBundle.save();
            } catch (RuntimeException e) {
                Log.info("Cannot finished reload configuration");
                throw e;
            }
        }
        int[] keycodes = Arrays.stream(value.split("\\+")).mapToInt(Integer::parseInt).toArray();
        return getKeyStroke(keycodes);
    }

    public static String getPropFromHotkey(KeyStroke hotkey) {
        int keyCode = hotkey.getKeyCode();
        int modifiers = hotkey.getModifiers();
        String modifiersText = KeyEvent.getModifiersExText(modifiers);
        if (modifiers == 0) return String.valueOf(keyCode);
        String[] split = modifiersText.split("\\+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            sb.append(STRING_KEYCODE_MAP.get(split[i]).toString()).append("+");
        }
        return sb.append(keyCode).toString();
    }

/*    public static String formatText(String text, int format) {
        if (text.length() > format) return text.substring(0, format);
        int space = format - text.length();
        int singleLen = space / 2;
        return " "*(singleLen) +
                text +
                " ".repeat(space % 2 == 0 ? singleLen : singleLen + 1);
    }*/

    public static JDialog getSetHotkeyDialog(JFrame owner, String text) {
        JDialog dialog = new JDialog(owner, "Setting Shortcut", true);
        dialog.setResizable(false);
        //Set dialog to mouse's position.
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 1));
        Image scaledInstance = GuiUtils.icon.getScaledInstance(50, 50, Image.SCALE_DEFAULT);
        JLabel icon = new JLabel(new ImageIcon(scaledInstance));
        JLabel info = new JLabel(text);
        infoPanel.add(icon);
        infoPanel.add(info);
        dialog.add(infoPanel, BorderLayout.CENTER);
//        dialog.addKeyListener(listener);
        dialog.pack();
        Point location = owner.getLocation();
        Point point = new Point(location.x + owner.getWidth() / 2, location.y + owner.getHeight() / 2);
        dialog.setLocation(point.x - dialog.getWidth() / 2, point.y - dialog.getHeight() / 2);
        return dialog;
    }

    @Deprecated
    public static JPanel getShortcutSettingEntry(Container owner, String text, JButton resetBut) {
        JPanel entryPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 1));
        entryPanel.setSize(new Dimension(owner.getWidth(), 40));
        JLabel textLabel = new JLabel(text);
        entryPanel.add(textLabel);
        entryPanel.add(resetBut);
        return entryPanel;
    }

    public static void main(String[] args) {
        KeyStroke key = KeyStroke.getKeyStroke(91, 0);
        System.out.println(key.getKeyCode());
    }

    public static String toShowingText(KeyStroke keyStroke) {
        if (keyStroke == null) return "";
        int code = keyStroke.getKeyCode();
        String keyText;
        if (TEXT_MAP.containsKey(code)) keyText = TEXT_MAP.get(code);
        else keyText = KeyEvent.getKeyText(code);
        String modifiers = KeyEvent.getModifiersExText(keyStroke.getModifiers());
        return modifiers + " " + keyText;
    }

    public void test02(String[] args) {

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setSize(new Dimension(400, 300));
            frame.setTitle("Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JButton button = new JButton("launch");
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 1));
            buttonPanel.add(button);
            button.addActionListener(new AbstractAction() {

                //as buffer
                final Set<Integer> pressedKeys = new LinkedHashSet<>();
                final Set<Integer> releasedKeys = new LinkedHashSet<>();

                @Override
                public void actionPerformed(ActionEvent e) {
                    //clear last store
                    pressedKeys.clear();
                    releasedKeys.clear();

                    JDialog dialog = getSetHotkeyDialog(frame, "press key to set short cut");
                    dialog.addKeyListener(new KeyAdapter() {
                        @Override
                        public void keyPressed(KeyEvent e) {
                            pressedKeys.add(e.getKeyCode());
                            if (pressedKeys.size() >= 3) {
                                int[] ints = pressedKeys.stream().mapToInt(TRANSFORM_STRATEGY).toArray();
                                KeyStroke keyStroke = KeyUtils.getKeyStroke(ints);
                                System.out.println(toShowingText(keyStroke));
                                System.out.println(getPropFromHotkey(keyStroke));
                                dialog.dispose();
                            }
                        }

                        @Override
                        public void keyReleased(KeyEvent e) {
                            releasedKeys.add(e.getKeyCode());
                            if (releasedKeys.size() == pressedKeys.size()) {
                                int[] ints = pressedKeys.stream().mapToInt(TRANSFORM_STRATEGY).toArray();
                                KeyStroke keyStroke = KeyUtils.getKeyStroke(ints);
                                System.out.println(toShowingText(keyStroke));
                                System.out.println(getPropFromHotkey(keyStroke));
                                dialog.dispose();
                            }
                        }
                    });
                    dialog.setVisible(true);
                }
            });

            frame.add(buttonPanel, BorderLayout.SOUTH);
            frame.setVisible(true);
        });
    }

    public static final KeyListener DEF_STRATEGY = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            System.out.println(e.paramString());
        }

        @Override
        public void keyReleased(KeyEvent e) {
            System.out.println(e.paramString());
        }
    };

    public static void launchKeyListener() {
        launchKeyListener(DEF_STRATEGY);
    }

    /**
     * This is the test method for check the pressing key's keycode.
     */
    public static void launchKeyListener(KeyListener listener) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setAlwaysOnTop(true);
            frame.setTitle("KeyInput Launcher");
            frame.setLocation(new Point(75, 75));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            Image scaledInstance = GuiUtils.icon.getScaledInstance(200, 200, Image.SCALE_DEFAULT);
            ImageIcon display = new ImageIcon(scaledInstance);
            JLabel pictureLabel = new JLabel(display);
            frame.setResizable(false);

            frame.add(pictureLabel, BorderLayout.CENTER);
            JButton launchAdapter = new JButton("Launch KeyAdapter");
            JButton launchNative = new JButton("Launch Native");
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 1));
            buttonPanel.add(launchAdapter);
            buttonPanel.add(launchNative);
            launchAdapter.setFocusPainted(false);
            frame.add(buttonPanel, BorderLayout.SOUTH);
            frame.pack();

            launchNative.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    GlobalKeyListener globalKeyListener = null;
                    try {
                        globalKeyListener = new GlobalKeyListener() {
                            @Override
                            public void nativeKeyPressed(NativeKeyEvent e) {
                                System.out.println(e.getKeyCode());
                            }

                            @Override
                            public void nativeKeyReleased(NativeKeyEvent e) {

                            }
                        };
                    } catch (NativeHookException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            launchAdapter.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JDialog dialog = new JDialog(frame, "Listener", true);
                    dialog.setSize(new Dimension(150, 150));
                    dialog.setResizable(false);

                    Image scaledInstance2 = GuiUtils.image_02.getScaledInstance(50, 50, Image.SCALE_DEFAULT);
                    ImageIcon display = new ImageIcon(scaledInstance2);
                    JLabel pictureLabel = new JLabel(display);
                    JLabel textLabel = new JLabel("Listening for input.");
                    JPanel listenerPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 1));
                    listenerPanel.add(pictureLabel);
                    listenerPanel.add(textLabel);
                    dialog.add(listenerPanel);

                    Point frameLoc = frame.getLocation();
                    Point point = new Point(frameLoc.x + frame.getWidth() / 2, frameLoc.y + frame.getHeight() / 2);

                    dialog.addKeyListener(listener);
                    dialog.pack();
                    dialog.setLocation(point.x - dialog.getWidth() / 2, point.y - dialog.getHeight() / 2);
                    dialog.setVisible(true);
                }
            });

            frame.setVisible(true);
        });
    }

    public static abstract class GlobalKeyListener implements NativeKeyListener {

        public GlobalKeyListener() throws NativeHookException {
            GlobalScreen.registerNativeHook();
            Logger.getLogger(GlobalScreen.class.getPackage().getName()).setLevel(Level.OFF);
            GlobalScreen.addNativeKeyListener(this);
        }

        public void remove() {
            GlobalScreen.removeNativeKeyListener(this);
        }

        @Override
        public void nativeKeyTyped(NativeKeyEvent e) {

        }

        @Override
        public abstract void nativeKeyPressed(NativeKeyEvent e);

        @Override
        public abstract void nativeKeyReleased(NativeKeyEvent e);
    }

    public static abstract class KeyCaptor extends KeyAdapter implements CallBack<KeyStroke> {

        private final Set<Integer> pressedKeys = new LinkedHashSet<>();
        private final Set<Integer> releasedKeys = new LinkedHashSet<>();

        @Override
        public void keyPressed(KeyEvent e) {
            try {
                pressedKeys.add(e.getKeyCode());
                if (pressedKeys.size() >= 3) {
                    int[] ints = pressedKeys.stream().mapToInt(TRANSFORM_STRATEGY).toArray();
                    callBack(getKeyStroke(ints));
                    reset();
                }
            } catch (Exception ex) {
                reset();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            try {
                releasedKeys.add(e.getKeyCode());
                if (releasedKeys.size() == pressedKeys.size()) {
                    int[] ints = pressedKeys.stream().mapToInt(TRANSFORM_STRATEGY).toArray();
                    callBack(getKeyStroke(ints));
                    reset();
                }
            } catch (Exception ex) {
                reset();
            }
        }

        /**
         * This method should be called in every time before using this class's instance to capture key shortcut.
         */
        private void reset() {
            pressedKeys.clear();
            releasedKeys.clear();
        }

        /**
         * This method will add a hook on the listener method, after the key is generated by the listener, this key instance
         * will simply be call back by the hook, the operation to the key can be defined in the hook, and notice that there
         * might better define some gui stuff in the hook, such as disposing some gui frame.
         */
        @Override
        public abstract void callBack(KeyStroke keyStroke);
    }

    /**
     * Use this method to simply create connection between an action with a keystroke, usually called a shortcut.
     *
     * @param component The component,  when client are focusing on upper window of this component, then the shortcut
     *                  will keep listening to user's operation.
     * @param keyStroke The shortcut which is going to be set combining with the given action.
     * @param action    The action to be executed while the corresponding keystroke is pressed.
     * @param tag       The unique identification distinguish this combination with the others.
     */
    public static void addShortcut(JComponent component, KeyStroke keyStroke, Action action, String tag) {
        InputMap inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(keyStroke, tag);
        ActionMap actionMap = component.getActionMap();
        actionMap.put(tag, action);
    }

}
