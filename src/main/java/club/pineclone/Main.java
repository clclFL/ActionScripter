package club.pineclone;

import club.pineclone.gui.MainFrame;
import club.pineclone.utils.Log;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) throws ClassNotFoundException {

        SwingUtilities.invokeLater(() -> {
            try {
                Class.forName("club.pineclone.Driver");
            } catch (ClassNotFoundException e) {
                Log.infoExceptionally("Cannot finish file initialization" , e);
                return;
            }

            try {
                MainFrame _mainFrame = new MainFrame();
                SwingUtilities.invokeLater(() -> _mainFrame.setVisible(true));
            } catch (AWTException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
