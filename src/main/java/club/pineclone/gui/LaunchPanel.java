package club.pineclone.gui;

import club.pineclone.concurrent.GuiThreadPool;
import club.pineclone.gui.event.PropertyChangeEventDispatcher;
import club.pineclone.gui.status.*;
import club.pineclone.gui.swing.VFlowLayout;
import club.pineclone.utils.*;
import club.pineclone.utils.l10n.LocTag;
import club.pineclone.utils.l10n.LocaleUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class LaunchPanel extends JFrame {
    /**
     * A Context contains the logic of how those button in the main frame display to the client, while execute different
     * logic, those button will have different showing status, this object will help to control all those buttons and make sure
     * all of them are in the right status.
     */
    private final Context ctx;

    private final JTextArea info;
    private boolean alwaysOnTop;

    private boolean allowBeep;
    private boolean enableInfoPanel;
    private boolean hasSaved = true;

    /**************************** The PropChangeListener Component ********************************/
    private final JMenu settingMenu;
    private final JMenu savedMenu;

    private final JMenu helpMenu;
    private final JMenu preferences;

    private final JButton recordBut;
    private final JButton stopBut;
    private final JButton saveBut;
    private final JButton submitBut;
    private final JButton selectBut;
    private final JButton execBut;

    private final JMenuItem keyboard;

    private final JMenuItem setAlwaysOnTop;
    private final JMenuItem setStartDelay;
    private final JMenuItem setUsingFont;

    private final JMenu language;
    private final JMenu selectLang;
    private final JMenuItem loadAvailableLang;
    private final JMenuItem openLangDir;

    private final JMenuItem setAllowBeep;
    private final JMenuItem openScriptDir;
    private final JMenuItem resetSavingLocation;
    private final JMenuItem openSystemConfig;

    private final JMenuItem usingGuidance;
    private final JMenuItem submitProblem;
    private final JMenuItem enableExtensionPanel;

    private final TitledBorder runBorderTitle;
    final TitledBorder buildBorderTitle;
    final JLabel statusLabel;

    /**
     * This method should be called at somewhere correctly.
     * em actually later those component will be added into a list to execute the flush method.
     */
    private class MenuBarLocalizer implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals("language_change")) {
                //update the text in the component.
                settingMenu.setText(LocaleUtils.loc(LocTag.SETTING_MENU));
                savedMenu.setText(LocaleUtils.loc(LocTag.SAVED_MENU));
                helpMenu.setText(LocaleUtils.loc(LocTag.HELP_MENU));

                keyboard.setText(LocaleUtils.loc(LocTag.KEY_BOARD));
                setAlwaysOnTop.setText(LocaleUtils.loc(LocTag.SET_ALWAYS_ON_TOP));

                preferences.setText(LocaleUtils.loc(LocTag.PREFERENCES));
                setStartDelay.setText(LocaleUtils.loc(LocTag.SET_START_DELAY));
                setAllowBeep.setText(allowBeep ? LocaleUtils.loc(LocTag.SET_MUTE_BEEP) :
                        LocaleUtils.loc(LocTag.SET_ALLOW_BEEP));
                setUsingFont.setText(LocaleUtils.loc(LocTag.SET_USING_FONT));
                language.setText(LocaleUtils.loc(LocTag.LANGUAGE));
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

    private class ButtonLocalizer implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals("language_change")) {
                recordBut.setText(LocaleUtils.loc(LocTag.RECORD_BUT));
                stopBut.setText(LocaleUtils.loc(LocTag.STOP_BUT));
                saveBut.setText(LocaleUtils.loc(LocTag.SAVE_BUT));
                submitBut.setText(LocaleUtils.loc(LocTag.SUBMIT_BUT));

                selectBut.setText(LocaleUtils.loc(LocTag.SELECT_BUT));
                execBut.setText(LocaleUtils.loc(LocTag.EXECUTE_BUT));

                //update the tooltip.
                saveBut.setToolTipText(LocaleUtils.loc(LocTag.SAVE_BUT_TOOLTIP));
                submitBut.setToolTipText(LocaleUtils.loc(LocTag.SUBMIT_BUT_TOOLTIP));
                selectBut.setToolTipText(LocaleUtils.loc(LocTag.SELECT_BUT_TOOLTIP));
                execBut.setToolTipText(LocaleUtils.loc(LocTag.EXECUTE_BUT_TOOLTIP));
                recordBut.setToolTipText(LocaleUtils.loc(LocTag.RECORD_BUT_TOOLTIP));
                stopBut.setToolTipText(LocaleUtils.loc(LocTag.STOP_BUT_TOOLTIP));
            }
        }
    }

    private class ExtendLocalizer implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals("language_change")) {
                setTitle(LocaleUtils.loc(LocTag.MAIN_TITLE) + (alwaysOnTop ? LocaleUtils.loc(LocTag.LOCK) : ""));
                //update border title
                runBorderTitle.setTitle(LocaleUtils.loc(LocTag.RUN_PANEL_BORDER_TITLE));
                buildBorderTitle.setTitle(LocaleUtils.loc(LocTag.BUILD_PANEL_BORDER_TITLE));
            }
        }
    }

    public void setHasSaved(boolean flag) {
        this.hasSaved = flag;
    }

    public void setAllowBeep(boolean flag) {
        this.allowBeep = flag;
    }

    public void setEnableInfoPanel(boolean flag) {
        this.enableInfoPanel = flag;
    }

    public boolean allowBeep() {
        return allowBeep;
    }

    public String getMainTitle() {
        return LocaleUtils.loc(LocTag.MAIN_TITLE);
    }

    public LaunchPanel() throws AWTException {
        FileUtils.PropertiesBundle sysBundle = FileUtils.getSysBundle();

        this.ctx = new Context(this);
        PropertyChangeEventDispatcher dispatcher = ctx.getDispatcher();

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setLocation(GuiUtils.getLastShutDownPoint());

        this.setIconImage(GuiUtils.icon);
        this.setResizable(false);

        this.info = new JTextArea();
        info.setBackground(new Color(0xFF, 0xFF, 0xFF));
        info.setForeground(new Color(0x00, 0x00, 0x00));
        info.setText("");
        info.setEditable(false);

        this.alwaysOnTop = Boolean.parseBoolean(
                sysBundle.getProp(FileUtils.SysTag.SET_ALLOW_ON_TOP.name()));
        this.enableInfoPanel = Boolean.parseBoolean(
                sysBundle.getProp(FileUtils.SysTag.ENABLE_INFO_PANEL.name()));

        this.stickOnTop(alwaysOnTop);
        this.allowBeep = Boolean.parseBoolean(sysBundle.getProp(FileUtils.SysTag.ALLOW_BEEP.name()));

        this.statusLabel = new JLabel();

        JMenuBar menuBar = new JMenuBar();
        settingMenu = new JMenu();
        savedMenu = new JMenu();
        helpMenu = new JMenu();
        recordBut = new JButton();
        stopBut = new JButton();
        saveBut = new JButton();
        submitBut = new JButton();
        selectBut = new JButton();
        execBut = new JButton();

        /* adding action listener to buttons. */
        saveBut.addActionListener(e -> {
            ctx.update(ImitStatus.EnumStatus.SAVING_FILE);
            ctx.refine();
        });

        submitBut.addActionListener(e -> {
            ctx.update(ImitStatus.EnumStatus.SUBMITTING);
            ctx.refine();
        });

        selectBut.addActionListener(e -> {
            ctx.update(ImitStatus.EnumStatus.SELECTING_FILE);
            ctx.refine();
        });

        ActionListener recordAct = e -> {
            if (!ctx.getImitStatus().getDesc().equals("nascent status")) return;
            ctx.update(ImitStatus.EnumStatus.PRE_RECORDING);
            ctx.refine();
        };
        recordBut.addActionListener(recordAct);

        ActionListener stopAct = e -> {
            String desc = ctx.getImitStatus().getDesc();
            if (desc.equals("nascent status")
                    || desc.equals("selecting status")
                    || desc.equals("saving status")) return;
            ctx.setStop(ctx -> ctx.resetAllButs(false, false, false, false, false, false));
        };
        stopBut.addActionListener(stopAct);

        ActionListener execAct = e -> {
            String desc = ctx.getImitStatus().getDesc();
            if (!desc.equals("nascent status")) return;
            if (!ctx.getProcessor().isExecutable()) return;
            ctx.update(ImitStatus.EnumStatus.EXECUTING);
            ctx.refine();
        };
        execBut.addActionListener(execAct);

        GlobalListener hotkeyListener = this.ctx.getHotkeyListener();
        hotkeyListener.putAction(KeyUtils.getHotkeyFromProp(KeyUtils.KeyTag.BEGIN_RECORD), recordAct);
        hotkeyListener.putAction(KeyUtils.getHotkeyFromProp(KeyUtils.KeyTag.STOP), stopAct);
        hotkeyListener.putAction(KeyUtils.getHotkeyFromProp(KeyUtils.KeyTag.EXECUTE), execAct);
        hotkeyListener.launch();


        //Set keyboard for easier start recording.
        keyboard = new JMenuItem();
        keyboard.addActionListener(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                hotkeyListener.setCanceled();
                Map<KeyUtils.KeyTag, KeyStroke> buffer = new HashMap<>();
                JDialog basicDialog = new JDialog(LaunchPanel.this,
                        LocaleUtils.loc(LocTag.HOTKEY_SETTING_DIALOG_TITLE), true);

                basicDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

                basicDialog.setIconImage(GuiUtils.icon);
                basicDialog.setResizable(false);
                VFlowLayout mgr1 = new VFlowLayout();
                FlowLayout mgr2 = new FlowLayout(FlowLayout.CENTER, 10, 1);
                GridLayout mgr3 = new GridLayout(0, 2);
                mgr3.setVgap(5);
                mgr3.setHgap(5);

                basicDialog.setLayout(mgr1);

                JButton resetBeginRecordBut = new JButton(KeyUtils.toShowingText(KeyUtils.getHotkeyFromProp(KeyUtils.KeyTag.BEGIN_RECORD)));
                resetBeginRecordBut.setFocusPainted(false);
                resetBeginRecordBut.addActionListener(el -> {
                    JDialog resetDialog = KeyUtils.getSetHotkeyDialog(LaunchPanel.this,
                            LocaleUtils.loc(LocTag.PRESS_TO_SET_BEGIN_RECORD));
                    resetDialog.addKeyListener(new KeyUtils.KeyCaptor() {
                        @Override
                        public void callBack(KeyStroke keyStroke) {
                            buffer.put(KeyUtils.KeyTag.BEGIN_RECORD, keyStroke);
                            resetBeginRecordBut.setText(KeyUtils.toShowingText(keyStroke));
                            resetDialog.dispose();
                        }
                    });
                    resetDialog.setVisible(true);
                });

                JButton resetExecuteBut = new JButton(KeyUtils.toShowingText(KeyUtils.getHotkeyFromProp(KeyUtils.KeyTag.EXECUTE)));
                resetExecuteBut.setFocusPainted(false);
                resetExecuteBut.addActionListener(el -> {
                    JDialog resetDialog = KeyUtils.getSetHotkeyDialog(LaunchPanel.this,
                            LocaleUtils.loc(LocTag.PRESS_TO_SET_EXECUTE));
                    resetDialog.addKeyListener(new KeyUtils.KeyCaptor() {
                        @Override
                        public void callBack(KeyStroke keyStroke) {
                            buffer.put(KeyUtils.KeyTag.EXECUTE, keyStroke);
                            resetExecuteBut.setText(KeyUtils.toShowingText(keyStroke));
                            resetDialog.dispose();
                        }
                    });
                    resetDialog.setVisible(true);
                });

                JButton resetStopBut = new JButton(KeyUtils.toShowingText(KeyUtils.getHotkeyFromProp(KeyUtils.KeyTag.STOP)));
                resetStopBut.setFocusPainted(false);
                resetStopBut.addActionListener(el -> {
                    JDialog resetDialog = KeyUtils.getSetHotkeyDialog(LaunchPanel.this,
                            LocaleUtils.loc(LocTag.PRESS_TO_SET_STOP));
                    resetDialog.addKeyListener(new KeyUtils.KeyCaptor() {
                        @Override
                        public void callBack(KeyStroke keyStroke) {
                            buffer.put(KeyUtils.KeyTag.STOP, keyStroke);
                            resetStopBut.setText(KeyUtils.toShowingText(keyStroke));
                            resetDialog.dispose();
                        }
                    });
                    resetDialog.setVisible(true);
                });

                JPanel HotkeyPanel = new JPanel();
                HotkeyPanel.setBorder(BorderFactory.createTitledBorder(LocaleUtils.loc(LocTag.HOTKEY_SETTING_BORDER)));
                HotkeyPanel.setLayout(mgr3);

                HotkeyPanel.add(new JLabel(LocaleUtils.loc(LocTag.HOTKEY_SETTING_LABEL_BEGIN_RECORD)));
                HotkeyPanel.add(resetBeginRecordBut);
                HotkeyPanel.add(new JLabel(LocaleUtils.loc(LocTag.HOTKEY_SETTING_LABEL_EXECUTE)));
                HotkeyPanel.add(resetExecuteBut);
                HotkeyPanel.add(new JLabel(LocaleUtils.loc(LocTag.HOTKEY_SETTING_LABEL_STOP)));
                HotkeyPanel.add(resetStopBut);

                JButton saveButton = new JButton(LocaleUtils.loc(LocTag.HOTKEY_SETTING_SAVE_BUT));
                saveButton.setFocusPainted(false);
                saveButton.addActionListener(event -> {
                    boolean hasRepeat = KeyUtils.checkIfHasRepeatHotkey(buffer);
                    if (hasRepeat) {
                        JOptionPane.showMessageDialog(LaunchPanel.this,
                                LocaleUtils.loc(LocTag.HOTKEY_SETTING_REPEAT_HOTKEY_POPUP_MSG),
                                LocaleUtils.loc(LocTag.HOTKEY_SETTING_REPEAT_HOTKEY_POPUP_TITLE),
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        KeyUtils.resetHotkey(buffer);
                        perform(LocaleUtils.loc(LocTag.SUCCESSFULLY_SAVE_HOTKEY_PERFORM));
                        buffer.forEach((k, v) -> perform(LocaleUtils.loc(LocTag.SUCCESSFULLY_SAVE_HOTKEY_SET_PRETEXT)
                                + k.getDescription() + LocaleUtils.loc(LocTag.SUCCESSFULLY_SAVE_HOTKEY_SET_POST_TEXT)
                                + KeyUtils.toShowingText(v)));
                        buffer.clear();
                        hotkeyListener.putAction(KeyUtils.getHotkeyFromProp(KeyUtils.KeyTag.BEGIN_RECORD), recordAct);
                        hotkeyListener.putAction(KeyUtils.getHotkeyFromProp(KeyUtils.KeyTag.STOP), stopAct);
                        hotkeyListener.putAction(KeyUtils.getHotkeyFromProp(KeyUtils.KeyTag.EXECUTE), execAct);
                        hotkeyListener.launch();
                        basicDialog.dispose();
                    }
                });

                basicDialog.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        hotkeyListener.rollBack();
                        hotkeyListener.launch();
                        basicDialog.dispose();
                    }
                });

                JButton cancelButton = new JButton(LocaleUtils.loc(LocTag.HOTKEY_SETTING_CANCEL_BUT));
                cancelButton.setFocusPainted(false);
                cancelButton.addActionListener(event -> basicDialog.dispose());

                JPanel buttonPanel = new JPanel();

                buttonPanel.setLayout(mgr2);
                buttonPanel.add(saveButton);
                buttonPanel.add(cancelButton);

                basicDialog.add(HotkeyPanel);
                basicDialog.add(buttonPanel);
                basicDialog.pack();
                basicDialog.setLocation(GuiUtils.getRelativeCenter(basicDialog, LaunchPanel.this));
                basicDialog.setVisible(true);
            }
        });


        //The param decide if the window could stay on the top before shutdown.
        setAlwaysOnTop = new JMenuItem();
        setAlwaysOnTop.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alwaysOnTop = !alwaysOnTop;
                String mainTitle = getMainTitle();
                String displayTitle = alwaysOnTop ? mainTitle + LocaleUtils.loc(LocTag.LOCK) : mainTitle;
                setTitle(displayTitle);
                stickOnTop(alwaysOnTop);
            }
        });

        //this is for store the last setting about if the window stick on the top is allowed.
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ctx.setStop(mainCtx -> mainCtx.resetAllButs(false, false, false, false, false, false));

                if (!hasSaved) {
                    int result = JOptionPane.showConfirmDialog(LaunchPanel.this,
//                            "You have unsaved action list \n Quiting will cause losing the recorded action \n Do want to save it? ",
//                            "Unsaved Actions",
                            LocaleUtils.loc(LocTag.WINDOW_CLOSE_WITH_UNSAVED_ACTION_POPUP_MSG),
                            LocaleUtils.loc(LocTag.WINDOW_CLOSE_WITH_UNSAVED_ACTION_POPUP_TITLE),
                            JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                            new ImageIcon(GuiUtils.icon));
                    switch (result) {
                        //yes
                        case 0: {
                            //turn to saving the file.
                            ctx.update(ImitStatus.EnumStatus.SAVING_FILE);
                            ctx.refine();
                            break;
                        }
                        //no
                        case 1: {
                            //do nothing and quit
                            break;
                        }
                        case -1: {
                            //simply return to origin
                            ctx.update(ImitStatus.EnumStatus.NASCENT);
                            ctx.refine();
                            return;
                        }
                    }
                }
                sysBundle.setProp(FileUtils.SysTag.SET_ALLOW_ON_TOP.name(), String.valueOf(alwaysOnTop));
                sysBundle.setProp(FileUtils.SysTag.ALLOW_BEEP.name(), String.valueOf(allowBeep));
                sysBundle.save();
                GuiUtils.loadShutDownPoint(LaunchPanel.this);
                LaunchPanel.this.dispose();
                System.exit(0);
            }
        });
        this.setTitle(getMainTitle() + (alwaysOnTop ? LocaleUtils.loc(LocTag.LOCK) : ""));

        //Set the delay before truly start record.
        setStartDelay = new JMenuItem();
        setStartDelay.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CompletableFuture.runAsync(() -> {
                    JDialog dialog = new JDialog(LaunchPanel.this,
                            LocaleUtils.loc(LocTag.RESET_INITIAL_DELAY_DIALOG_TITLE), true);
                    dialog.setIconImage(GuiUtils.icon);
                    dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                    JPanel buttonPanel = new JPanel();
                    JPanel configPanel = new JPanel();
                    configPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 1, 5));
                    configPanel.add(new JLabel(LocaleUtils.loc(LocTag.RESET_INITIAL_DELAY_DIALOG_LABEL_PRE)));

                    String curDelay = sysBundle.getProp(FileUtils.SysTag.START_DELAY.name());
                    JTextField recordInitialDelayTextField = new JTextField(curDelay, 7);
                    ((AbstractDocument) recordInitialDelayTextField.getDocument())
                            .setDocumentFilter(GuiUtils.DIG_FILTER);
                    recordInitialDelayTextField.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusLost(FocusEvent e) {
                            if (recordInitialDelayTextField.getText().equals("")) {
                                recordInitialDelayTextField.setText(curDelay);
                            }
                        }
                    });
                    configPanel.add(recordInitialDelayTextField);
                    configPanel.add(new JLabel(LocaleUtils.loc(LocTag.RESET_INITIAL_DELAY_DIALOG_LABEL_POST)));


                    JButton yesButton = new JButton(LocaleUtils.loc(LocTag.RESET_INITIAL_DELAY_DIALOG_YES_BUT));
                    yesButton.setFocusPainted(false);
                    yesButton.addActionListener(new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String delay = recordInitialDelayTextField.getText();
                            sysBundle.setProp(FileUtils.SysTag.START_DELAY.name(),
                                    delay);
                            sysBundle.save();
                            dialog.dispose();
                            perform(LocaleUtils.loc(LocTag.DONE_SETTING_INITIAL_DELAY_DELAY_PERFORM_PRE) +
                                    delay + LocaleUtils.loc(LocTag.DONE_SETTING_INITIAL_DELAY_DELAY_PERFORM_POST));
                        }
                    });
                    buttonPanel.add(yesButton);

                    JButton cancelButton = new JButton(LocaleUtils.loc(LocTag.RESET_INITIAL_DELAY_DIALOG_CANCEL_BUT));
                    cancelButton.setFocusPainted(false);
                    cancelButton.addActionListener(new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            dialog.dispose();
                        }
                    });
                    buttonPanel.add(cancelButton);

                    dialog.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            dialog.dispose();
                        }
                    });

                    dialog.add(configPanel, BorderLayout.CENTER);
                    dialog.add(buttonPanel, BorderLayout.SOUTH);
                    dialog.pack();
                    Point point = GuiUtils.getRelativeCenter(dialog, LaunchPanel.this);
                    dialog.setLocation(point);
                    dialog.setVisible(true);
                }, GuiThreadPool.getDefPool());
            }
        });

        //set using font. (this is weired
        setUsingFont = new JMenuItem();

        //set using language. this will allow user to set to their using language.
        language = new JMenu();
        selectLang = new JMenu();
        loadAvailableLang = new JMenuItem();
        openLangDir = new JMenuItem();

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

        openLangDir.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
            }
        });

        langItems.forEach(i -> {
            if (i.getName().equals(LocaleUtils.getLocale().toString())) {
                i.setEnabled(false);
            }
        });

        language.add(selectLang);
        language.add(loadAvailableLang);
        language.add(openLangDir);

        //set if beep is allow, in some place the beep is used for notice the client.
        setAllowBeep = new JMenuItem();
        setAllowBeep.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!allowBeep) Toolkit.getDefaultToolkit().beep();
                setAllowBeep(!allowBeep);
                setAllowBeep.setText(allowBeep ? LocaleUtils.loc(LocTag.SET_MUTE_BEEP) :
                        LocaleUtils.loc(LocTag.SET_ALLOW_BEEP));
            }
        });

        //open the directory where store the operation.
        openScriptDir = new JMenuItem();
        openScriptDir.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CompletableFuture.runAsync(() -> {
                    try {
                        String folderPath = FileUtils.getSysBundle().getProp(FileUtils.SysTag.DIR_FOR_SCRIPTS.name());
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
            }
        });

        //reset the directory for saving the recording action.
        resetSavingLocation = new JMenuItem();
        resetSavingLocation.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CompletableFuture.runAsync(() -> {
                    JFileChooser folderChooser = new JFileChooser();
                    folderChooser.setDialogTitle(LocaleUtils.loc(LocTag.RESET_SAVING_LOCATION_DIALOG_TITLE));
                    folderChooser.setMultiSelectionEnabled(false);
                    folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int result = folderChooser.showOpenDialog(LaunchPanel.this);
                    //when client choose a file correctly.
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = folderChooser.getSelectedFile();
                        String absolutePath = selectedFile.getAbsolutePath();
                        perform(LocaleUtils.loc(LocTag.RESET_SAVING_LOCATION_POST_PERFORM) + absolutePath);
                        sysBundle.setProp(FileUtils.SysTag.DIR_FOR_SCRIPTS.name(), absolutePath);
                        sysBundle.save();
                    }
                }, GuiThreadPool.getDefPool());
            }
        });

        //open the system configuration, this conf is usually for store the setting for software itself.
        openSystemConfig = new JMenuItem();
        openSystemConfig.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CompletableFuture.runAsync(() -> {
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
                }, GuiThreadPool.getDefPool());
            }
        });

        //enable to show up an extension panel to show more information.
        enableExtensionPanel = new JMenuItem();
        enableExtensionPanel.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setEnableInfoPanel(!enableInfoPanel);
                enableExtensionPanel.setText(enableInfoPanel ? LocaleUtils.loc(LocTag.DISABLE_EXTENSION_INFO_PANEL) :
                        LocaleUtils.loc(LocTag.ENABLE_EXTENSION_INFO_PANEL));
            }
        });

        //open a window show help to client.
        usingGuidance = new JMenuItem();

        //in this page client could send problem to the author.
        submitProblem = new JMenuItem();
        preferences = new JMenu();

        settingMenu.add(keyboard);
        settingMenu.add(setAlwaysOnTop);
        settingMenu.add(enableExtensionPanel);
        settingMenu.add(preferences);

        preferences.add(setStartDelay);
        preferences.add(setAllowBeep);
        preferences.add(setUsingFont);

        savedMenu.add(openScriptDir);
        savedMenu.add(resetSavingLocation);
        savedMenu.add(openSystemConfig);

        helpMenu.add(usingGuidance);
        helpMenu.add(submitProblem);

        menuBar.add(settingMenu);
        menuBar.add(savedMenu);
        menuBar.add(language);
        menuBar.add(helpMenu);


