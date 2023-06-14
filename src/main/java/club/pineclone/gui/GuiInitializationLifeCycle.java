package club.pineclone.gui;

import club.pineclone.gui.api.Registrable;
import club.pineclone.gui.api.Registry;
import club.pineclone.gui.event.PropertyChangeEventDispatcher;
import club.pineclone.gui.registry.FunctionalPanel;
import club.pineclone.gui.registry.FunctionalPanelRegistryHandler;
import club.pineclone.gui.registry.SettingPanel;
import club.pineclone.gui.registry.SettingPanelRegistryHandler;
import club.pineclone.gui.settingPanel.LaunchSettingPanel;
import club.pineclone.gui.settingPanel.MainSettingPanel;
import club.pineclone.utils.Log;

import javax.swing.*;

public class GuiInitializationLifeCycle {

    private static MainFrame frame;

    public static void loadFile() {
        try {
            Class.forName("club.pineclone.Driver");
        } catch (ClassNotFoundException e) {
            Log.infoExceptionally("Cannot correctly initializing the file." , e);
        }
    }

    public static void launchMainFrame() {
        SwingUtilities.invokeLater(() -> {
            frame = new MainFrame();
            frame.initializeMenuBar();
            frame.addActionListenerForMenuItem();
            registryFunctionalPanel();
            frame.initializingFunctionalPanel();
            registrySettingPanel();
            frame.getMainSettingMenu().initializingSettingPanel();
            frame.registryPropertyChangeEventSubscriber();
            frame.postDoneInitializationEvent();
            frame.setVisible(true);
        });
    }

    public static void registryFunctionalPanel() {
        Registry<FunctionalPanel> register = frame.getRegister();
        register.registerAll();
    }

    public static void registrySettingPanel() {
        MainSettingMenu main = frame.getMainSettingMenu();
        Registry<SettingPanel> register = main.getRegister();
        register.addToRegistryList(new MainSettingPanel(main, 0));
        register.addToRegistryList(new LaunchSettingPanel(main, 1));
        register.registerAll();
    }

    public static void main(String[] args) {
        loadFile();
        launchMainFrame();
    }

}
