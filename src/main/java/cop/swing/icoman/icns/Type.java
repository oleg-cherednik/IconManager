package cop.swing.icoman.icns;

import cop.swing.icoman.IconIO;
import cop.swing.icoman.ImageKey;
import cop.swing.icoman.exceptions.IconManagerException;
import cop.swing.icoman.icns.foo.rle24;
import cop.swing.icoman.imageio.bmp.Bitmap;
import org.apache.commons.lang3.ArrayUtils;

import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

/**
 * @author Oleg Cherednik
 * @since 17.08.2015
 */
public enum Type {
    // 32-bit image types > 256x256 - no mask (mask is already in image)
    ICNS_1024x1024_32BIT_ARGB_DATA("ic10", ImageKey.createXpKey(1024), null),
    ICNS_512x512_32BIT_ARGB_DATA("ic09", ImageKey.createXpKey(512), null),
    ICNS_256x256_32BIT_ARGB_DATA("ic08", ImageKey.createXpKey(256), null),

    // 32-bit image types - 8-bit mask type
    ICNS_128x128_32BIT_DATA("it32", ImageKey.createXpKey(128), ImageKey.createKey(128, 8)) {},
    ICNS_48x48_32BIT_DATA("ih32", ImageKey.createXpKey(48), ImageKey.createKey(48, 8)),
    ICNS_32x32_32BIT_DATA("il32", ImageKey.createXpKey(32), ImageKey.createKey(32, 8)),
    ICNS_16x16_32BIT_DATA("is32", ImageKey.createXpKey(16), ImageKey.createKey(16, 8)),

    // 8-bit image types - 1-bit mask types
    ICNS_48x48_8BIT_DATA("ich8", ImageKey.createKey(48, 8), ImageKey.createKey(48, 1)),
    ICNS_32x32_8BIT_DATA("icl8", ImageKey.createKey(32, 8), ImageKey.createKey(32, 1)),
    ICNS_16x16_8BIT_DATA("ics8", ImageKey.createKey(16, 8), ImageKey.createKey(16, 1)),
    ICNS_16x12_8BIT_DATA("icm8", ImageKey.createKey(16, 12, 8), ImageKey.createKey(16, 12, 1)),

    // 4 bit image types - 1-bit mask types
    ICNS_48x48_4BIT_DATA("ich4", ImageKey.createKey(48, 4), ImageKey.createKey(48, 1)),
    ICNS_32x32_4BIT_DATA("icl4", ImageKey.createKey(32, 4), ImageKey.createKey(32, 1)),
    ICNS_16x16_4BIT_DATA("ics4", ImageKey.createKey(16, 4), ImageKey.createKey(16, 1)),
    ICNS_16x12_4BIT_DATA("icm4", ImageKey.createKey(16, 12, 4), ImageKey.createKey(16, 12, 1)),

    // 1 bit image types - 1-bit mask types
    ICNS_48x48_1BIT_DATA("ich#", ImageKey.createKey(48, 1), ImageKey.createKey(48, 1), true) {
        @Override
        protected void readData(int[] buf, Map<ImageKey, int[]> mapData, Map<ImageKey, int[]> mapMask) {
            if (buf.length == 48 * 48 / 8)
                super.readData(buf, mapData, mapMask);
            else
                _readData(key, buf, mapData);
        }
    },
    ICNS_32x32_1BIT_DATA("ICN#", ImageKey.createKey(32, 1), ImageKey.createKey(32, 1), true) {
        @Override
        protected void readData(int[] buf, Map<ImageKey, int[]> mapData, Map<ImageKey, int[]> mapMask) {
            if (buf.length == 32 * 32 / 8)
                super.readData(buf, mapData, mapMask);
            else
                _readData(key, buf, mapData);
        }
    },
    ICNS_16x16_1BIT_DATA("ics#", ImageKey.createKey(16, 1), ImageKey.createKey(16, 1), true) {
        @Override
        protected void readData(int[] buf, Map<ImageKey, int[]> mapData, Map<ImageKey, int[]> mapMask) {
            if (buf.length == 16 * 16 / 8)
                super.readData(buf, mapData, mapMask);
            else
                _readData(key, buf, mapData);
        }
    },

    ICNS_16x12_1BIT_DATA("icm#", ImageKey.createKey(16, 12, 1), ImageKey.createKey(16, 12, 1), true),

    // masks
    ICNS_128x128_8BIT_MASK("t8mk", null, ImageKey.createKey(128, 8)),
    ICNS_48x48_8BIT_MASK("h8mk", null, ImageKey.createKey(48, 8)),
    ICNS_48x48_1BIT_MASK("ich#", null, ImageKey.createKey(48, 1)) {
        @Override
        protected void readData(int[] buf, Map<ImageKey, int[]> mapData, Map<ImageKey, int[]> mapMask) {
            if (buf.length == 48 * 48 / 8)
                super.readData(buf, mapData, mapMask);
            else
                _readMask(mask, buf, mapMask);
        }
    },
    ICNS_32x32_8BIT_MASK("l8mk", null, ImageKey.createKey(32, 8)),
    ICNS_32x32_1BIT_MASK("ICN#", null, ImageKey.createKey(32, 1)) {
        @Override
        protected void readData(int[] buf, Map<ImageKey, int[]> mapData, Map<ImageKey, int[]> mapMask) {
            if (buf.length == 32 * 32 / 8)
                super.readData(buf, mapData, mapMask);
            else
                _readMask(mask, buf, mapMask);
        }
    },
    ICNS_16x16_8BIT_MASK("s8mk", null, ImageKey.createKey(16, 8)),
    ICNS_16x16_1BIT_MASK("ics#", null, ImageKey.createKey(16, 1)) {
        @Override
        protected void readData(int[] buf, Map<ImageKey, int[]> mapData, Map<ImageKey, int[]> mapMask) {
            if (buf.length == 16 * 16 / 8)
                super.readData(buf, mapData, mapMask);
            else
                _readMask(mask, buf, mapMask);
        }
    },
    ICNS_16x12_1BIT_MASK("icm#", null, ImageKey.createKey(16, 12, 1));


