package club.pineclone.gui.registry;

import club.pineclone.gui.api.Registrable;
import club.pineclone.gui.api.Registry;

import java.util.LinkedList;
import java.util.List;

public class FunctionalPanelRegistryHandler
        implements Registry<FunctionalPanel> {

    private final List<FunctionalPanel> panels = new LinkedList<>();

    public FunctionalPanelRegistryHandler(){
    }

    @Override
    public void addToRegistryList(FunctionalPanel target) {
        this.panels.add(target);
    }

    public void registerAll() {
        panels.forEach(Registrable::register);
    }
}
