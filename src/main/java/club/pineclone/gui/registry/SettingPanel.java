package club.pineclone.gui.registry;

import club.pineclone.gui.MainSettingMenu;
import club.pineclone.gui.api.Registrable;

import javax.swing.*;

public abstract class SettingPanel extends JPanel implements Registrable {

    protected final MainSettingMenu main;
    protected int index;

    public SettingPanel(MainSettingMenu main, int index) {
        this.main = main;
        this.index = index;
        this.setSize(main.getSize());
    }

    public abstract String getTitle();

    public String getTip() {
        return null;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public void register() {
        main.addSettingPanel(this);
    }
}
