package club.pineclone.process.executor.interfaces;

import club.pineclone.process.action.ActionList;
import club.pineclone.process.action.interfaces.RobotAction;

import java.util.LinkedList;
import java.util.List;

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
