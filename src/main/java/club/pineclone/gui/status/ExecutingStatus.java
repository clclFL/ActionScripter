package club.pineclone.gui.status;

import club.pineclone.api.CallBack;
import club.pineclone.concurrent.GuiThreadPool;
import club.pineclone.gui.LaunchContext;
import club.pineclone.gui.MainFrame;
import club.pineclone.gui.swing.VFlowLayout;
import club.pineclone.process.Processor;
import club.pineclone.process.ProcessorImpl;
import club.pineclone.utils.FileUtils;
import club.pineclone.utils.GuiUtils;
import club.pineclone.utils.i18n.LocTag;
import club.pineclone.utils.i18n.LocaleUtils;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ExecutingStatus implements ImitStatus {

    private CompletableFuture<Void> future;
    private TimerTask task;
    private final Timer timer = new Timer();

    @Override
    public String getDesc() {
        return "executing status";
    }

    public enum TextFieldType {
        EXECUTING_MODE, EXECUTING_TIMES, INITIAL_DELAY, INTERVAL_DELAY;
    }

    @Override
    public void prep(LaunchContext ctx) {
        ctx.resetAllButs(false,
                true,
                false,
                false,
                false,
                false);
    }

    @Override
    public void exec(LaunchContext ctx) {
        MainFrame mainFrame = ctx.getImitFrame();
        Processor imitater = ctx.getProcessor();

        JDialog dialog = new JDialog(mainFrame, LocaleUtils.loc(LocTag.EXECUTING_STATUS_CONFIG_DIALOG_TITLE), true);
        dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        ImageIcon image = new ImageIcon(GuiUtils.icon.getScaledInstance(50, 50, Image.SCALE_DEFAULT));

        JPanel imagePanel = new JPanel();
        JLabel imageLabel = new JLabel(image);
        imagePanel.add(imageLabel);

        FlowLayout mgr1 = new FlowLayout(FlowLayout.CENTER, 10, 3);
        GridLayout mgr2 = new GridLayout(0, 2);
        FlowLayout mgr4 = new FlowLayout(FlowLayout.LEADING, 5, 1);
        VFlowLayout mgr3 = new VFlowLayout();
        mgr3.setHgap(5);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(mgr2);
        textPanel.setBorder(BorderFactory.createTitledBorder(LocaleUtils
                .loc(LocTag.EXECUTING_STATUS_CONFIG_DIALOG_BORDER_TITLE)));

        //Executing Times
        JPanel executingTimesPanel = new JPanel(mgr4);
//        executingTimesPanel.setBorder(defBorder);

//        Properties sysConf = FileUtils.getSysConf();
        FileUtils.PropertiesBundle sysBundle = FileUtils.getSysBundle();
        String executingTimes = sysBundle.getProp(TextFieldType.EXECUTING_TIMES.name());
        String initialDelay = sysBundle.getProp(TextFieldType.INITIAL_DELAY.name());
        String intervalDelay = sysBundle.getProp(TextFieldType.INTERVAL_DELAY.name());

        JTextField executingTimesTextField = new JTextField(executingTimes, 2);
        JTextField initialDelayTextField = new JTextField(initialDelay, 6);
        JTextField intervalDelayTextField = new JTextField(intervalDelay, 6);

        ButtonGroup executingTimesButGroup = new ButtonGroup();
        AbstractAction timesAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (e.getActionCommand()) {
                    case "Unlimited" : {
                        executingTimesTextField.setEditable(false);
                        break;
                    }

                    case "Limited" : {
                        executingTimesTextField.setEditable(true);
                        break;
                    }
                }
            }
        };

        String executingMode = sysBundle.getProp(TextFieldType.EXECUTING_MODE.name());
        boolean flag = executingMode.equals("Limited");

        JRadioButton unlimitedTimes = new JRadioButton(LocaleUtils
                .loc(LocTag.EXECUTING_STATUS_CONFIG_DIALOG_RADIO_BUT_UNLIMITED));
        unlimitedTimes.setActionCommand("Unlimited");
        unlimitedTimes.setSelected(!flag);
        unlimitedTimes.addActionListener(timesAction);
        unlimitedTimes.setFocusPainted(false);

        JRadioButton limitedTimes = new JRadioButton(LocaleUtils.loc(LocTag.EXECUTING_STATUS_CONFIG_DIALOG_RADIO_BUT_LIMITED));
        limitedTimes.setActionCommand("Limited");
        limitedTimes.setSelected(flag);
        limitedTimes.addActionListener(timesAction);
        limitedTimes.setFocusPainted(false);

        executingTimesButGroup.add(unlimitedTimes);
        executingTimesButGroup.add(limitedTimes);
        executingTimesPanel.add(unlimitedTimes);
        executingTimesPanel.add(limitedTimes);
