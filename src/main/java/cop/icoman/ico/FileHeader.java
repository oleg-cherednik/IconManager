package cop.icoman.ico;

import cop.icoman.BitmapType;
import cop.icoman.exceptions.IconManagerException;
import lombok.Data;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

import static cop.icoman.BitmapType.parseCode;

/**
 * @author Oleg Cherednik
 * @since 03.07.2013
 */
@Data
final class FileHeader {
    public static final int SIZE = 6;

    private final BitmapType type;
    private final int imageCount;

    public FileHeader(ImageInputStream in) throws IOException, IconManagerException {
        in.skipBytes(2);
        type = parseCode(in.readUnsignedShort());
        imageCount = in.readUnsignedShort();
        check(type, imageCount);
    }

    // ========== Object ==========

    @Override
    public String toString() {
        return type + ":" + imageCount;
    }

    // ========== static ==========

    private static void check(BitmapType type, int imageCount) throws IconManagerException {
        if (type == null || type == BitmapType.NONE)
            throw new IconManagerException("'header.type' is not set");
        if (type != BitmapType.ICO)
            throw new IconManagerException("'header.type' " + type.name() + " is not supported");
        if (imageCount < 1)
            throw new IconManagerException("'header.imageCount' is illegal: " + imageCount);
    }
}
