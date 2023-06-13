package club.pineclone.gui.status;

import club.pineclone.gui.Context;

public class NascentStatue implements ImitStatus {

    @Override
    public String getDesc() {
        return "nascent status";
    }

    @Override
    public void prep(Context ctx) {
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
    public void exec(Context ctx) {
        //void
    }

    @Override
    public void stop(Context ctx) {
        //void
    }
}
