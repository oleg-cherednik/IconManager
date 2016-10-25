package cop.icoman.icns;

import cop.icoman.AbstractIconFile;
import cop.icoman.ImageKey;
import cop.icoman.exceptions.FormatNotSupportedException;
import cop.icoman.exceptions.IconManagerException;
import cop.icoman.icns.imageio.IcnsReaderSpi;
import cop.icoman.ico.IcoFile;

import javax.imageio.stream.ImageInputStream;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
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

    private IcnsFile(Map<ImageKey, Image> images) {
        super(IcoFile.createImageById(images));
    }

    // ========== static ==========

    private static Map<ImageKey, Image> readImages(ImageInputStream in) throws IOException, IconManagerException {
        Map<ImageKey, int[]> mapData = new HashMap<>();
        Map<ImageKey, int[]> mapMask = new HashMap<>();

        try {
            while (true) {
                Type.readData(in, mapData, mapMask);
            }
        } catch(Exception ignored) {
        }

        Map<ImageKey, Image> images = new TreeMap<>();

        for (Map.Entry<ImageKey, int[]> entry : mapData.entrySet()) {
            ImageKey key = entry.getKey();
            Type type = Type.parseImageKey(key);
            int[] data = entry.getValue();
            int[] mask = mapMask.get(type.mask);
            BufferedImage image = type.createImage(key, data, mask);

            // TODO set default image
            if (image != null)
                images.put(key, image);
        }

        return images.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(images);

    }

    // ========== static ==========

    private static void checkHeader(ImageInputStream in) throws IOException, FormatNotSupportedException {
        if (!IcnsReaderSpi.isHeaderValid(in.readInt()))
            throw new FormatNotSupportedException("Expected icns format: 'header offs:0, size:4' should be 'icns'");

        in.readUnsignedInt();   // file size (offs: 0x4, size: 4)
    }
}
