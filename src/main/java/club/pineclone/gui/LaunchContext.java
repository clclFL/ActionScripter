package club.pineclone.gui;

import club.pineclone.api.CallBack;
import club.pineclone.gui.event.PropertyChangeEventDispatcher;
import club.pineclone.gui.status.ImitStatus;
import club.pineclone.process.Processor;
import club.pineclone.process.ProcessorImpl;
import club.pineclone.utils.GlobalListener;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class LaunchContext {

    private final MainFrame launchPanel;

    private final PropertyChangeEventDispatcher dispatcher;
    private final Processor processor;
    private final GlobalListener hotkeyListener;

    private final List<JButton> butList = new LinkedList<>();
    private final Map<String, ImitStatus> staMap = new HashMap<>();

    private ImitStatus status;

    public LaunchContext(MainFrame parent) throws AWTException {
        this.dispatcher = new PropertyChangeEventDispatcher();
        this.processor = new ProcessorImpl();
        this.hotkeyListener = new GlobalListener();
        this.launchPanel = parent;
    }

    public Processor getProcessor() {
        return this.processor;
    }

    public GlobalListener getHotkeyListener() {
        return hotkeyListener;
    }

    public MainFrame getImitFrame() {
        return launchPanel;
    }

    public ImitStatus getImitStatus() {
        return status;
    }

    public PropertyChangeEventDispatcher getDispatcher() {
        return dispatcher;
    }

    public void update(ImitStatus.EnumStatus status) {
        this.status = staMap.get(status.name());
    }
    
    /**
     * This method is for update the status of software, such as the gui.
     */
    public void refine() {
        //the first step, this is for flush the ui.
        status.prep(this);
        //the second is for running the task.
        status.exec(this);
    }

    public void render() {
        status.prep(this);
    }

    public void setStop(CallBack<LaunchContext> callBack) {
        callBack.callBack(this);
        status.stop(this);
    }

    public void putButton(JButton but) {
        butList.add(but);
    }

    public void putStatus(String tag, ImitStatus stu) {
        staMap.put(tag, stu);
    }

    public void putStatus(Enum<?> tag, ImitStatus stu) {
        staMap.put(tag.name(), stu);
    }

    public void initButList(JButton... buts) {
        butList.addAll(Arrays.stream(buts).collect(Collectors.toList()));
    }

    public void initStuMap(Class<? extends Enum<?>> enumClass, ImitStatus... statuses) {
        Enum<?>[] tags = enumClass.getEnumConstants();
        if (tags.length != statuses.length) return;
        for (int i = 0; i < tags.length; i++) {
            staMap.put(tags[i].name() , statuses[i]);
        }
    }

    public void initStuMap(String[] tags , ImitStatus... statuses) {
        if (tags.length != statuses.length) return;
        for (int i = 0; i < tags.length; i++) {
            staMap.put(tags[i] , statuses[i]);
        }
    }

    public void resetAllButs(boolean... booleans) {
        if (booleans.length != butList.size()) return;

        for (int i = 0; i < butList.size(); i++) {
            butList.get(i).setEnabled(booleans[i]);
        }
    }
}
