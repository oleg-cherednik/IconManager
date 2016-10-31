package cop.icoman.icns;

import cop.icoman.IconIO;
import cop.icoman.ImageKey;
import cop.icoman.bmp.Bitmap;
import cop.icoman.exceptions.IconManagerException;
import org.apache.commons.lang3.ArrayUtils;

import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map;

/**
 * @author Oleg Cherednik
 * @since 17.08.2015
 */
@SuppressWarnings({ "EnumeratedConstantNamingConvention", "unused" })
enum Type {
    // 32-bit image types > 256x256 - no mask (mask is already in image)
    ICNS_1024x1024_32BIT_ARGB_DATA("ic10", ImageKey.xp(1024), null),
    ICNS_512x512_32BIT_ARGB_DATA("ic09", ImageKey.xp(512), null),
    ICNS_256x256_32BIT_ARGB_DATA("ic08", ImageKey.xp(256), null),

    // 32-bit image types - 8-bit mask type
    ICNS_128x128_32BIT_DATA("it32", ImageKey.xp(128), ImageKey.custom(128, 8)),
    ICNS_48x48_32BIT_DATA("ih32", ImageKey.xp(48), ImageKey.custom(48, 8)),
    ICNS_32x32_32BIT_DATA("il32", ImageKey.xp(32), ImageKey.custom(32, 8)),
    ICNS_16x16_32BIT_DATA("is32", ImageKey.xp(16), ImageKey.custom(16, 8)),

    // 8-bit image types - 1-bit mask types
    ICNS_48x48_8BIT_DATA("ich8", ImageKey.custom(48, 8), ImageKey.custom(48, 1)),
    ICNS_32x32_8BIT_DATA("icl8", ImageKey.custom(32, 8), ImageKey.custom(32, 1)),
    ICNS_16x16_8BIT_DATA("ics8", ImageKey.custom(16, 8), ImageKey.custom(16, 1)),
    ICNS_16x12_8BIT_DATA("icm8", ImageKey.custom(16, 12, 8), ImageKey.custom(16, 12, 1)),

    // 4 bit image types - 1-bit mask types
    ICNS_48x48_4BIT_DATA("ich4", ImageKey.custom(48, 4), ImageKey.custom(48, 1)),
    ICNS_32x32_4BIT_DATA("icl4", ImageKey.custom(32, 4), ImageKey.custom(32, 1)),
    ICNS_16x16_4BIT_DATA("ics4", ImageKey.custom(16, 4), ImageKey.custom(16, 1)),
    ICNS_16x12_4BIT_DATA("icm4", ImageKey.custom(16, 12, 4), ImageKey.custom(16, 12, 1)),

    // 1 bit image types - 1-bit mask types
    ICNS_48x48_1BIT_DATA("ich#", ImageKey.custom(48, 1), ImageKey.custom(48, 1), true) {
        @Override
        protected void readData(int[] buf, Map<Type, int[]> mapData, Map<ImageKey, int[]> mapMask) {
            if (buf.length == 48 * 48 / 8)
                super.readData(buf, mapData, mapMask);
            else
                _readData(this, buf, mapData);
        }
    },
    ICNS_32x32_1BIT_DATA("ICN#", ImageKey.custom(32, 1), ImageKey.custom(32, 1), true) {
        @Override
        protected void readData(int[] buf, Map<Type, int[]> mapData, Map<ImageKey, int[]> mapMask) {
            if (buf.length == 32 * 32 / 8)
                super.readData(buf, mapData, mapMask);
            else
                _readData(this, buf, mapData);
        }
    },
    ICNS_16x16_1BIT_DATA("ics#", ImageKey.custom(16, 1), ImageKey.custom(16, 1), true) {
        @Override
        protected void readData(int[] buf, Map<Type, int[]> mapData, Map<ImageKey, int[]> mapMask) {
            if (buf.length == 16 * 16 / 8)
                super.readData(buf, mapData, mapMask);
            else
                _readData(this, buf, mapData);
        }
    },

    ICNS_16x12_1BIT_DATA("icm#", ImageKey.custom(16, 12, 1), ImageKey.custom(16, 12, 1), true),

