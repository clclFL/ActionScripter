package club.pineclone.utils;

import club.pineclone.gui.status.ExecutingStatus;
import club.pineclone.utils.l10n.LocaleUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class FileUtils {

    /**
     * The default keystrokes map, the value is what stored in the properties file, in the first setup process if finding
     * missing essential key properties file then the program will automatically generate one in the {@link FileUtils#HOME_DIR}
     */
    public static final String HOME_DIR_PATH = System.getProperty("user.home") + "\\AppData\\Local\\Imitater";
    public static final String SCRIPT_DIR_PATH = HOME_DIR_PATH + "\\scripts";
    public static final String L10N_DIR_PATH = HOME_DIR_PATH + "\\l10n";
    public static final String SYS_CONF_PATH = HOME_DIR_PATH + "\\sys_conf.properties";
    public static final String KEY_CONF_PATH = HOME_DIR_PATH + "\\key_conf.properties";

    public static final File HOME_DIR = new File(HOME_DIR_PATH);
    public static final File L10N_DIR = new File(L10N_DIR_PATH);
    public static final File SCRIPT_DIR = new File(SCRIPT_DIR_PATH);

    public static final File SYS_CONF = new File(SYS_CONF_PATH);
    public static final File KEY_CONF = new File(KEY_CONF_PATH);

    private static boolean hasInit = false;

    /**
     * The map that include all the key-value entries for the default system configuration, the program relay this map to
     * check if the system configuration file in the client is missed or not, if the file is missed then the program could
     * simply use the value from the map or reload the data in the map into the configuration file again.
     */
    private static final Map<String, String> DEF_SYS_CONF_MAP = new HashMap<>();
    /**
     * The configuration map include all the key-value entries for the default hot key mapping, its function is likely the
     * same with the system configuration map.
     */
    public static final Map<String, String> DEF_HOTKEY_MAP = new HashMap<>();

    public enum SysTag {
        SET_ALLOW_ON_TOP, START_DELAY, DIR_FOR_SCRIPTS, ALLOW_BEEP, USING_LANGUAGE,
        LAST_SHUTDOWN_POS_X, LAST_SHUTDOWN_POS_Y, ENABLE_INFO_PANEL
    }

    private static final Properties SYS_CONFIG = new Properties();
    private static final Properties KEY_CONFIG = new Properties();

    private static final String SYS_CONF_INFO = "The configuration for the software.";
    private static final String KEY_CONF_INFO = "The configuration for client's shortcuts.";

    private static PropertiesBundle sysBundle;
    private static PropertiesBundle keyBundle;

    public static PropertiesBundle getSysBundle() {
        return sysBundle;
    }

    public static PropertiesBundle getKeyBundle() {
        return keyBundle;
    }

    static {
        DEF_SYS_CONF_MAP.put(SysTag.SET_ALLOW_ON_TOP.name(), "false");
        DEF_SYS_CONF_MAP.put(SysTag.START_DELAY.name(), "3");
        DEF_SYS_CONF_MAP.put(SysTag.DIR_FOR_SCRIPTS.name(), SCRIPT_DIR_PATH);
        DEF_SYS_CONF_MAP.put(SysTag.ALLOW_BEEP.name(), "false");
        DEF_SYS_CONF_MAP.put(SysTag.USING_LANGUAGE.name(), "en_EN");
        DEF_SYS_CONF_MAP.put(SysTag.LAST_SHUTDOWN_POS_X.name(), "75");
        DEF_SYS_CONF_MAP.put(SysTag.LAST_SHUTDOWN_POS_Y.name(), "75");
        DEF_SYS_CONF_MAP.put(SysTag.ENABLE_INFO_PANEL.name(), "false");

        DEF_SYS_CONF_MAP.put(ExecutingStatus.TextFieldType.EXECUTING_MODE.name(), "Limited");
        DEF_SYS_CONF_MAP.put(ExecutingStatus.TextFieldType.EXECUTING_TIMES.name(), "1");
        DEF_SYS_CONF_MAP.put(ExecutingStatus.TextFieldType.INITIAL_DELAY.name(), "0");
        DEF_SYS_CONF_MAP.put(ExecutingStatus.TextFieldType.INTERVAL_DELAY.name(), "0");

        FileUtils.DEF_HOTKEY_MAP.put(KeyUtils.KeyTag.BEGIN_RECORD.name(), "17+91");
        FileUtils.DEF_HOTKEY_MAP.put(KeyUtils.KeyTag.EXECUTE.name(), "18+91");
        FileUtils.DEF_HOTKEY_MAP.put(KeyUtils.KeyTag.STOP.name(), "18+93");

        initializeFile();
    }

    /**
     * This method should be called in the primary stage of life cycle, to make sure the field could be called rather than
     * throw null pointer exception, This is the main life cycle of file initialization, first this method will check if any
     * file is missing and trying to recreate the missing file, considering the properties file is contained in the project,
     * this method will then load the properties in order to make client could read the prop correctly, Remember to call this
     * method in the very primary period of program.
     */
    public static void initializeFile() {
        //Check home directory.
        ensureDir(HOME_DIR , "home directory");

        //check script directory.
        ensureDir(SCRIPT_DIR , "script directory");

        //check l10n directory
        ensureDir(L10N_DIR , "i10n directory");

        /* ensure the properties & configuration */
        sysBundle = new PropertiesBundle(SYS_CONFIG, SYS_CONF, SYS_CONF_INFO, DEF_SYS_CONF_MAP);
        keyBundle = new PropertiesBundle(KEY_CONFIG, KEY_CONF, KEY_CONF_INFO, DEF_HOTKEY_MAP);

        /* initialize the locale file */
        LocaleUtils.initializeLocale();
        GuiUtils.initializeGui();

        /* after loading system config, initialize field */
        hasInit = true;
    }

    private static void ensureDir(File dir, String name) {
        if (!dir.exists()) {
            try {
                boolean flag = dir.mkdir();
                Log.info("Result of creating " + name + " : " + (flag ? "success" : "fail"));
                if (!flag) throw new IOException();
            } catch (IOException e) {
                Log.infoExceptionally("Exception occurs while trying creating " + name + ".", e);
            }
        }
    }

    public static boolean hasInit() {
        return hasInit;
    }

    /**
     * This class enclose the datas that are essential for properties operation, for creating class's instance, its constructor
     * require a few param to make sure the data is completable.
     */
    public static final class PropertiesBundle {
        private final Properties prop;
        private final File file;
        private final String comment;
        private final Map<String, String> def;

        @SuppressWarnings("ResultOfMethodCallIgnored")
        public PropertiesBundle(Properties prop, File file,
                                String comment, Map<String, String> def) {
            this.prop = prop;
            this.file = file;
            this.comment = comment;
            this.def = def;

            if (!file.exists()) {
                try {
                    file.createNewFile();
                    def.forEach(prop::setProperty);
                    save();
                } catch (IOException e) {
                    Log.infoExceptionally(
                            "Cannot create target properties file at "
                                    + file.getAbsolutePath(), e);
                }
            } else load();
            ensure();
        }


        public Object setProp(String k, String v) {
            return this.prop.setProperty(k, v);
        }

        public String getProp(String k) {
            String v = this.prop.getProperty(k);
            if (v != null) return v;
            setProp(k, def.get(k));
            save();
            return def.get(k);
        }

        public void load() {
            try (FileReader fr = new FileReader(this.file);
                 BufferedReader br = new BufferedReader(fr)
            ) {
                this.prop.load(br);
            } catch (IOException e) {
                Log.infoExceptionally("Cannot correctly read file into properties", e);
            }
        }

        public void save() {
            try (FileWriter fw = new FileWriter(this.file);
                 BufferedWriter bw = new BufferedWriter(fw)) {
                prop.store(bw, this.comment);
            } catch (IOException e) {
                Log.infoExceptionally("Cannot correctly Save properties into file.", e);
            }
        }

        public boolean containsKey(Object k) {
            return this.prop.containsKey(k);
        }

        public boolean containsValue(Object v) {
            return this.prop.containsValue(v);
        }

        public void ensure() {
            boolean flag = false;
            for (Map.Entry<String, String> e : def.entrySet()) {
                String k = e.getKey();
                if (!containsKey(k) || getProp(k) == null) {
                    setProp(k, e.getValue());
                    flag = true;
                }
            }
            if (flag) save();
        }
    }
}
