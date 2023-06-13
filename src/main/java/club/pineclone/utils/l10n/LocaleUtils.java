package club.pineclone.utils.l10n;

import club.pineclone.gui.event.PropertyChangeEventDispatcher;
import club.pineclone.utils.FileUtils;
import club.pineclone.utils.Log;

import java.beans.PropertyChangeEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class LocaleUtils {

    private static final String DUMMY_1 = "l18n.language";
    private static final Locale DEF_LANG = new Locale("en", "EN");
    private static final PropertyChangeEventDispatcher DUMMY_3 = new PropertyChangeEventDispatcher();
    private static final Properties DEF_LANG_PROP = new Properties();

    private static ResourceBundle bundle;
    private static Locale locale;

    public static void preloadDefaultLangProp() {
        try (InputStream is = LocaleUtils.class.getClassLoader().getResourceAsStream("l10n/language_en_EN.properties");
             InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)) {
            DEF_LANG_PROP.load(br);
        } catch (IOException e) {
            Log.infoExceptionally("Cannot correctly read the def lang prop from resources", e);
        }
    }

    public static void initializeLocale() {
        preloadDefaultLangProp();
        updateBundle(getLocaleFromProp());
        locale = getLocaleFromProp();
        saveLangToFile(new Locale("en", "EN"));
        saveLangToFile(new Locale("zh", "CN"));
    }

    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName("club.pineclone.Driver");
        System.out.println(getDefBundle().getString("TEST"));
    }

    private static File getLangFile(Locale locale) {
        String propertiesPath = FileUtils.L10N_DIR_PATH + "\\language_" + locale.toString() + ".properties";
        return new File(propertiesPath);
    }

    public static ResourceBundle getDefBundle() {
        ResourceBundle bundle = null;
        try (InputStream is = LocaleUtils.class.getClassLoader().getResourceAsStream("l10n/" + toPropertiesName(DEF_LANG));
             InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)) {

            bundle = ResourceBundle.getBundle(DUMMY_1, DEF_LANG, new ReaderBundleControl(br));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bundle;
    }

    public static boolean verifyLangProp(File file) {
        Properties verifyProp = new Properties();
        try (FileReader fr = new FileReader(file);
             BufferedReader br = new BufferedReader(fr);
        ) {
            verifyProp.load(br);
        } catch (IOException e) {
            return false;
        }
        Set<String> keys1 = verifyProp.stringPropertyNames();
        Set<String> keys2 = DEF_LANG_PROP.stringPropertyNames();
        return keys1.equals(keys2);
    }

    public static List<Locale> getAvailableLocales() {
        final List<Locale> locales = new ArrayList<>();
        File[] files = FileUtils.L10N_DIR.listFiles();
        if (files != null && files.length > 0) {
            Arrays.stream(files).forEach(f -> {
                if (!verifyLangProp(f)) return;
                String name = f.getName();
                if (name.matches("^(language_[a-z]{2,3})_([A-Za-z]{2,3})\\.properties$")) {
                    String spilt = name.substring(0, name.length() - ".properties".length());
                    String[] splitName = spilt.split("_");
                    String lang = splitName[1];
                    String country = splitName[2];
                    locales.add(new Locale(lang, country));
                }
            });
        }
        return locales;
    }

    public static Locale getLocale() {
        return locale;
    }

    private static Locale getLocaleFromProp() {
        FileUtils.PropertiesBundle sysBundle = FileUtils.getSysBundle();
        String locale = sysBundle.getProp(FileUtils.SysTag.USING_LANGUAGE.name());
        String[] split = locale.split("_");
        return new Locale(split[0], split[1]);
    }

    /**
     * This method allow customer to set to their prefer language while using the program, if the newLoc is changed, the
     * instance will automatically call the {@link PropertyChangeEventDispatcher} to dispatch event to the listener and tell them
     * to change the language, the task is asynchronously ran.
     */
    public static void updateBundle(Locale newLoc, PropertyChangeEventDispatcher dispatcher) {
        FileUtils.PropertiesBundle sysBundle = FileUtils.getSysBundle();
        String propPath = toPropertiesAbsolutePath(newLoc);
        File file = new File(propPath);
        ResourceBundle.clearCache();

        if (!file.exists() || !verifyLangProp(file)) {
            try (InputStream is = LocaleUtils.class.getClassLoader().getResourceAsStream("l10n/" + toPropertiesName(DEF_LANG));
                 InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
                 BufferedReader br = new BufferedReader(isr);
            ) {
                bundle = ResourceBundle.getBundle(DUMMY_1, DEF_LANG, new ReaderBundleControl(br));
                dispatcher.dispatch(new PropertyChangeEvent(dispatcher, "change_language",
                        null, null));

                sysBundle.setProp(FileUtils.SysTag.USING_LANGUAGE.name(), DEF_LANG.toString());
                sysBundle.save();
                return;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ResourceBundle oldBundle = bundle;
        bundle = ResourceBundle.getBundle(DUMMY_1, newLoc, new FileBundleControl(propPath));
        ResourceBundle newBundle = bundle;

        dispatcher.dispatch(new PropertyChangeEvent(dispatcher, "language_change", oldBundle, newBundle));
        sysBundle.setProp(FileUtils.SysTag.USING_LANGUAGE.name(), newLoc.toString());
        sysBundle.save();
    }

    private static void updateBundle(Locale newLoc) {
        updateBundle(newLoc, DUMMY_3);
    }

    public static String loc(LocTag tag) {
        return bundle.getString(tag.name());
    }

    private static void saveLangToFile(Locale locale) {
        String propertiesName = toPropertiesName(locale);
        saveResourcesToFile("l10n/" + propertiesName, FileUtils.L10N_DIR_PATH + "\\" + propertiesName);
    }

    private static void saveResourcesToFile(String src, String to) {
        try (InputStream is = FileUtils.class.getClassLoader().getResourceAsStream(src);
        ) {
            if (is == null) throw new IOException("Can not find resources file");
            if (new File(to).exists()) return;
            try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
                 BufferedReader br = new BufferedReader(isr);

                 FileOutputStream fos = new FileOutputStream(to);
                 OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                 BufferedWriter bw = new BufferedWriter(osw)
            ) {

                String line;
                while ((line = br.readLine()) != null) {
                    bw.write(line);
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            Log.infoExceptionally("Exception occur while loading sources file", e);
        }
    }

    private static String toPropertiesName(String locale) {
        return "language_" + locale + ".properties";
    }

    private static String toPropertiesName(Locale locale) {
        return "language_" + locale.toString() + ".properties";
    }

    private static String toPropertiesAbsolutePath(String propertiesName) {
        return FileUtils.L10N_DIR_PATH + "\\" + propertiesName;
    }

    private static String toPropertiesAbsolutePath(Locale locale) {
        return toPropertiesAbsolutePath(toPropertiesName(locale));
    }

    public static ResourceBundle getBundle() {
        return bundle;
    }
}
