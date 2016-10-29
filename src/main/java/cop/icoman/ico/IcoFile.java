package cop.icoman.ico;

import cop.icoman.AbstractIconFile;
import cop.icoman.IconIO;
import cop.icoman.ImageKey;
import cop.icoman.exceptions.IconManagerException;

import javax.imageio.stream.ImageInputStream;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author Oleg Cherednik
 * @since 03.07.2013
 */
public final class IcoFile extends AbstractIconFile {
    public IcoFile(ImageInputStream in) throws IOException, IconManagerException {
        super(createImageById(new IcoFileHeader(in), in));
    }

    // ========== static ==========

    private static List<ImageHeader> readImageHeaders(int total, ImageInputStream in) throws IOException {
        assert total > 0;
        assert in != null;

        Set<ImageHeader> imageHeaders = new TreeSet<>(ImageHeader.SORT_BY_BITS_SIZE_ASC);
        List<ImageHeader> headers = new ArrayList<>(total);

        for (int i = 0; i < total; i++)
            headers.add(new ImageHeader(i, in));

        return Collections.unmodifiableList(headers);
    }

    private static Map<String, Image> createImageById(IcoFileHeader fileHeader, ImageInputStream in) throws IOException, IconManagerException {
        List<ImageHeader> imageHeaders = readImageHeaders(fileHeader.getImageCount(), in);
        Map<String, Image> imageById = new TreeMap<>();
        int offs = IcoFileHeader.SIZE + imageHeaders.size() * ImageHeader.SIZE;

        for (ImageHeader imageHeader : imageHeaders) {
            checkOffs(offs, imageHeader);

            String id = ImageKey.parse(imageHeader.getWidth(), imageHeader.getHeight(), imageHeader.getBitsPerPixel());
            BufferedImage image = IconIO.readImage(imageHeader.getSize(), in);

            // TODO set default image
            if (image == null)
                continue;
            if (imageById.containsKey(id))
                System.out.println("duplicate image key '" + id + '\'');
            else
                imageById.put(id, image);

            offs += imageHeader.getSize();
        }

        return imageById.isEmpty() ? Collections.emptyMap() : imageById;
    }

    private static void checkOffs(int expected, ImageHeader imageHeader) throws IconManagerException {
        if (expected != imageHeader.getOffs())
            throw new IconManagerException("rva image no. " + imageHeader.getPos() + " incorrect. actual=" +
                    imageHeader.getOffs() + ", expected=" + expected);
    }
}
