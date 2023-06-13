package club.pineclone;

import club.pineclone.utils.Log;

public class Driver {

    static {
        try {
            Class.forName("club.pineclone.utils.FileUtils");
        } catch (ClassNotFoundException e) {
            Log.infoExceptionally("Exception occur while launch the driver" , e);
            throw new RuntimeException(e);
        }
    }

}
