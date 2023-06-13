package club.pineclone.gui.status;

import club.pineclone.gui.LaunchContext;

public interface ImitStatus {

    String getDesc();

    void prep(LaunchContext ctx);

    void exec(LaunchContext ctx);

    void stop(LaunchContext ctx);

    enum EnumStatus {
        NASCENT, PRE_RECORDING, RECORDING,
        SELECTING_FILE, EXECUTING, SUBMITTING, SAVING_FILE;
    }
}
