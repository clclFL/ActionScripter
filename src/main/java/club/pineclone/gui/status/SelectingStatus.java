package club.pineclone.gui.status;

import club.pineclone.gui.Context;
import club.pineclone.gui.LaunchPanel;
import club.pineclone.process.Processor;
import club.pineclone.utils.FileUtils;
import club.pineclone.utils.i18n.LocTag;
import club.pineclone.utils.i18n.LocaleUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class SelectingStatus implements ImitStatus {

    @Override
    public String getDesc() {
        return "selecting status";
    }

    public void prep(Context ctx) {
        boolean isExecutable = ctx.getProcessor().isExecutable();
        ctx.resetAllButs(
                false,
                false,
                false,
                false,
                false,
                isExecutable);
    }

    @Override
    public void exec(Context ctx) {
        LaunchPanel mainFr = ctx.getImitFrame();
        Processor imitater = ctx.getProcessor();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(LocaleUtils.loc(LocTag.SELECTING_STATUS_DIALOG_TITLE));
        fileChooser.setMultiSelectionEnabled(false);

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                LocaleUtils.loc(LocTag.SELECTING_STATUS_FILTER_DESCRIPTION),
                "imit");
        fileChooser.setFileFilter(filter);
        String dirPath = FileUtils.getSysBundle().getProp(FileUtils.SysTag.DIR_FOR_SCRIPTS.name());
        fileChooser.setCurrentDirectory(new File(dirPath));
        fileChooser.setVisible(true);
        int result = fileChooser.showOpenDialog(mainFr);

        //when client choose a file correctly.
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            imitater.submit(file);
            mainFr.perform(LocaleUtils
                    .loc(LocTag.SELECTING_STATUS_AFTER_SELECTING_PERFORM_PRE)
                    + file.getAbsolutePath() + LocaleUtils
                    .loc(LocTag.SELECTING_STATUS_AFTER_SELECTING_PERFORM_POST));
        }
        ctx.update(EnumStatus.NASCENT);
        ctx.refine();
    }

    @Override
    public void stop(Context ctx) {

    }
}
