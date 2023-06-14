package club.pineclone.process.action;

import club.pineclone.process.api.RobotAction;
import club.pineclone.utils.Log;

import java.io.*;

/**
 * The tool class for action list's coding and decoding
 *
 * @see ActionList
 */
public class ActionSerializer {

    public static void code(ActionList actions, String filePath) {
        code(actions, new File(filePath));
    }

    public static void code(ActionList actions, File file) {
        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            for (RobotAction action : actions) {
                oos.writeObject(action);
            }

        } catch (IOException e) {
            Log.infoExceptionally("Exception occurs while trying serialize action" +
                    "list into the destination file : " + file.getAbsolutePath() +
                    ", this may happen because incorrect pattern of file is loaded.", e);
        }
    }

    public static ActionList decode(File file) {
        final ActionList toReturn = new ActionList();
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try {
                    Object obj = ois.readObject();
                    toReturn.add((RobotAction) obj);
                } catch (EOFException e) {
                    //The end of loading file.
                    break;
                }
            }
        } catch (IOException | ClassCastException e) {
            Log.infoExceptionally("Exception occurs while trying decoding the given file, " +
                    "considering about the pattern of file : " + file + " . make sure this file has right pattern.", e);
        } catch (ClassNotFoundException e) {
            Log.infoExceptionally("Cannot correctly run the cast task, make sure the software is in right written", e);
        }
        return toReturn;
    }

    public static ActionList decode(String filePath) {
        return decode(new File(filePath));
    }

}
