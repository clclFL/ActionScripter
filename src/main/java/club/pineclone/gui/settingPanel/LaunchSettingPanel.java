package club.pineclone.gui.settingPanel;

import club.pineclone.gui.MainSettingMenu;
import club.pineclone.gui.registry.SettingPanel;
import club.pineclone.utils.i18n.LocTag;
import club.pineclone.utils.i18n.LocaleUtils;

public class LaunchSettingPanel extends SettingPanel {

    public LaunchSettingPanel(MainSettingMenu main, int index) {
        super(main, index);
    }

    @Override
    public String getTitle() {
        return LocaleUtils.loc(LocTag.LAUNCH_PANEL_SETTING_TITLE);
    }
}
