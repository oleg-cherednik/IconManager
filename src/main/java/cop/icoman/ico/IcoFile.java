package cop.icoman.ico;

import cop.icoman.AbstractIconFile;
import cop.icoman.IconIO;
import cop.icoman.exceptions.IconManagerException;

import javax.imageio.stream.ImageInputStream;
import java.awt.Image;
import java.awt.image.BufferedImage;
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
    public IcoFile(ImageInputStream in) throws IOException, IconManagerException {
        super(createImageById(new FileHeader(in), in));
    }

    // ========== static ==========

    private static List<ImageHeader> readImageHeaders(int total, ImageInputStream in) throws IOException {
        assert total > 0;
        assert in != null;

        List<ImageHeader> headers = new ArrayList<>(total);

        for (int pos = 0; pos < total; pos++)
            headers.add(ImageHeader.read(pos, in));

        return Collections.unmodifiableList(headers);
    }

    private static Map<String, Image> createImageById(FileHeader fileHeader, ImageInputStream in) throws IOException, IconManagerException {
        List<ImageHeader> imageHeaders = readImageHeaders(fileHeader.getImageCount(), in);
        Map<ImageHeader, Image> imageByHeader = new TreeMap<>();
        int offs = FileHeader.SIZE + imageHeaders.size() * ImageHeader.SIZE;

        for (ImageHeader imageHeader : imageHeaders) {
            checkOffs(offs, imageHeader);

            BufferedImage image = IconIO.readImage(in, imageHeader.getSize());

            if (image == null)
                System.err.println("Image '" + imageHeader + "' cannot be read");
            else
                imageByHeader.put(imageHeader, image);

            offs += imageHeader.getSize();
        }

        if (imageByHeader.isEmpty())
            return Collections.emptyMap();

        Map<String, Image> imageById = new LinkedHashMap<>();
        imageByHeader.entrySet().forEach(entry -> imageById.put(entry.getKey().getId(), entry.getValue()));

        return imageById;
    }

    private static void checkOffs(int expected, ImageHeader imageHeader) throws IconManagerException {
        if (expected != imageHeader.getOffs())
            throw new IconManagerException("rva image no. " + imageHeader.getPos() + " incorrect. actual=" +
                    imageHeader.getOffs() + ", expected=" + expected);
    }
}
