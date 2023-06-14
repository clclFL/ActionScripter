package club.pineclone.gui.registry;

import club.pineclone.gui.api.Registrable;
import club.pineclone.gui.api.Registry;

import java.util.LinkedList;
import java.util.List;

public class SettingPanelRegistryHandler implements Registry<SettingPanel> {

    private final List<SettingPanel> panels = new LinkedList<>();

    public SettingPanelRegistryHandler() {
    }

    @Override
    public void addToRegistryList(SettingPanel target) {
        panels.add(target);
    }

    @Override
    public void registerAll() {
        panels.forEach(Registrable::register);
    }
}
