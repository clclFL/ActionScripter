package club.pineclone.gui.status;

import club.pineclone.concurrent.GuiThreadPool;
import club.pineclone.gui.context.LaunchPanelCtx;
import club.pineclone.gui.functionalPanel.LaunchPanel;
import club.pineclone.process.Processor;
import club.pineclone.utils.i18n.LocTag;
import club.pineclone.utils.i18n.LocaleUtils;

import java.util.concurrent.CompletableFuture;

public class SubmittingStatus implements ImitStatus {

    @Override
    public String getDesc() {
        return "submitting status";
    }

    @Override
    public void prep(LaunchPanelCtx ctx) {
        ctx.resetAllButs(false , false , false , false , false , false);
    }

    @Override
    public void exec(LaunchPanelCtx ctx) {
        CompletableFuture.runAsync(() -> {
            LaunchPanel mainFr = ctx.getImitFrame();
            Processor imitater = ctx.getProcessor();
            mainFr.perform(LocaleUtils
                    .loc(LocTag.SUBMITTING_STATUS_TRYING_SUBMITTING_PERFORM));
            if (!imitater.isSavable()) {
                mainFr.perform(LocaleUtils
                        .loc(LocTag.SUBMITTING_STATUS_NO_ACTION_IN_MONITOR_PERFORM));
                return;
            }
            imitater.submit();
            mainFr.perform(LocaleUtils
                    .loc(LocTag.SUBMITTING_STATUS_DONE_SUBMITTING_PERFORM));
            ctx.update(EnumStatus.NASCENT);
            ctx.refine();
        }, GuiThreadPool.getDefPool());
    }

    @Override
    public void stop(LaunchPanelCtx ctx) {

    }
}