    private final String id;
    private final long val;
    public final ImageKey key;
    public final ImageKey mask;
    private final boolean skip;

    Type(String id, ImageKey key, ImageKey mask) {
        this(id, key, mask, false);
    }

    Type(String id, ImageKey key, ImageKey mask, boolean skip) {
        this.id = id;
        this.key = key;
        this.mask = mask;
        val = toInt(id);
        this.skip = skip;
    }

    protected void readData(int[] buf, Map<ImageKey, int[]> mapData, Map<ImageKey, int[]> mapMask) {
        if (key != null) {
            if (mapData.put(key, buf) != null)
                throw new IllegalArgumentException("Duplication image key: " + key);
        } else if (mapMask.put(mask, buf) != null)
            throw new IllegalArgumentException("Duplication image mask: " + mask);
    }

    public BufferedImage createImage(ImageKey key, int[] data, int[] mask) throws IconManagerException {
        if (key == null)
            return null;

        if (key.getColors() == 0x2)
            return Bitmap.getInstanceForColors(0x2).createImage(key.width(), key.height(), ColorTable.BIT_1_2, data, mask, true);
        if (key.getColors() == 0x10)
            return Bitmap.getInstanceForColors(0x10).createImage(key.width(), key.height(), ColorTable.BIT_4_16, data, mask, true);
        if (key.getColors() == 0x100)
            return Bitmap.getInstanceForColors(0x100).createImage(key.width(), key.height(), ColorTable.BIT_8_256, data, mask, true);
        if (key.getColors() == 0x7FFFFFFF) {
            int iconBitDepth = 32;
            int iconDataRowSize = key.width() * iconBitDepth / 8;
            int iconDataSize = key.height() * iconDataRowSize;


            if (data.length < iconDataSize) {
                int pixelCount = key.height() * key.height();
                int[] buf = rle24.icns_decode_rle24_data1(data, key.width() * key.height(), 1000);
                return Bitmap.getInstanceForColors(0x7FFFFFFF).createImage(key.width(), key.height(), null, buf, mask, true);
            } else {
                int a = 0;
                a++;
//                    int pixelCount = 0;
//                    icns_byte_t * swapPtr = NULL;
//                    icns_argb_t * pixelPtr = NULL;
//
//                    for (int dataCount = 0; dataCount < imageOut.imageHeight; dataCount++)
//                        memcpy( & (((char*)(imageOut -> imageData))[dataCount * iconDataRowSize]),&(((char*)(rawDataPtr))[
//                    dataCount * iconDataRowSize]),iconDataRowSize);
//
//                    pixelCount = imageOut -> imageWidth * imageOut -> imageHeight;
//
//                    System.out.println(String.format("Converting %d pixels from argb to rgba", pixelCount));
//                            swapPtr = imageOut -> imageData;
//                    for (dataCount = 0; dataCount < pixelCount; dataCount++) {
//                        pixelPtr = (icns_argb_t *) (swapPtr + (dataCount * 4));
//                        *((icns_rgba_t *) pixelPtr)=ICNS_ARGB_TO_RGBA( * ((icns_argb_t *) pixelPtr));
//                    }
            }

//            return Bitmap.getInstanceForColors(0x7FFFFFFF).createImage(key.width(), key.height(), null, data, mask, true);
        }

        return null;
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

    public static Type parse(int val) {
        for (Type type : values())
            if (type.val == val)
                return type;
        return null;
    }

    public static Type parseImageKey(ImageKey key) {
        for (Type type : values())
            if (type.key == key)
                return type;
        throw new IllegalArgumentException(key.toString());
    }

    public static void readData(ImageInputStream in, Map<ImageKey, int[]> mapData, Map<ImageKey, int[]> mapMask) throws IOException {
        long val = in.readUnsignedInt();
        int size = in.readInt();
        int[] data = IconIO.readUnsignedBytes(size - 8, in);

        for (Type type : values()) {
            if (/*!type.skip &&*/ type.val == val) {
                type.readData(data, mapData, mapMask);
                System.out.println(String.format("type: %s, size: %d", type.id, size));
            }
        }
    }

    private static void _readData(ImageKey key, int[] buf, Map<ImageKey, int[]> mapData) {
        if (mapData.put(key, ArrayUtils.subarray(buf, 0, buf.length / 2)) != null)
            throw new IllegalArgumentException("Duplication image key: " + key);
    }

    private static void _readMask(ImageKey mask, int[] buf, Map<ImageKey, int[]> mapMask) {
        if (mapMask.put(mask, ArrayUtils.subarray(buf, buf.length / 2, buf.length)) != null)
            throw new IllegalArgumentException("Duplication image mask: " + mask);
    }
}