    // masks
    ICNS_128x128_8BIT_MASK("t8mk", null, ImageKey.custom(128, 8)),
    ICNS_48x48_8BIT_MASK("h8mk", null, ImageKey.custom(48, 8)),
    ICNS_48x48_1BIT_MASK("ich#", null, ImageKey.custom(48, 1)) {
        @Override
        protected void readData(int[] buf, Map<Type, int[]> mapData, Map<ImageKey, int[]> mapMask) {
            if (buf.length == 48 * 48 / 8)
                super.readData(buf, mapData, mapMask);
            else
                _readMask(mask, buf, mapMask);
        }
    },
    ICNS_32x32_8BIT_MASK("l8mk", null, ImageKey.custom(32, 8)),
    ICNS_32x32_1BIT_MASK("ICN#", null, ImageKey.custom(32, 1)) {
        @Override
        protected void readData(int[] buf, Map<Type, int[]> mapData, Map<ImageKey, int[]> mapMask) {
            if (buf.length == 32 * 32 / 8)
                super.readData(buf, mapData, mapMask);
            else
                _readMask(mask, buf, mapMask);
        }
    },
    ICNS_16x16_8BIT_MASK("s8mk", null, ImageKey.custom(16, 8)),
    ICNS_16x16_1BIT_MASK("ics#", null, ImageKey.custom(16, 1)) {
        @Override
        protected void readData(int[] buf, Map<Type, int[]> mapData, Map<ImageKey, int[]> mapMask) {
            if (buf.length == 16 * 16 / 8)
                super.readData(buf, mapData, mapMask);
            else
                _readMask(mask, buf, mapMask);
        }
    },
    ICNS_16x12_1BIT_MASK("icm#", null, ImageKey.custom(16, 12, 1));

    private final long val;
    protected final ImageKey key;
    protected final ImageKey mask;
    private final boolean skip;

    Type(String id, ImageKey key, ImageKey mask) {
        this(id, key, mask, false);
    }

    Type(String id, ImageKey key, ImageKey mask, boolean skip) {
        this.key = key;
        this.mask = mask;
        val = toInt(id);
        this.skip = skip;
    }

    protected void readData(int[] buf, Map<Type, int[]> mapData, Map<ImageKey, int[]> mapMask) {
        if (key != null) {
            if (mapData.put(this, buf) != null)
                throw new IllegalArgumentException("Duplication image key: " + key);
        } else if (mapMask.put(mask, buf) != null)
            throw new IllegalArgumentException("Duplication image mask: " + mask);
    }

    @SuppressWarnings("MethodCanBeVariableArityMethod")
    public BufferedImage createImage(int[] data, int[] mask) throws IconManagerException, IOException {
        Bitmap bitmap = key != null ? Bitmap.getInstanceForBits(key.getBitsPerPixel()) : null;

        if (bitmap == null)
            return null;

        int[] colors = ColorTable.get(key.getBitsPerPixel());
        mask = key.getBitsPerPixel() == 1 ? mask : bitmap.invertMask(mask);

        if (mask == null)
            return IconIO.readImage(IconIO.toByteArray(data));

        if (key.getBitsPerPixel() == ImageKey.XP)
            data = rle24.decompress(key.getWidth(), key.getHeight(), data, mask);

        return bitmap.createImage(key.getWidth(), key.getHeight(), colors, data, mask);
    }

    // ========== static ==========

    private static int toInt(String id) {
        int val = 0x0;
        char[] arr = id.toCharArray();
        int i = 0;

        ArrayUtils.reverse(arr);

        for (char ch : arr)
            val += (int)ch << 8 * i++;

        return val;
    }

    public static Type parseImageKey(ImageKey key) {
        for (Type type : values())
            if (type.key == key)
                return type;
        throw new IllegalArgumentException(key.toString());
    }

    public static void readData(ImageInputStream in, Map<Type, int[]> mapData, Map<ImageKey, int[]> mapMask) throws IOException {
        long val = in.readUnsignedInt();
        int size = in.readInt();
        int[] data = IconIO.readUnsignedBytes(size - 8, in);

        for (Type type : values())
            if (!type.skip && type.val == val)
                type.readData(data, mapData, mapMask);
    }

    @SuppressWarnings("StaticMethodNamingConvention")
    private static void _readData(Type type, int[] buf, Map<Type, int[]> mapData) {
        if (mapData.put(type, ArrayUtils.subarray(buf, 0, buf.length / 2)) != null)
            throw new IllegalArgumentException("Duplication image key: " + type.key);
    }

    @SuppressWarnings("StaticMethodNamingConvention")
    private static void _readMask(ImageKey mask, int[] buf, Map<ImageKey, int[]> mapMask) {
        if (mapMask.put(mask, ArrayUtils.subarray(buf, buf.length / 2, buf.length)) != null)
            throw new IllegalArgumentException("Duplication image mask: " + mask);
    }

    public static final Comparator<Type> SORT_BY_KEY_ASC = (type1, type2) -> type1.key.compareTo(type2.key);
}
