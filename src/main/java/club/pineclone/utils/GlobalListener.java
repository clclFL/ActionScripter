package club.pineclone.utils;

import club.pineclone.api.Pair;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;

/**
 * This class will listen to client's input key and do the right action.
 */
public class GlobalListener {

    /**
     * The unique signature for each hotkey-action entry.
     */
    private int identifier = 0;

    private final JIntellitype instance = JIntellitype.getInstance();
    private final HashMap<Integer , Pair<KeyStroke , ActionListener>> actionMap = new HashMap<>();
    private final ActionEvent defAction = new ActionEvent(this, 0, "cmd");

    private final HotkeyListener hotkeyListener = identifier -> actionMap.get(identifier).snd.actionPerformed(defAction);

    public GlobalListener() {
    }

    public void launch() {
        instance.addHotKeyListener(hotkeyListener);
    }

    public void setCanceled() {
        while (identifier >= 0) {
            instance.unregisterHotKey(identifier);
            identifier --;
        }
        identifier = 0;
        instance.removeHotKeyListener(hotkeyListener);

    }

    public void rollBack() {
        actionMap.forEach((i , p) -> {
            instance.registerHotKey(identifier , p.fst.getModifiers() , p.fst.getKeyCode());
            identifier ++;
        });
    }

    public void putAction(KeyStroke stroke , ActionListener action) {
        instance.registerHotKey(identifier , stroke.getModifiers() , stroke.getKeyCode());
        actionMap.put(identifier , new Pair<>(stroke , action));
        identifier ++;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ImageIcon image = new ImageIcon(GuiUtils.icon.getScaledInstance(250, 250, Image.SCALE_DEFAULT));
            KeyStroke keyStroke = KeyStroke.getKeyStroke(219, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK);
            JLabel label = new JLabel(image);

//            InputMap labelInputMap = label.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
//            ActionMap labelActionMap = label.getActionMap();
//            labelInputMap.put(keyStroke , "test01");
            AbstractAction action = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("The keystroke has been pressed!");
                }
            };
//            labelActionMap.put("test01", action);

            GlobalListener globalListener = new GlobalListener();
            globalListener.putAction(keyStroke , action);
            globalListener.launch();

            frame.getContentPane().add(label);
            frame.pack();
            Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setLocation((size.width - frame.getWidth()) / 2 , (size.height - frame.getHeight()) / 2);
            frame.setVisible(true);
        });

        while (true) {
        }
    }

}