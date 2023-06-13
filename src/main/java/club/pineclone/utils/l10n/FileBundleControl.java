package club.pineclone.utils.l10n;

import club.pineclone.utils.Log;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FileBundleControl extends ResourceBundle.Control {

    private final String fileEncoding;

    public FileBundleControl(String fileEncoding) {
        this.fileEncoding = fileEncoding;
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

        try (FileInputStream fis = new FileInputStream(fileEncoding);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8)) {

            toReturn = new PropertyResourceBundle(isr);

        } catch (Exception e) {
            Log.infoExceptionally("Cannot read the given file", e);
        }

        return toReturn;
    }

}
