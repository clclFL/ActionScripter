package club.pineclone.gui.status;

import club.pineclone.gui.Context;

public interface ImitStatus {

    String getDesc();

    void prep(Context ctx);

    void exec(Context ctx);

    void stop(Context ctx);

    enum EnumStatus {
        NASCENT, PRE_RECORDING, RECORDING,
        SELECTING_FILE, EXECUTING, SUBMITTING, SAVING_FILE;
    }
}
