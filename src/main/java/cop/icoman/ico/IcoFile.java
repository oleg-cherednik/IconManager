package cop.icoman.ico;

import cop.icoman.AbstractIconFile;
import cop.icoman.IconIO;
import cop.icoman.ImageKey;
import cop.icoman.exceptions.IconManagerException;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Oleg Cherednik
 * @since 03.07.2013
 */
public final class IcoFile extends AbstractIconFile {
    private final IcoFileHeader header;

    public IcoFile(ImageInputStream in) throws IOException, IconManagerException {
        this(IcoFileHeader.read(in), in);
    }

    private IcoFile(IcoFileHeader header, ImageInputStream in) throws IOException, IconManagerException {
        this(header, readImages(header, in));
    }

    public IcoFile(IcoFileHeader header, Map<ImageKey, Image> images) {
        super(createImageById(images));
        this.header = header;
    }

    public static Map<String, Image> createImageById(Map<ImageKey, Image> images) {
        Map<String, Image> imageById = new LinkedHashMap<>();
        images.entrySet().forEach(entry -> imageById.put(entry.getKey().getId(), entry.getValue()));
        return Collections.unmodifiableMap(imageById);
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
            headers.add(IconImageHeader.readHeader(i, in));

        return Collections.unmodifiableList(headers);
    }

    private static Map<ImageKey, Image> readImages(IcoFileHeader fileHeader, ImageInputStream in) throws IOException, IconManagerException {
        List<IconImageHeader> imageHeaders = readImageHeaders(fileHeader.getImageCount(), in);
        Map<ImageKey, Image> images = new TreeMap<>();
        int offs = IcoFileHeader.SIZE + imageHeaders.size() * IconImageHeader.SIZE;

        for (IconImageHeader imageHeader : imageHeaders) {
            checkOffs(offs, imageHeader);

            ImageKey key = imageHeader.getImageKey();
            BufferedImage image = readIconImage(in, imageHeader.getSize());

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
            throw new IconManagerException("rva image no. " + imageHeader.getId() + " incorrect. actual=" +
                    imageHeader.getOffs() + ", expected=" + expected);
    }

    public static BufferedImage readIconImage(ImageInputStream in, int size) throws IOException {
        return ImageIO.read(new ByteArrayInputStream(IconIO.readBytes(size, in)));
    }
}
