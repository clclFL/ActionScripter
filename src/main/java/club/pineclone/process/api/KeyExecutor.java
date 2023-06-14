package club.pineclone.process.api;

import club.pineclone.process.action.ActionList;

public abstract class KeyExecutor extends Executor {

    @Override
    public abstract void submit(ActionList actions);

    @Override
    public abstract void setCanceled();

    @Override
    public abstract void launch();
}