//        executingTimesPanel.add(executingTimesButGroup);
        executingTimesPanel.add(executingTimesTextField);
        executingTimesPanel.add(new JLabel(LocaleUtils.loc(LocTag.EXECUTING_STATUS_CONFIG_DIALOG_TIMES_POST)));
        ((AbstractDocument) executingTimesTextField.getDocument()).setDocumentFilter(GuiUtils.DIG_FILTER);
        executingTimesTextField.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                String text = executingTimesTextField.getText();
                if (text.equals("") || Integer.parseInt(text) == 0)
                    executingTimesTextField.setText("1");
                boolean flag = (Integer.parseInt(executingTimesTextField.getText()) > 1);
                if (flag)
                    intervalDelayTextField.setEditable(true);
                else {
                    intervalDelayTextField.setEditable(false);
                    intervalDelayTextField.setText("0");
                }
            }
        });
        textPanel.add(new JLabel(LocaleUtils.loc(LocTag.EXECUTING_STATUS_CONFIG_DIALOG_TIMES_PRE)));
        textPanel.add(executingTimesPanel);

        //Initial Delay.
        JPanel initialDelayPanel = new JPanel(mgr4);


        initialDelayPanel.add(initialDelayTextField);
        ((AbstractDocument) initialDelayTextField.getDocument()).setDocumentFilter(GuiUtils.DIG_FILTER);
        initialDelayTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (initialDelayTextField.getText().equals(""))
                    initialDelayTextField.setText("0");
            }
        });
        initialDelayPanel.add(new JLabel(LocaleUtils
                .loc(LocTag.EXECUTING_STATUS_CONFIG_DIALOG_INITIAL_DELAY_POST)));
        textPanel.add(new JLabel(LocaleUtils
                .loc(LocTag.EXECUTING_STATUS_CONFIG_DIALOG_INITIAL_DELAY_PRE)));
        textPanel.add(initialDelayPanel);

        //Interval Delay.
        JPanel intervalDelayPanel = new JPanel(mgr4);
        intervalDelayPanel.add(intervalDelayTextField);
        ((AbstractDocument) intervalDelayTextField.getDocument()).setDocumentFilter(GuiUtils.DIG_FILTER);
        intervalDelayTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                boolean flag = (Integer.parseInt(executingTimesTextField.getText()) > 1);
                if (flag)
                    intervalDelayTextField.setEditable(true);
                else {
                    intervalDelayTextField.setEditable(false);
                    intervalDelayTextField.setText("0");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (initialDelayTextField.getText().equals(""))
                    initialDelayTextField.setText("0");
            }
        });

        dialog.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                boolean flag = (Integer.parseInt(executingTimesTextField.getText()) > 1);
                if (flag)
                    intervalDelayTextField.setEditable(true);
                else {
                    intervalDelayTextField.setEditable(false);
                    intervalDelayTextField.setText("0");
                }
            }
        });

        intervalDelayPanel.add(new JLabel(LocaleUtils
                .loc(LocTag.EXECUTING_STATUS_CONFIG_DIALOG_INTERVAL_DELAY_POST)));
        textPanel.add(new JLabel(LocaleUtils
                .loc(LocTag.EXECUTING_STATUS_CONFIG_DIALOG_INTERVAL_DELAY_PRE)));
        intervalDelayTextField.setEditable(Integer.parseInt(executingTimesTextField.getText()) > 1);
        textPanel.add(intervalDelayPanel);

        JPanel configPanel = new JPanel();
        configPanel.add(imagePanel, BorderLayout.WEST);
        configPanel.add(textPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(mgr1);
        JButton yesButton = new JButton(LocaleUtils.loc(LocTag.EXECUTING_STATUS_CONFIG_YES_BUT));
        yesButton.setFocusPainted(false);
        yesButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String actionCommand = executingTimesButGroup.getSelection().getActionCommand();
                String executingTimesStr = executingTimesTextField.getText();
                String initialDelayStr = initialDelayTextField.getText();
                String intervalDelayStr = intervalDelayTextField.getText();

                boolean flag = actionCommand.equals("Limited");
                int initialDelay = Integer.parseInt(initialDelayStr);
                int intervalDelay = Integer.parseInt(intervalDelayStr);
                int times = Integer.parseInt(executingTimesStr);

                sysBundle.setProp(TextFieldType.EXECUTING_MODE.name(), actionCommand);
                sysBundle.setProp(TextFieldType.EXECUTING_TIMES.name(), executingTimesStr);
                sysBundle.setProp(TextFieldType.INITIAL_DELAY.name(), initialDelayStr);
                sysBundle.setProp(TextFieldType.INTERVAL_DELAY.name(), intervalDelayStr);
                sysBundle.save();

                mainFrame.perform(LocaleUtils
                        .loc(LocTag.EXECUTING_STATUS_LAUNCHING_EXECUTOR_PERFORM)
                        + imitater.getExecutorName());
                mainFrame.perform(LocaleUtils.loc(
                        LocTag.EXECUTING_STATUS_HOTKEY_IS_AVAILABLE_PERFORM));

                final CallBack<Void> pre = var -> {
                };
                final CallBack<Void> post = var -> {
                };
                final CallBack<Void> terminal1 = var -> {
                    mainFrame.perform(LocaleUtils
                            .loc(LocTag.EXECUTING_STATUS_DONE_TASK_PERFORM));
                    mainFrame.perform(LocaleUtils
                            .loc(LocTag.EXECUTING_STATUS_TERMINATING_EXECUTOR_PERFORM) + imitater.getExecutorName());
                    ctx.update(EnumStatus.NASCENT);
                    ctx.refine();
                    imitater.setCanceled();
                };
                final CallBack<Void> terminal2 = var -> {
                };

                final Supplier<CompletableFuture<Void>> limitedFuture = () -> CompletableFuture.runAsync(() -> ((ProcessorImpl) imitater)
                        .launchExecutor(times, 0, intervalDelay,
                                pre, post, terminal1), GuiThreadPool.getDefPool());

                final Supplier<CompletableFuture<Void>> unlimitedFuture = () -> CompletableFuture.runAsync(() -> ((ProcessorImpl) imitater)
                        .launchExecutor(Integer.MAX_VALUE, 0, intervalDelay,
                                pre, post, terminal2), GuiThreadPool.getDefPool());


                if (initialDelay != 0) {
                    task = new TimerTask() {
                        @Override
                        public void run() {
                            future = flag ? limitedFuture.get() : unlimitedFuture.get();
                        }
                    };
                    timer.schedule(task, initialDelay);
                } else future = flag ? limitedFuture.get() : unlimitedFuture.get();
                dialog.dispose();
            }
        });

        JButton cancelButton = new JButton(LocaleUtils
                .loc(LocTag.EXECUTING_STATUS_CONFIG_CANCEL_BUT));
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ctx.update(EnumStatus.NASCENT);
                ctx.refine();
                dialog.dispose();
            }
        });

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ctx.update(EnumStatus.NASCENT);
                ctx.refine();
                dialog.dispose();
            }
        });

        buttonPanel.add(yesButton);
        buttonPanel.add(cancelButton);

        dialog.add(configPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        Point point = GuiUtils.getRelativeCenter(dialog, mainFrame);
        dialog.setLocation(point);
        dialog.setVisible(true);
    }


    @Override
    public void stop(LaunchContext ctx) {
        Processor imitater = ctx.getProcessor();
        MainFrame mainFrame = ctx.getImitFrame();
        if (future != null ) {
            future.cancel(true);
            mainFrame.perform(LocaleUtils.loc(LocTag.EXECUTING_STATUS_SET_STOP_PERFORM_1));
        }
        if (task != null) {
            task.cancel();
        }
        timer.purge();
        imitater.setCanceled();
        mainFrame.perform(LocaleUtils.loc(LocTag.EXECUTING_STATUS_SET_STOP_PERFORM_2)
                + imitater.getExecutorName());
        ctx.update(EnumStatus.NASCENT);
        ctx.refine();
    }
}
