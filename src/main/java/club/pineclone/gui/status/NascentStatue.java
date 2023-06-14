package club.pineclone.gui.status;

import club.pineclone.gui.context.LaunchPanelCtx;

public class NascentStatue implements ImitStatus {

    @Override
    public String getDesc() {
        return "nascent status";
    }

    @Override
    public void prep(LaunchPanelCtx ctx) {
        boolean isSavable = ctx.getProcessor().isSavable();
        boolean isExecutable = ctx.getProcessor().isExecutable();

        ctx.resetAllButs(true,
                false,
                isSavable,
                isSavable,
                true,
                isExecutable);
    }

    @Override
    public void exec(LaunchPanelCtx ctx) {
        //void
    }

    @Override
    public void stop(LaunchPanelCtx ctx) {
        //void
    }
}
