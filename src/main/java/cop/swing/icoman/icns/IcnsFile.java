package cop.swing.icoman.icns;

import cop.swing.icoman.IconFile;
import cop.swing.icoman.ImageKey;
import cop.swing.icoman.exceptions.FormatNotSupportedException;
import cop.swing.icoman.exceptions.IconManagerException;
import cop.swing.icoman.exceptions.ImageNotFoundException;
import cop.swing.icoman.icns.imageio.IcnsReaderSpi;

import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Oleg Cherednik
 * @since 02.08.15
 */
public final class IcnsFile implements IconFile {
    private final Map<ImageKey, ImageIcon> images;

    public static IcnsFile read(ImageInputStream in) throws Exception {
        checkHeader(in);
        return new IcnsFile(readImages(in));
    }

    private IcnsFile(Map<ImageKey, ImageIcon> images) {
        this.images = images;
    }

    // ========== IconFile ==========

    @Override
    public Set<ImageKey> getKeys() {
        if (images.isEmpty())
            return Collections.emptySet();
        return Collections.unmodifiableSet(new TreeSet<ImageKey>(images.keySet()));
    }

    @Override
    public ImageIcon getImage(ImageKey key) throws ImageNotFoundException {
        ImageIcon image = images.get(key);

        if (image == null)
            throw new ImageNotFoundException(key);

        return image;
    }

    @Override
    public int getImagesAmount() {
        return images.size();
    }

    // ========== static ==========

    private static Map<ImageKey, ImageIcon> readImages(ImageInputStream in) throws IOException, IconManagerException {
        Map<ImageKey, int[]> mapData = new HashMap<>();
        Map<ImageKey, int[]> mapMask = new HashMap<>();

        while (in.getStreamPosition() < in.length()) {
            Type.readData(in, mapData, mapMask);
        }

        Map<ImageKey, ImageIcon> images = new HashMap<>(mapData.size());

        for (Map.Entry<ImageKey, int[]> entry : mapData.entrySet()) {
            ImageKey key = entry.getKey();
            Type type = Type.parseImageKey(key);
            int[] data = entry.getValue();
            int[] mask = mapMask.get(type.mask);
            BufferedImage image = type.createImage(key, data, mask);

            // TODO set default image
            if (image != null)
                images.put(key, new ImageIcon(image));
        }

        return images.isEmpty() ? Collections.<ImageKey, ImageIcon>emptyMap() : Collections.unmodifiableMap(images);
    }

    // ========== Iterable ==========

    @Override
    public Iterator<ImageIcon> iterator() {
        return images.values().iterator();
    }

    // ========== static ==========

    private static void checkHeader(ImageInputStream in) throws IOException, FormatNotSupportedException {
        if (!IcnsReaderSpi.isHeaderValid(in.readInt()))
            throw new FormatNotSupportedException("Expected icns format: 'header offs:0, size:4' should be 'icns'");

        long size = in.readUnsignedInt();   // file size, = in.length() (offs: 0x4, size: 4)

        if (size != in.length())
            throw new FormatNotSupportedException(String.format("File size expected: %d, actual: %d", size, in.length()));
    }
}
