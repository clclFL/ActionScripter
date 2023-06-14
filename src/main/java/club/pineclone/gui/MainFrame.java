package club.pineclone.gui;

import club.pineclone.concurrent.GuiThreadPool;
import club.pineclone.gui.api.Registry;
import club.pineclone.gui.registry.FunctionalPanel;
import club.pineclone.gui.context.MainFrameCtx;
import club.pineclone.gui.event.PropertyChangeEventDispatcher;
import club.pineclone.gui.registry.FunctionalPanelRegistryHandler;
import club.pineclone.utils.FileUtils;
import club.pineclone.utils.GuiUtils;
import club.pineclone.utils.Log;
import club.pineclone.utils.i18n.LocTag;
import club.pineclone.utils.i18n.LocaleUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class MainFrame extends JFrame {

    private final JTabbedPane tabbed;
    private final MainFrameCtx ctx;
    private final MainSettingMenu mainSettingMenu;
    private final FunctionalPanelRegistryHandler register;

    private boolean alwaysOnTop;
    private boolean allowBeep;
    private boolean enableInfoPanel;

    private JMenu configMenu;
    private JMenuItem setting;
    private JMenuItem setAlwaysOnTop;
    private JMenu preferences;
    private JMenuItem setUsingFont;
    private JMenuItem setAllowBeep;

    private JMenu languageMenu;
    private JMenu selectLang;
    private JMenuItem loadAvailableLang;
    private JMenuItem openLangDir;

    private JMenu savedMenu;
    private JMenuItem openScriptDir;
    private JMenuItem resetSavingLocation;
    private JMenuItem openSystemConfig;

    private JMenu helpMenu;
    private JMenuItem usingGuidance;
    private JMenuItem submitProblem;
    private JMenuItem enableExtensionPanel;

    /**
     * This method should be called at somewhere correctly.
     * em actually later those component will be added into a list to execute the flush method.
     */
    private class MenuBarListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals("language_change")) {
                //update the text in the component.
                configMenu.setText(LocaleUtils.loc(LocTag.CONFIG_MENU));
                setting.setText(LocaleUtils.loc(LocTag.SETTING));
                savedMenu.setText(LocaleUtils.loc(LocTag.SAVED_MENU));
                helpMenu.setText(LocaleUtils.loc(LocTag.HELP_MENU));

                setAlwaysOnTop.setText(LocaleUtils.loc(LocTag.SET_ALWAYS_ON_TOP));

                preferences.setText(LocaleUtils.loc(LocTag.PREFERENCES));
                setAllowBeep.setText(allowBeep ? LocaleUtils.loc(LocTag.SET_MUTE_BEEP) :
                        LocaleUtils.loc(LocTag.SET_ALLOW_BEEP));
                setUsingFont.setText(LocaleUtils.loc(LocTag.SET_USING_FONT));
                languageMenu.setText(LocaleUtils.loc(LocTag.LANGUAGE));
                selectLang.setText(LocaleUtils.loc(LocTag.SELECT_LANG));
                loadAvailableLang.setText(LocaleUtils.loc(LocTag.LOAD_AVAILABLE_LANG));
                openLangDir.setText(LocaleUtils.loc(LocTag.OPEN_LANG_DIR));

                openScriptDir.setText(LocaleUtils.loc(LocTag.OPEN_OPERATION_DIR));
                resetSavingLocation.setText(LocaleUtils.loc(LocTag.RESET_SAVING_LOCATION));
                openSystemConfig.setText(LocaleUtils.loc(LocTag.OPEN_SYSTEM_CONFIG));
                usingGuidance.setText(LocaleUtils.loc(LocTag.USING_GUIDANCE));
                submitProblem.setText(LocaleUtils.loc(LocTag.SUBMIT_PROBLEM));
                enableExtensionPanel.setText(LocaleUtils.loc(LocTag.ENABLE_EXTENSION_INFO_PANEL));
            }
        }
    }

    private class ExtendListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals("language_change")) {
                setTitle(LocaleUtils.loc(LocTag.MAIN_TITLE) + (isAlwaysOnTop() ? LocaleUtils.loc(LocTag.LOCK) : ""));
            }
        }
    }

    public void setAllowBeep(boolean flag) {
        this.allowBeep = flag;
    }

    public void setEnableInfoPanel(boolean flag) {
        this.enableInfoPanel = flag;
    }

    public boolean setAllowBeep() {
        return allowBeep;
    }

    public String getMainTitle() {
        return LocaleUtils.loc(LocTag.MAIN_TITLE);
    }

    public void perform(String msg) {

    }

    public MainFrame() {
        ctx = new MainFrameCtx(this);
        setSize(new Dimension(470, 340));
        mainSettingMenu = new MainSettingMenu(this);
        tabbed = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);
        register = new FunctionalPanelRegistryHandler();

        FileUtils.PropertiesBundle sysBundle = FileUtils.getSysBundle();
        PropertyChangeEventDispatcher dispatcher = ctx.getDispatcher();

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocation(GuiUtils.getLastShutDownPoint());
        this.setIconImage(GuiUtils.icon);
        this.setResizable(false);

        this.alwaysOnTop = Boolean.parseBoolean(
                sysBundle.getProp(FileUtils.SysTag.SET_ALLOW_ON_TOP.name()));
        this.enableInfoPanel = Boolean.parseBoolean(
                sysBundle.getProp(FileUtils.SysTag.ENABLE_INFO_PANEL.name()));

        this.setAlwaysOnTop(alwaysOnTop);
        this.allowBeep = Boolean.parseBoolean(sysBundle.getProp(FileUtils.SysTag.ALLOW_BEEP.name()));

        //this is for store the last setting about if the window stick on the top is allowed.
        this.setTitle(getMainTitle() + (alwaysOnTop ? LocaleUtils.loc(LocTag.LOCK) : ""));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                sysBundle.setProp(FileUtils.SysTag.SET_ALLOW_ON_TOP.name(), String.valueOf(alwaysOnTop));
                sysBundle.setProp(FileUtils.SysTag.ALLOW_BEEP.name(), String.valueOf(allowBeep));
                sysBundle.save();
                GuiUtils.loadShutDownPoint(MainFrame.this);
                MainFrame.this.dispose();
                System.exit(0);
            }
        });
    }

    /**
     * Finial Step of initialization.
     */
    public void postDoneInitializationEvent() {
        PropertyChangeEventDispatcher dispatcher = ctx.getDispatcher();
        dispatcher.dispatch(new PropertyChangeEvent(dispatcher, "language_change"
                , null, LocaleUtils.getBundle().getLocale()));
    }

    /**
     * First step of initialization, this step will initial the menu bar to keep the menu bar is in right
     * structure.
     */
    public void initializeMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        configMenu = new JMenu();
        savedMenu = new JMenu();
        helpMenu = new JMenu();

        //The param decide if the window could stay on the top before shutdown.
        setAlwaysOnTop = new JMenuItem();
        setting = new JMenuItem();
        //set using font. (this is weired
        setUsingFont = new JMenuItem();

        //set using language. this will allow user to set to their using language.
        languageMenu = new JMenu();
        selectLang = new JMenu();
        loadAvailableLang = new JMenuItem();
        openLangDir = new JMenuItem();

        languageMenu.add(selectLang);
        languageMenu.add(loadAvailableLang);
        languageMenu.add(openLangDir);

        //set if beep is allow, in some place the beep is used for notice the client.
        setAllowBeep = new JMenuItem();

        //open the directory where store the operation.
        openScriptDir = new JMenuItem();

        //reset the directory for saving the recording action.
        resetSavingLocation = new JMenuItem();

        //open the system configuration, this conf is usually for store the setting for software itself.
        openSystemConfig = new JMenuItem();

        //enable to show up an extension panel to show more information.
        enableExtensionPanel = new JMenuItem();

        //open a window show help to client.
        usingGuidance = new JMenuItem();

        //in this page client could send problem to the author.
        submitProblem = new JMenuItem();
        preferences = new JMenu();

        configMenu.add(setAlwaysOnTop);
        configMenu.add(enableExtensionPanel);
        configMenu.add(preferences);
        configMenu.add(setting);

        preferences.add(setAllowBeep);
        preferences.add(setUsingFont);

        savedMenu.add(openScriptDir);
        savedMenu.add(resetSavingLocation);
        savedMenu.add(openSystemConfig);

        helpMenu.add(usingGuidance);
        helpMenu.add(submitProblem);

        menuBar.add(configMenu);
        menuBar.add(savedMenu);
        menuBar.add(languageMenu);
        menuBar.add(helpMenu);
        add(menuBar, BorderLayout.NORTH);
    }

    /**
     * The forth step of initialization, this step will add the essential listener to the dispatcher.
     */
    public void registryPropertyChangeEventSubscriber() {
        /* for event */
        PropertyChangeEventDispatcher dispatcher = ctx.getDispatcher();
        dispatcher.subscribe(MainFrame.MenuBarListener::new);
        dispatcher.subscribe(MainFrame.ExtendListener::new);
    }

    /**
     * The third step of initialization, registry functional panels into the main frame.
     */
    public void initializingFunctionalPanel() {
        if (tabbed.getTabCount() > 0) tabbed.setSelectedIndex(0);
        getContentPane().add(tabbed, BorderLayout.CENTER);
    }

    /**
     * The second step of initialization, adding action listener to the menu items.
     */
    public void addActionListenerForMenuItem() {

        FileUtils.PropertiesBundle sysBundle = FileUtils.getSysBundle();
        PropertyChangeEventDispatcher dispatcher = ctx.getDispatcher();

        setAlwaysOnTop.addActionListener(e -> {
            alwaysOnTop = !alwaysOnTop;
            String mainTitle = getMainTitle();
            String displayTitle = alwaysOnTop ? mainTitle + LocaleUtils.loc(LocTag.LOCK) : mainTitle;
            setTitle(displayTitle);
            setAlwaysOnTop(alwaysOnTop);
        });

        final List<JMenuItem> langItems = new LinkedList<>();
        Consumer<Locale> forEachLocale = l -> {
            String localStr = l.toString();
            JMenuItem item = new JMenuItem(localStr);
            item.setName(localStr);
            langItems.add(item);

            ActionListener forEachItem = e -> {
                //set using language
                LocaleUtils.updateBundle(l, dispatcher);
                item.setEnabled(false);
                langItems.forEach(i -> {
                    if (item != i) {
                        i.setEnabled(true);
                    }
                });
                sysBundle.setProp(FileUtils.SysTag.USING_LANGUAGE.name(), localStr);
                sysBundle.save();
            };

            item.addActionListener(forEachItem);
            selectLang.add(item);
        };

        LocaleUtils.getAvailableLocales().forEach(forEachLocale);

        loadAvailableLang.addActionListener(e -> {
            selectLang.removeAll();
            langItems.clear();
            LocaleUtils.getAvailableLocales().forEach(forEachLocale);
            langItems.forEach(i -> {
                if (i.getName().equals(LocaleUtils.getLocale().toString())) {
                    i.setEnabled(false);
                }
            });
        });

        langItems.forEach(i -> {
            if (i.getName().equals(LocaleUtils.getLocale().toString())) {
                i.setEnabled(false);
            }
        });

        openLangDir.addActionListener(e -> {
            CompletableFuture.runAsync(() -> {
                File folder = FileUtils.L10N_DIR;
                if (folder.exists() && folder.isDirectory()) {
                    try {
                        Desktop.getDesktop().open(folder);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }, GuiThreadPool.getDefPool());
        });

        setAllowBeep.addActionListener(e -> {
            if (!allowBeep) Toolkit.getDefaultToolkit().beep();
            setAllowBeep(!allowBeep);
            setAllowBeep.setText(allowBeep ? LocaleUtils.loc(LocTag.SET_MUTE_BEEP) :
                    LocaleUtils.loc(LocTag.SET_ALLOW_BEEP));
        });

        openScriptDir.addActionListener(e -> {
            CompletableFuture.runAsync(() -> {
                try {
                    String folderPath = sysBundle.getProp(FileUtils.SysTag.DIR_FOR_SCRIPTS.name());
                    File folder = new File(folderPath);
                    if (folder.exists() && folder.isDirectory()) {
                        Desktop.getDesktop().open(folder);
                    } else {
//                            String info1 = "Cannot find custom configuration directory, trying complete the directory";
                        String info1 = LocaleUtils.loc(LocTag.OPEN_SCRIPT_DIR_FAIL);
                        Log.info(info1);
                        perform(info1);
                        FileUtils.initializeFile();
                    }
                } catch (IOException ex) {
//                        String info1 = "Exception occurs while trying open custom configuration";
                    String info1 = LocaleUtils.loc(LocTag.OPEN_SCRIPT_DIR_SUCCESSFULLY);
                    perform(info1);
                    Log.infoExceptionally(info1, ex);
                }
            }, GuiThreadPool.getDefPool());
        });

        resetSavingLocation.addActionListener(e -> CompletableFuture.runAsync(() -> {
            JFileChooser folderChooser = new JFileChooser();
            folderChooser.setDialogTitle(LocaleUtils.loc(LocTag.RESET_SAVING_LOCATION_DIALOG_TITLE));
            folderChooser.setMultiSelectionEnabled(false);
            folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = folderChooser.showOpenDialog(MainFrame.this);
            //when client choose a file correctly.
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = folderChooser.getSelectedFile();
                String absolutePath = selectedFile.getAbsolutePath();
                perform(LocaleUtils.loc(LocTag.RESET_SAVING_LOCATION_POST_PERFORM) + absolutePath);
                sysBundle.setProp(FileUtils.SysTag.DIR_FOR_SCRIPTS.name(), absolutePath);
                sysBundle.save();
            }
        }, GuiThreadPool.getDefPool()));

        openSystemConfig.addActionListener(e -> CompletableFuture.runAsync(() -> {
            File sysConf = FileUtils.SYS_CONF;
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (sysConf.exists()) {
                    try {
                        desktop.open(sysConf);
                    } catch (IOException ex) {
                        Log.infoExceptionally("Cannot open System conf", ex);
                    }
                } else {
                    Log.info("Cannot find System conf, trying recreating");
                    FileUtils.initializeFile();
                }
            }
        }, GuiThreadPool.getDefPool()));

        enableExtensionPanel.addActionListener(e -> {
            setEnableInfoPanel(!enableInfoPanel);
            enableExtensionPanel.setText(enableInfoPanel ? LocaleUtils.loc(LocTag.DISABLE_EXTENSION_INFO_PANEL) :
                    LocaleUtils.loc(LocTag.ENABLE_EXTENSION_INFO_PANEL));
        });

        setting.addActionListener(e -> {
            mainSettingMenu.setLocation(GuiUtils.getRelativeCenter(mainSettingMenu, MainFrame.this));
            mainSettingMenu.setVisible(true);
        });
    }

    public void addFunctionalPanel(FunctionalPanel panel) {
        this.tabbed.addTab("" , panel.getIcon() , panel , panel.getTip());
    }

    public MainSettingMenu getMainSettingMenu() {
        return mainSettingMenu;
    }

    public Registry<FunctionalPanel> getRegister() {
        return register;
    }

    public MainFrameCtx getContext() {
        return ctx;
    }

    public static void main(String[] args) {

        try {
            Class.forName("club.pineclone.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame mainFr = new MainFrame();
            mainFr.setVisible(true);
        });
    }

}
