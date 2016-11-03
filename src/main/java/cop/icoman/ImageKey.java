package cop.icoman;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents single image in the icon. There're no images with same key (width, height, colors), that's why this class
 * is singleton for each key. So each set of width, height and color returns <b>same</b> class instance.
 *
 * @author Oleg Cherednik
 * @since 14.12.2012
 */
@Data
@EqualsAndHashCode
public class ImageKey implements Comparable<ImageKey> {
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
        ImageKey key = MAP.get(parse(width, height, bitsPerPixel));
        return key != null ? key : new ImageKey(width, height, bitsPerPixel);
    }

    protected ImageKey(int width, int height, int bitsPerPixel) {
        this.width = width;
        this.height = height;
        this.bitsPerPixel = bitsPerPixel;
        MAP.put(parse(width, height, bitsPerPixel), this);
    }

    public String getId() {
        return parse(width, height, bitsPerPixel);
    }

    // ========== Comparable ==========

    @Override
    public int compareTo(ImageKey key) {
        if (key == this)
            return 0;

        int res;

        if ((res = Integer.compare(bitsPerPixel, key.bitsPerPixel)) != 0)
            return res;
        if ((res = Integer.compare(width, key.width)) != 0)
            return res;
        return Integer.compare(height, key.height);
    }

    // ========== Object ==========

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append(width).append('x').append(height);

        if (bitsPerPixel == HIGH_COLOR)
            buf.append(" High Color");
        else if (bitsPerPixel == TRUE_COLOR)
            buf.append(" True Color");
        else if (bitsPerPixel == XP)
            buf.append(" XP");
        else
            buf.append(' ').append((int)Math.pow(2, bitsPerPixel));

        return buf.toString();
    }

    // ========== static ==========

    public static String parse(int width, int height, int bitsPerPixel) {
        return String.format("%dx%d_%d", width, height, bitsPerPixel);
    }

    public static String parse(String id, int width, int height, int bitsPerPixel) {
        return String.format("%s_%dx%d_%d", id.toLowerCase(), width, height, bitsPerPixel);
    }
}
