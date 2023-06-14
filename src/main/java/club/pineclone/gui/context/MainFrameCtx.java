package club.pineclone.gui.context;

import club.pineclone.gui.MainFrame;
import club.pineclone.gui.MainSettingMenu;
import club.pineclone.gui.event.PropertyChangeEventDispatcher;

public class MainFrameCtx {

    private final MainFrame mainFrame;

    /**
     * Main dispatcher
     */
    private final PropertyChangeEventDispatcher dispatcher;

    public MainFrameCtx(MainFrame mainFr) {
        this.dispatcher = new PropertyChangeEventDispatcher();
        this.mainFrame = mainFr;
    }

    public PropertyChangeEventDispatcher getDispatcher() {
        return dispatcher;
    }

    public MainFrame getMainFrame() {
        return mainFrame;
    }

}
