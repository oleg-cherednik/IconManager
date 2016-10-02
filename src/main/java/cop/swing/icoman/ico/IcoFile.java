package cop.swing.icoman.ico;

import cop.swing.icoman.BitmapType;
import cop.swing.icoman.IconFile;
import cop.swing.icoman.IconIO;
import cop.swing.icoman.IconImageHeader;
import cop.swing.icoman.ImageKey;
import cop.swing.icoman.exceptions.IconManagerException;
import cop.swing.icoman.exceptions.ImageNotFoundException;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.validation.constraints.NotNull;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
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
public final class IcoFile implements IconFile {
    private final IcoFileHeader header;
    private final Map<ImageKey, Image> images;

    public static IcoFile read(ImageInputStream in) throws IOException, IconManagerException {
        IcoFileHeader header = IcoFileHeader.read(in);
        return new IcoFile(header, readImages(header, in));
    }

    private IcoFile(IcoFileHeader header, Map<ImageKey, Image> images) {
        this.header = header;
        this.images = images;
    }

    // ========== IconFile ==========

    @NotNull
    @Override
    public Set<ImageKey> getKeys() {
        return images.isEmpty() ? Collections.emptySet() : Collections.unmodifiableSet(new TreeSet<>(images.keySet()));
    }

    @NotNull
    @Override
    public Image getImage(ImageKey key) throws ImageNotFoundException {
        Image image = images.get(key);

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

    private static Map<ImageKey, Image> readImages(IcoFileHeader fileHeader, ImageInputStream in) throws IOException, IconManagerException {
        List<IconImageHeader> imageHeaders = readImageHeaders(fileHeader.getImageCount(), in);
        Map<ImageKey, Image> images = new LinkedHashMap<>(imageHeaders.size());
        int offs = IcoFileHeader.SIZE + imageHeaders.size() * IconImageHeader.SIZE;

        for (IconImageHeader imageHeader : imageHeaders) {
            checkOffs(offs, imageHeader);

            ImageKey key = imageHeader.getImageKey();
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(IconIO.readBytes(imageHeader.getSize(), in)));

            // TODO set default image
            if (image == null)
                continue;

            int width = image.getWidth();
            int height = image.getHeight();

            if (key.width() != width || key.height() != height)
                key = ImageKey.custom(width, height, key.getBitsPerPixel());

            if (images.containsKey(key))
                System.out.println("duplicate image key '" + key + '\'');
            else
                images.put(key, image);

            offs += imageHeader.getSize();
        }

        return images.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(images);
    }

    private static void checkOffs(int expected, IconImageHeader imageHeader) throws IconManagerException {
        if (expected != imageHeader.getOffs())
            throw new IconManagerException("offs image no. " + imageHeader.getId() + " incorrect. actual=" +
                    imageHeader.getOffs() + ", expected=" + expected);
    }

    // ========== Iterable ==========

    @Override
    public Iterator<Image> iterator() {
        return images.values().iterator();
    }
}
