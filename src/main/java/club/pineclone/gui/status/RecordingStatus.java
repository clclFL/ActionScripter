package club.pineclone.gui.status;

import club.pineclone.concurrent.GuiThreadPool;
import club.pineclone.gui.LaunchContext;
import club.pineclone.gui.MainFrame;
import club.pineclone.utils.i18n.LocTag;
import club.pineclone.utils.i18n.LocaleUtils;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

public class RecordingStatus implements ImitStatus {

    private CompletableFuture<Void> future;

    @Override
    public String getDesc() {
        return "recording status";
    }

    @Override
    public void prep(LaunchContext ctx) {
        ctx.resetAllButs(
                false,
                true,
                false,
                false,
                false,
                false);
    }

    @Override
    public void exec(LaunchContext ctx) {
        future = CompletableFuture.runAsync(() -> {
            MainFrame mainFr = ctx.getImitFrame();
            if (mainFr.allowBeep()) Toolkit.getDefaultToolkit().beep();
            mainFr.perform(LocaleUtils.loc(LocTag.RECORDING_STATUS_LAUNCH_MONITOR)
                    + ctx.getProcessor().getMonitorName());
            ctx.getProcessor().launchMonitor();
            mainFr.perform(LocaleUtils.loc(LocTag.RECORDING_STATUS_START_RECORDING));
        }, GuiThreadPool.getDefPool());
    }

    @Override
    public void stop(LaunchContext ctx) {
        MainFrame mainFrame = ctx.getImitFrame();
        if (future != null) {
            future.cancel(true);
            mainFrame.perform(LocaleUtils
                    .loc(LocTag.RECORDING_STATUS_SET_CANCEL_TERMINATING_MONITOR) +
                    ctx.getProcessor().getMonitorName());
            ctx.getProcessor().setCanceled();
            mainFrame.perform(LocaleUtils
                    .loc(LocTag.RECORDING_STATUS_SET_CANCEL_STOP_RECORDING));
            mainFrame.setHasSaved(false);
            ctx.update(EnumStatus.NASCENT);
            ctx.refine();
        }
    }
}
