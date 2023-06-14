package club.pineclone.gui;

import club.pineclone.gui.api.Registry;
import club.pineclone.gui.registry.SettingPanel;
import club.pineclone.gui.registry.SettingPanelRegistryHandler;
import club.pineclone.utils.i18n.LocTag;
import club.pineclone.utils.i18n.LocaleUtils;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;

public class MainSettingMenu extends JDialog {

    private final JTabbedPane tabbed;
    private final SettingPanelRegistryHandler register;
    private final List<SettingPanel> panels = new LinkedList<>();

    public MainSettingMenu(MainFrame parent) {
        super(parent, true);
        this.tabbed = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        this.register = new SettingPanelRegistryHandler();
        this.setSize(new Dimension((int) (parent.getWidth() * 0.9),
                (int) (parent.getHeight() * 0.9)));
        this.setTitle(LocaleUtils.loc(LocTag.MAIN_SETTING_MENU_TITLE));
        this.setResizable(false);
        parent.getContext().getDispatcher().subscribe(RenderListener::new);
        setLayout(new BorderLayout());

    }

    public void addSettingPanel(SettingPanel panel) {
        panels.add(panel);
        this.tabbed.addTab(panel.getTitle(), null, panel, panel.getTip());
//        this.tabbed.addTab(panel.getTitle() , panel);
    }

    public void initializingSettingPanel() {
        if (tabbed.getTabCount() > 0) tabbed.setSelectedIndex(0);
        add(tabbed, BorderLayout.CENTER);
    }

    public Registry<SettingPanel> getRegister() {
        return register;
    }

    public class RenderListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals("language_change")) {
                MainSettingMenu.this.setTitle(LocaleUtils.loc(LocTag.MAIN_SETTING_MENU_TITLE));
                panels.forEach(p -> tabbed.setTitleAt(p.getIndex() , p.getTitle()));
            }
        }
    }
}
