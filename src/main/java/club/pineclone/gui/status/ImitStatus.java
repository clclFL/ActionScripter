package club.pineclone.gui.status;

import club.pineclone.gui.context.LaunchPanelCtx;

public interface ImitStatus {

    String getDesc();

    void prep(LaunchPanelCtx ctx);

    void exec(LaunchPanelCtx ctx);

    void stop(LaunchPanelCtx ctx);

    enum EnumStatus {
        NASCENT, PRE_RECORDING, RECORDING,
        SELECTING_FILE, EXECUTING, SUBMITTING, SAVING_FILE;
    }
}
