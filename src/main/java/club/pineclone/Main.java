package club.pineclone;

import club.pineclone.gui.functionalPanel.LaunchPanel;
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
                LaunchPanel _mainFrame = new LaunchPanel();
                SwingUtilities.invokeLater(() -> _mainFrame.setVisible(true));
            } catch (AWTException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
