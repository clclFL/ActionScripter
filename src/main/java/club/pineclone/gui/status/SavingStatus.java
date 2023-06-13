package club.pineclone.gui.status;

import club.pineclone.gui.LaunchContext;
import club.pineclone.gui.MainFrame;
import club.pineclone.process.Processor;
import club.pineclone.utils.FileUtils;
import club.pineclone.utils.i18n.LocTag;
import club.pineclone.utils.i18n.LocaleUtils;

import javax.swing.*;
import java.io.File;

public class SavingStatus implements ImitStatus {

    @Override
    public String getDesc() {
        return "saving status";
    }

    @Override
    public void prep(LaunchContext ctx) {
        ctx.resetAllButs(
                false , false , false , false , false , false
        );
    }

    @Override
    public void exec(LaunchContext ctx) {
            MainFrame mainFr = ctx.getImitFrame();
//            SimpleMonitor monitor = ctx.getMonitor();
            Processor imitater = ctx.getProcessor();

            if (imitater.isSavable()) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle(LocaleUtils.loc(LocTag.SAVING_STATUS_DIALOG_TITLE));

                fileChooser.setSelectedFile(new File("ImitaterTask.imit"));
                String dstDirPath = FileUtils.getSysBundle().getProp(FileUtils.SysTag.DIR_FOR_SCRIPTS.name());
                fileChooser.setCurrentDirectory(new File(dstDirPath));
                fileChooser.setVisible(true);

                int userOption = fileChooser.showSaveDialog(mainFr);

                if (userOption == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    imitater.saveOpAsFile(file);
                    mainFr.perform(LocaleUtils
                            .loc(LocTag.SAVING_STATUS_AFTER_SAVING_PERFORM_PRE)
                            + file.getAbsolutePath() + LocaleUtils
                            .loc(LocTag.SAVING_STATUS_AFTER_SAVING_PERFORM_POST));
                    mainFr.setHasSaved(true);
                }
            }

            ctx.update(EnumStatus.NASCENT);
            ctx.refine();
    }

    @Override
    public void stop(LaunchContext ctx) {

    }
}
