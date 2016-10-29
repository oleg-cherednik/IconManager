package cop.icoman.icns;

import cop.icoman.AbstractIconFile;
import cop.icoman.exceptions.FormatNotSupportedException;
import cop.icoman.exceptions.IconManagerException;
import cop.icoman.icns.imageio.IcnsReaderSpi;

import javax.imageio.stream.ImageInputStream;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Oleg Cherednik
 * @since 02.08.15
 */
public final class IcnsFile extends AbstractIconFile {
    public static IcnsFile read(ImageInputStream in) throws Exception {
        checkHeader(in);
        return new IcnsFile(readImages(in));
    }

    private IcnsFile(Map<String, Image> imageById) {
        super(imageById);
    }

    // ========== static ==========

    private static Map<String, Image> readImages(ImageInputStream in) throws IOException, IconManagerException {
        Map<Type, int[]> mapData = new TreeMap<>(Type.SORT_BY_BITS_SIZE_ASC);
        Map<String, int[]> mapMask = new HashMap<>();

        readData(mapData, mapMask, in);

        Map<String, Image> imageById = new LinkedHashMap<>();

        for (Map.Entry<Type, int[]> entry : mapData.entrySet()) {
            Type type = entry.getKey();
            BufferedImage image = type.createImage(entry.getValue(), mapMask.get(type.strMask));

            if (image != null)
                imageById.put(type.strKey, image);
        }

        return imageById.isEmpty() ? Collections.emptyMap() : imageById;
    }

    // ========== static ==========

    private static void readData(Map<Type, int[]> mapData, Map<String, int[]> mapMask, ImageInputStream in) {
        try {
            while (true) {
                Type.readData(in, mapData, mapMask);
            }
        } catch(Exception ignored) {
        }
    }

    private static void checkHeader(ImageInputStream in) throws IOException, FormatNotSupportedException {
        if (!IcnsReaderSpi.isHeaderValid(in.readInt()))
            throw new FormatNotSupportedException("Expected icns format: 'header offs:0, size:4' should be 'icns'");

        in.readUnsignedInt();   // file size (offs: 0x4, size: 4)
    }
}
