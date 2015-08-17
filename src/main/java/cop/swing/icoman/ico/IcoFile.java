package cop.swing.icoman.ico;

import cop.swing.icoman.BitmapType;
import cop.swing.icoman.IconFile;
import cop.swing.icoman.IconImageHeader;
import cop.swing.icoman.ImageKey;
import cop.swing.icoman.exceptions.IconManagerException;
import cop.swing.icoman.exceptions.ImageDuplicationException;
import cop.swing.icoman.exceptions.ImageNotFoundException;

import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Oleg Cherednik
 * @since 03.07.2013
 */
public final class IcoFile extends IconFile implements Iterable<IcoImage> {
    private final IcoFileHeader header;
    private final Map<ImageKey, IcoImage> images;

    public static IcoFile read(ImageInputStream in) throws IOException, IconManagerException {
        IcoFileHeader header = IcoFileHeader.read(in);
        Map<ImageKey, IcoImage> images = readImages(header, in);
        return new IcoFile(header, images);
    }

    private IcoFile(IcoFileHeader header, Map<ImageKey, IcoImage> images) {
        assert header != null && header != IcoFileHeader.NULL;
        assert images != null && !images.isEmpty();

        this.header = header;
        this.images = images;
    }

    // ========== IconFile ==========

    @NotNull
    @Override
    public Set<ImageKey> getKeys() {
        if (images.isEmpty())
            return Collections.emptySet();
        return Collections.unmodifiableSet(new TreeSet<ImageKey>(images.keySet()));
    }

    @NotNull
    @Override
    public ImageIcon getIcon(ImageKey key) throws ImageNotFoundException {
        return getImage(key).getIcon();
    }

    @NotNull
    @Override
    public IcoImage getImage(ImageKey key) throws ImageNotFoundException {
        IcoImage image = images.get(key);

        if (image == null)
            throw new ImageNotFoundException(key);

        return image;
    }

    @Override
    public int getImagesAmount() {
        return images.size();
    }

    // ========== Object ==========

    @Override
    public String toString() {
        return header.toString();
    }

    // ========== static ==========

    private static List<IconImageHeader> readImageHeaders(int total, ImageInputStream in) throws IOException, IconManagerException {
        assert total > 0;
        assert in != null;

        List<IconImageHeader> headers = new ArrayList<>(total);

        for (int i = 0; i < total; i++)
            headers.add(BitmapType.ICO.createImageHeader(i, in));

        return Collections.unmodifiableList(headers);
    }

    private static Map<ImageKey, IcoImage> readImages(IcoFileHeader fileHeader, ImageInputStream in) throws IOException, IconManagerException {
        List<IconImageHeader> imageHeaders = readImageHeaders(fileHeader.getImageCount(), in);
        Map<ImageKey, IcoImage> images = new LinkedHashMap<>(imageHeaders.size());
        int offs = IcoFileHeader.SIZE + imageHeaders.size() * IconImageHeader.SIZE;

        for (IconImageHeader imageHeader : imageHeaders) {
            checkOffs(offs, imageHeader);

            if (images.put(imageHeader.getImageKey(), IcoImage.read(imageHeader, in)) != null)
                throw new ImageDuplicationException(imageHeader.getImageKey());

            offs += imageHeader.getSize();
        }

        return images.isEmpty() ? Collections.<ImageKey, IcoImage>emptyMap() : Collections.unmodifiableMap(images);
    }

    private static void checkOffs(int expected, IconImageHeader imageHeader) throws IconManagerException {
        if (expected != imageHeader.getOffs())
            throw new IconManagerException("offs image no. " + imageHeader.getId() + " incorrect. actual=" +
                    imageHeader.getOffs() + ", expected=" + expected);
    }

    // ========== Iterable ==========

    @Override
    public Iterator<IcoImage> iterator() {
        return images.values().iterator();
    }
}
