package club.pineclone.gui.api;

public interface Registry<T extends Registrable> {

    void addToRegistryList(T target);

    void registerAll();

}
