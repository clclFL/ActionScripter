package club.pineclone.gui;

import club.pineclone.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    static {
        try {
            Class.forName("club.pineclone.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public MainFrame() {
        this.setSize(new Dimension(470, 340));
        this.setTitle("Title");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JTabbedPane tabbed = new JTabbedPane(JTabbedPane.LEFT,
                JTabbedPane.SCROLL_TAB_LAYOUT);

        JPanel panel1 = new JPanel();
        tabbed.addTab("panel1", panel1);

        JPanel panel2 = new JPanel();
        tabbed.addTab("panel2", panel2);
        this.add(tabbed);

        tabbed.setTabComponentAt(0, new JLabel(new ImageIcon(GuiUtils.launch
                .getScaledInstance(24 , 24 , Image.SCALE_DEFAULT))));
        tabbed.setTabComponentAt(1, new JLabel(new ImageIcon(GuiUtils.wrench
                .getScaledInstance(24 , 24 , Image.SCALE_DEFAULT))));

        tabbed.setSelectedIndex(0);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
