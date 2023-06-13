package club.pineclone.utils.i18n;

import club.pineclone.utils.Log;

import java.io.Reader;
import java.util.*;

public class ReaderBundleControl extends ResourceBundle.Control {

    private final Reader reader;

    public ReaderBundleControl(Reader reader) {
        this.reader = reader;
    }

    @Override
    public List<String> getFormats(String baseName) {
        return Collections.singletonList("properties");
    }

    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format,
                                    ClassLoader loader, boolean reload) {

        if (!format.equals("properties")) {
            return null;
        }

        ResourceBundle toReturn = null;
        try {
            toReturn = new PropertyResourceBundle(reader);

        } catch (Exception e) {
            Log.infoExceptionally("Cannot read the given file", e);
        }
        return toReturn;
    }


}
