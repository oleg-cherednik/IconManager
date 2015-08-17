package cop.swing.icoman.icns;

import cop.swing.icoman.IconFile;
import cop.swing.icoman.IconImage;
import cop.swing.icoman.ImageKey;
import cop.swing.icoman.exceptions.ImageNotFoundException;

import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;
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
public final class IcnsFile extends IconFile implements Iterable<IcnsImage> {
    private final Map<ImageKey, IcnsImage> images;

    public static IcnsFile read(ImageInputStream in) throws IOException {
        IcnsFileHeader header = IcnsFileHeader.read(in);
        Map<ImageKey, IcnsImage> images = readImages(in);
        return new IcnsFile(images);
    }

    private IcnsFile(Map<ImageKey, IcnsImage> images) {
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
    public IconImage getImage(ImageKey key) throws ImageNotFoundException {
        IcnsImage image = images.get(key);

        if (image == null)
            throw new ImageNotFoundException(key);

        return image;
    }

    @Override
    public ImageIcon getIcon(ImageKey key) throws ImageNotFoundException {
        return getImage(key).getIcon();
    }

    @Override
    public int getImagesAmount() {
        return images.size();
    }

    // ========== static ==========

    private static Map<ImageKey, IcnsImage> readImages(ImageInputStream in) throws IOException {
        Map<ImageKey, byte[]> mapData = new HashMap<>();
        Map<ImageKey, byte[]> mapMask = new HashMap<>();

        while (in.getStreamPosition() < in.length()) {
            Type.readData(in, mapData, mapMask);
        }

        IcnsImage image;
        Map<ImageKey, IcnsImage> images = new HashMap<>(mapData.size());

        for (Map.Entry<ImageKey, byte[]> entry : mapData.entrySet()) {
            ImageKey key = entry.getKey();
            byte[] data = entry.getValue();
            byte[] mask = mapMask.get(Type.parseImageKey(key).mask);
            images.put(key, image = new IcnsImage(key));
            image.setData(data);
            image.setMask(mask);
            image.createIcon();
        }

        return Collections.unmodifiableMap(images);
    }

    // ========== Iterable ==========

    @Override
    public Iterator<IcnsImage> iterator() {
        return images.values().iterator();
    }
}
