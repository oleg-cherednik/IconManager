package cop.swing.icoman.icns;

import cop.swing.icoman.IconIO;
import cop.swing.icoman.ImageKey;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.util.Map;

/**
 * @author Oleg Cherednik
 * @since 17.08.2015
 */
enum Type {
    // 32-bit image types > 256x256 - no mask (mask is already in image)
    ICNS_1024x1024_32BIT_ARGB_DATA("ic10", 0x69633130, ImageKey.createXpKey(1024), null),
    ICNS_512x512_32BIT_ARGB_DATA("ic9", 0x69633039, ImageKey.createXpKey(512), null),
    ICNS_256x256_32BIT_ARGB_DATA("ic8", 0x69633038, ImageKey.createXpKey(256), null),

    // 32-bit image types - 8-bit mask type
    ICNS_128x128_32BIT_DATA("it32", 0x69743332, ImageKey.createXpKey(128), ImageKey.createKey(128, 8)),
    ICNS_48x48_32BIT_DATA("ih32", 0x69683332, ImageKey.createXpKey(48), ImageKey.createKey(48, 8)),
    ICNS_32x32_32BIT_DATA("il32", 0x696C3332, ImageKey.createXpKey(32), ImageKey.createKey(32, 8)),
    ICNS_16x16_32BIT_DATA("is32", 0x69733332, ImageKey.createXpKey(16), ImageKey.createKey(16, 8)),

    // 8-bit image types - 1-bit mask types
    ICNS_48x48_8BIT_DATA("ich8", 0x69636838, ImageKey.createKey(48, 8), ImageKey.createKey(48, 1)),
    ICNS_32x32_8BIT_DATA("icl8", 0x69636C38, ImageKey.createKey(32, 8), ImageKey.createKey(32, 1)),
    ICNS_16x16_8BIT_DATA("ics8", 0x69637338, ImageKey.createKey(16, 8), ImageKey.createKey(16, 1)),
    ICNS_16x12_8BIT_DATA("icm4", 0x69636D38, ImageKey.createKey(16, 12, 8), ImageKey.createKey(16, 12, 1)),

    // 4 bit image types - 1-bit mask types
    ICNS_48x48_4BIT_DATA("ich4", 0x69636834, ImageKey.createKey(48, 4), ImageKey.createKey(48, 1)),
    ICNS_32x32_4BIT_DATA("icl4", 0x69636C34, ImageKey.createKey(32, 4), ImageKey.createKey(32, 1)),
    ICNS_16x16_4BIT_DATA("ics4", 0x69637334, ImageKey.createKey(16, 4), ImageKey.createKey(16, 1)),
    ICNS_16x12_4BIT_DATA("icm4", 0x69636D34, ImageKey.createKey(16, 12, 4), ImageKey.createKey(16, 12, 1)),

    // 1 bit image types - 1-bit mask types
    ICNS_48x48_1BIT_DATA("t8mk", 0x69636823, ImageKey.createKey(48, 1), ImageKey.createKey(48, 1)),
    ICNS_32x32_1BIT_DATA("ICN#", 0x49434E23, ImageKey.createKey(32, 1), ImageKey.createKey(32, 1)),
    ICNS_16x16_1BIT_DATA("ics#", 0x69637323, ImageKey.createKey(16, 1), ImageKey.createKey(16, 1)),
    ICNS_16x12_1BIT_DATA("icm#", 0x69636D23, ImageKey.createKey(16, 12, 1), ImageKey.createKey(16, 12, 1)),

    // masks
    ICNS_128x128_8BIT_MASK("t8mk", 0x74386D6B, null, ImageKey.createKey(128, 8)),
    ICNS_48x48_8BIT_MASK("h8mk", 0x68386D6B, null, ImageKey.createKey(48, 8)),
    ICNS_48x48_1BIT_MASK("t8mk", 0x69636823, null, ImageKey.createKey(48, 1)),
    ICNS_32x32_8BIT_MASK("l8mk", 0x6C386D6B, null, ImageKey.createKey(32, 8)),
    ICNS_32x32_1BIT_MASK("ICN#", 0x49434E23, null, ImageKey.createKey(32, 1)),
    ICNS_16x16_8BIT_MASK("s8mk", 0x73386D6B, null, ImageKey.createKey(16, 8)),
    ICNS_16x16_1BIT_MASK("ics#", 0x69637323, null, ImageKey.createKey(16, 1));

    private final String id;
    private final long val;
    public final ImageKey key;
    public final ImageKey mask;

    Type(String id, long val, ImageKey key, ImageKey mask) {
        this.id = id;
        this.val = val;
        this.key = key;
        this.mask = mask;
    }

    private void readData(byte[] buf, Map<ImageKey, byte[]> mapData, Map<ImageKey, byte[]> mapMask) {
        if (key != null) {
            if (mapData.put(key, buf) != null)
                throw new IllegalArgumentException("Duplication image key: " + key);
        } else if (mapMask.put(mask, buf) != null)
            throw new IllegalArgumentException("Duplication image mask: " + mask);
    }

    // ========== static ==========

    public static Type parseImageKey(ImageKey key) {
        for (Type type : values())
            if (type.key == key)
                return type;
        throw new IllegalArgumentException(key.toString());
    }

    public static void readData(ImageInputStream in, Map<ImageKey, byte[]> mapData, Map<ImageKey, byte[]> mapMask) throws IOException {
        long val = in.readUnsignedInt();
        int size = in.readInt();

        for (Type type : values()) {
            if (type.val != val)
                continue;

            type.readData(IconIO.readBytes(size - 8, in), mapData, mapMask);
            break;
        }
    }
}
