package club.pineclone.process.api;

import club.pineclone.process.action.ActionList;

public abstract class Executor {

    protected final ActionList actions = new ActionList();

    public abstract void submit(ActionList actions);

    public abstract void setCanceled();

    public abstract void launch();

    public final boolean hasActions() {
        return !this.actions.isEmpty();
    }

    public abstract String getName();
}
