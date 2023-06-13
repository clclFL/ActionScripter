package club.pineclone.gui.status;

import club.pineclone.concurrent.GuiThreadPool;
import club.pineclone.gui.Context;
import club.pineclone.gui.LaunchPanel;
import club.pineclone.utils.FileUtils;
import club.pineclone.utils.l10n.LocTag;
import club.pineclone.utils.l10n.LocaleUtils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

public class PreRecordingStatus implements ImitStatus {

    private CompletableFuture<Void> future;
    private final Timer timer = new Timer();
    private TimerTask task;

    @Override
    public String getDesc() {
        return "prerecording status";
    }

    @Override
    public void prep(Context ctx) {
        ctx.resetAllButs(false,
                true,
                false,
                false,
                false,
                false);
    }

    @Override
    public void exec(Context ctx) {
        future = CompletableFuture.runAsync(() -> {

            LaunchPanel mainFr = ctx.getImitFrame();
            int beginDelay = Integer.parseInt(FileUtils.getSysBundle()
                    .getProp(FileUtils.SysTag.START_DELAY.name()));

            mainFr.walkOff();
            if (beginDelay != 0)
                mainFr.perform(LocaleUtils
                        .loc(LocTag.PRE_RECORDING_STATUS_WAITING_PERFORM_PRE)+
                        beginDelay + LocaleUtils
                        .loc(LocTag.PRE_RECORDING_STATUS_WAITING_PERFORM_POST));
            task = new TimerTask() {
                @Override
                public void run() {
                    ctx.update(EnumStatus.RECORDING);
                    ctx.refine();
                    timer.purge();
                }
            };

            timer.schedule(task, beginDelay * 1000L);
        }, GuiThreadPool.getDefPool());
    }

    @Override
    public void stop(Context ctx) {
        if (future != null) future.cancel(true);
        if (task != null) task.cancel();
        ctx.getImitFrame().perform(LocaleUtils
                .loc(LocTag.PRE_RECORDING_STATUS_SET_CANCEL_PERFORM));
        ctx.update(EnumStatus.NASCENT);
        ctx.refine();
    }
}
