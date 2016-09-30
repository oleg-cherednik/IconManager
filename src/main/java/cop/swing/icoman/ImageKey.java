package cop.swing.icoman;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents single image in the icon. There're no images with same key (width, height, colors), that's why this class
 * is singleton for each key. So each set of width, height and color returns <b>same</b> class instance.
 *
 * @author Oleg Cherednik
 * @since 14.12.2012
 */
public final class ImageKey implements Comparable<ImageKey> {
    public static final int HIGH_COLOR = 16;
    public static final int TRUE_COLOR = 24;
    public static final int XP = 32;

    private static final Map<String, ImageKey> MAP = new HashMap<>();

    private final int width;
    private final int height;
    private final int bitsPerPixel;

    public static ImageKey highColor(int size) {
        return custom(size, size, HIGH_COLOR);
    }

    public static ImageKey trueColor(int size) {
        return custom(size, size, TRUE_COLOR);
    }

    public static ImageKey xp(int size) {
        return custom(size, size, XP);
    }

    public static ImageKey custom(int size, int bitsPerPixel) {
        return custom(size, size, bitsPerPixel);
    }

    public static ImageKey custom(int width, int height, int bitsPerPixel) {
        ImageKey key = MAP.get(getString(width, height, bitsPerPixel));
        return key != null ? key : new ImageKey(width, height, bitsPerPixel);
    }

    private ImageKey(int width, int height, int bitsPerPixel) {
        this.width = width;
        this.height = height;
        this.bitsPerPixel = bitsPerPixel;

        if (MAP.put(getString(width, height, this.bitsPerPixel), this) != null)
            assert false : "key duplication";
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int getBitsPerPixel() {
        return bitsPerPixel;
    }

    // ========== Comparable ==========

    @Override
    public int compareTo(ImageKey key) {
        if (key == this)
            return 0;

        int res;

        if ((res = Integer.compare(width, key.width)) != 0)
            return res;
        if ((res = Integer.compare(height, key.height)) != 0)
            return res;

        return Integer.compare(bitsPerPixel, key.bitsPerPixel);
    }

    // ========== Object ==========

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + bitsPerPixel;
        result = prime * result + height;
        result = prime * result + width;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        ImageKey other = (ImageKey)obj;

        return bitsPerPixel == other.bitsPerPixel && height == other.height && width == other.width;
    }

    @Override
    public String toString() {
        return getString(width, height, bitsPerPixel);
    }

    // ========== static ==========

    private static String getString(int width, int height, int bitsPerPixel) {
        StringBuilder buf = new StringBuilder();

        buf.append(width).append('x').append(height);

        if (bitsPerPixel == HIGH_COLOR)
            buf.append(" High Color");
        else if (bitsPerPixel == TRUE_COLOR)
            buf.append(" True Color");
        else if (bitsPerPixel == XP)
            buf.append(" XP");
        else
            buf.append(' ').append(bitsPerPixel).append(" colors");

        return buf.toString();
    }
}
