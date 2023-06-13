package club.pineclone.gui.status;

import club.pineclone.concurrent.GuiThreadPool;
import club.pineclone.gui.LaunchContext;
import club.pineclone.gui.MainFrame;
import club.pineclone.utils.FileUtils;
import club.pineclone.utils.i18n.LocTag;
import club.pineclone.utils.i18n.LocaleUtils;

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
    public void prep(LaunchContext ctx) {
        ctx.resetAllButs(false,
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
    public void stop(LaunchContext ctx) {
        if (future != null) future.cancel(true);
        if (task != null) task.cancel();
        ctx.getImitFrame().perform(LocaleUtils
                .loc(LocTag.PRE_RECORDING_STATUS_SET_CANCEL_PERFORM));
        ctx.update(EnumStatus.NASCENT);
        ctx.refine();
    }
}