//        outputPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        /* Output Panel */
        JPanel outputPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(info);
        scrollPane.setPreferredSize(new Dimension(250, 250));
        outputPanel.add(scrollPane, BorderLayout.CENTER);

        /* Run Panel */
        FlowLayout verticalLayout = new VFlowLayout();
        JPanel runPanel = new JPanel();
        runPanel.setLayout(verticalLayout);
        runBorderTitle = BorderFactory.createTitledBorder(LocaleUtils.loc(LocTag.RUN_PANEL_BORDER_TITLE));
        buildBorderTitle = BorderFactory.createTitledBorder(LocaleUtils.loc(LocTag.BUILD_PANEL_BORDER_TITLE));
        runPanel.setBorder(runBorderTitle);

        /* Build Panel */
        JPanel buildPanel = new JPanel();
        buildPanel.setLayout(verticalLayout);
        buildPanel.setBorder(buildBorderTitle);

        /* Functional Panel */
        JPanel functionalPanel = new JPanel();
        functionalPanel.setLayout(verticalLayout);
        functionalPanel.add(buildPanel);
        functionalPanel.add(runPanel);

        /* Adding Button */
        Stream<JButton> streamOfBuildBut = Stream.of(recordBut, stopBut, saveBut, submitBut);
        Stream<JButton> streamOfRunBut = Stream.of(selectBut, execBut);

        streamOfBuildBut.forEach(b -> {
            b.setFocusPainted(false);
            buildPanel.add(b);
        });

        streamOfRunBut.forEach(b -> {
            b.setFocusPainted(false);
            runPanel.add(b);
        });

        /* initialize button status context. */
        ctx.initButList(recordBut, stopBut, saveBut, submitBut, selectBut, execBut);
        ctx.initStuMap(ImitStatus.EnumStatus.class, new NascentStatue(), new PreRecordingStatus(),
                new RecordingStatus(), new SelectingStatus(), new ExecutingStatus(), new SubmittingStatus(), new SavingStatus());

        /* update to nascent status. */
        ctx.update(ImitStatus.EnumStatus.NASCENT);
        ctx.refine();

        this.add(menuBar, BorderLayout.NORTH);
        this.add(outputPanel, BorderLayout.CENTER);
        this.add(functionalPanel, BorderLayout.EAST);

        /* for event */
        dispatcher.subscribe(MenuBarLocalizer::new);
        dispatcher.subscribe(ButtonLocalizer::new);
        dispatcher.subscribe(ExtendLocalizer::new);

        dispatcher.dispatch(new PropertyChangeEvent(dispatcher, "language_change"
                , null, LocaleUtils.getBundle().getLocale()));
        ctx.render();
        this.pack();
        System.out.println(this.getWidth()  + " , " + this.getHeight());
    }

    public void walkOff() {
        try {
            info.setText("");
            this.info.setCaretPosition(info.getLineStartOffset(info.getLineCount() - 1));
        } catch (BadLocationException e) {
            info.setCaretPosition(info.getDocument().getLength());
            e.printStackTrace();
        }
    }

    public void perform(final String output) {
        String out = " > " + output;
        info.append(generateLines(out, 45));
        info.setCaretPosition(info.getDocument().getLength());
    }

    public static List<String> splitString(String str, int len) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < str.length(); i += len) {
            int end = Math.min(i + len, str.length());
            result.add(str.substring(i, end));
        }
        return result;
    }

    public static String generateLines(String str, int len) {
        StringBuilder sb = new StringBuilder();
        List<String> lines = splitString(str, len);
        for (String line : lines) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    public void stickOnTop(boolean flag) {
        setAlwaysOnTop(flag);
        this.alwaysOnTop = flag;
    }
}
