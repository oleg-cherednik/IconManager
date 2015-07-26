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
    private static final int BITS_HIGH_COLOR = 16;
    private static final int BITS_TRUE_COLOR = 24;
    private static final int BITS_XP = 32;

    private static final int HIGH_COLOR = Integer.MAX_VALUE - 2;
    private static final int TRUE_COLOR = Integer.MAX_VALUE - 1;
    private static final int XP = Integer.MAX_VALUE;

    private static final Map<String, ImageKey> MAP = new HashMap<>();

    private final int width; // size: 1, offs: 0x0 (0-255, 0=256 pixels)
    private final int height; // size: 1, offs: 0x1 (0-255, 0=256 pixels)
    private final int colors; // size: 1, offs: 0x2 (0=256 - high/true color)

    public static ImageKey createHighColorKey(int size) {
        return createKey(size, size, BITS_HIGH_COLOR);
    }

    public static ImageKey createTrueColorKey(int size) {
        return createKey(size, size, BITS_TRUE_COLOR);
    }

    public static ImageKey createXpKey(int size) {
        return createKey(size, size, BITS_XP);
    }

    static ImageKey createKey(int width, int height, int bitsPerPixel) {
        check(width, height, bitsPerPixel);

        int colors = getColors(bitsPerPixel);

        ImageKey key = MAP.get(getString(width, height, colors));
        return key != null ? key : new ImageKey(width, height, colors);
    }

    private ImageKey(int width, int height, int colors) {
        this.width = width;
        this.height = height;
        this.colors = colors;

        if (MAP.put(getString(width, height, colors), this) != null)
            assert false : "key duplication";
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getColors() {
        if (colors == HIGH_COLOR)
            return 0x10000;
        if (colors == TRUE_COLOR || colors == XP)
            return 0x1000000;
        return colors;
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

        return Integer.compare(colors, key.colors);
    }

    // ========== Object ==========

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + colors;
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

        return colors == other.colors && height == other.height && width == other.width;
    }

    @Override
    public String toString() {
        return getString(width, height, colors);
    }

    // ========== static ==========

    private static String getString(int width, int height, int colors) {
        StringBuilder buf = new StringBuilder();

        buf.append(width).append('x').append(height).append(' ');

        if (colors == HIGH_COLOR)
            buf.append(" High Color");
        else if (colors == TRUE_COLOR)
            buf.append(" True Color");
        else if (colors == XP)
            buf.append(" XP");
        else
            buf.append(' ').append(colors).append(" colors");

        return buf.toString();
    }

    private static void check(int width, int height, int bitsPerPixel) {
    }

    private static int getColors(int bitsPerPixel) {
        if (bitsPerPixel == 1)
            return 2;
        if (bitsPerPixel == 4)
            return 16;
        if (bitsPerPixel == 8)
            return 256;
        if (bitsPerPixel == BITS_HIGH_COLOR)
            return HIGH_COLOR;
        if (bitsPerPixel == BITS_TRUE_COLOR)
            return TRUE_COLOR;
        if (bitsPerPixel == BITS_XP)
            return XP;

        assert false : "invalid value: bitPerPixel = " + bitsPerPixel;

        return (int)Math.pow(2, bitsPerPixel);
    }
}
