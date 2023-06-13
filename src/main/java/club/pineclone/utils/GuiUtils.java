package club.pineclone.utils;

import javax.imageio.ImageIO;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class GuiUtils {

    public static final String DEF_TEXTURE_PATH = "club/pineclone/gui/";

    public static Image icon;
    public static Image image_01;
    public static Image image_02;

    public static Image wrench;
    public static Image launch;

    public static final DocumentFilter DIG_FILTER = new DocumentFilter() {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            if (string == null) return;
            if (string.matches("\\d+")) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attr)
                throws BadLocationException {
            if (string == null) return;
            if (string.matches("\\d+")) {
                super.replace(fb, offset, length, string, attr);
            }
        }
    };

    public static void initializeGui() {
        icon = readImage("icon", 50 , 50);
        image_01 = readImage("image_01");
        image_02 = readImage("image_02");
        wrench = readImage("wrench");
        launch = readImage("launch");
    }

    private static Image readImage(String name) {
        Image image = null;
        try (InputStream is = GuiUtils.class.getClassLoader().getResourceAsStream(DEF_TEXTURE_PATH + name + ".png")) {
            if (is != null) image = ImageIO.read(is);
        } catch (IOException ignored) {
        }
        return image;
    }

    private static Image readImage(String name, int scaleWidth , int scaleHeight) {
        Image image = null;
        try (InputStream is = GuiUtils.class.getClassLoader().getResourceAsStream("club/pineclone/gui/" + name + ".png")) {
            if (is != null) image = ImageIO.read(is);
        } catch (IOException ignored) {
        }
        if (image != null) return image.getScaledInstance(scaleWidth , scaleHeight , Image.SCALE_DEFAULT);
        return null;
    }

    public static Point getCenter(Container container) {
        Point location = container.getLocation();
        return new Point(location.x + container.getWidth() / 2, location.y + container.getHeight() / 2);
    }

    public static Point getRelativeCenter(Container main, Container reference) {
        Point center = getCenter(reference);
        return new Point(center.x - main.getWidth() / 2, center.y - main.getHeight() / 2);
    }

    public static Point getRelativeCenter(Container main, Point reference) {
        return new Point(reference.x - main.getWidth() / 2, reference.y - main.getHeight() / 2);
    }

    public static Point getScreenCenter() {
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension size = kit.getScreenSize();
        return new Point(size.width / 2, size.height / 2);
    }

    public static Point getLastShutDownPoint() {
        FileUtils.PropertiesBundle sysBundle = FileUtils.getSysBundle();
        int x = Integer.parseInt(sysBundle.getProp(FileUtils.SysTag.LAST_SHUTDOWN_POS_X.name()));
        int y = Integer.parseInt(sysBundle.getProp(FileUtils.SysTag.LAST_SHUTDOWN_POS_Y.name()));
        return new Point(x, y);
    }

    public static void loadShutDownPoint(Container container) {
        FileUtils.PropertiesBundle sysBundle = FileUtils.getSysBundle();
        Point point = container.getLocation();
        sysBundle.setProp(FileUtils.SysTag.LAST_SHUTDOWN_POS_X.name(), String.valueOf(point.x));
        sysBundle.setProp(FileUtils.SysTag.LAST_SHUTDOWN_POS_Y.name(), String.valueOf(point.y));
        sysBundle.save();
    }

    public static Font[] getAvailableFonts() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
    }


}
