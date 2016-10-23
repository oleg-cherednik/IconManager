package cop.icoman.ico;

import cop.icoman.BitmapType;
import cop.icoman.exceptions.FormatNotSupportedException;
import cop.icoman.exceptions.IconManagerException;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

import static cop.icoman.BitmapType.parseCode;

/**
 * @author Oleg Cherednik
 * @since 03.07.2013
 */
public final class IcoFileHeader {
    public static final int SIZE = 6;

    private final BitmapType type; // size: 2, rva: 0x2
    private final int imageCount; // size: 2, rva: 0x4

    private IcoFileHeader(BitmapType type, int imageCount) {
        this.type = type;
        this.imageCount = imageCount;
    }

    public BitmapType getType() {
        return type;
    }

    public int getImageCount() {
        return imageCount;
    }

    // ========== Object ==========

    @Override
    public String toString() {
        return type + ":" + imageCount;
    }

    // ========== static ==========

    public static IcoFileHeader read(ImageInputStream in) throws IconManagerException, IOException {
        if (in.readUnsignedShort() != 0)
            throw new FormatNotSupportedException("Expected ico format: 'header rva:0, size:2' is reserved, should be 0");

        BitmapType type = parseCode(in.readUnsignedShort());
        int imageCount = in.readUnsignedShort();

        check(type, imageCount);

        return new IcoFileHeader(type, imageCount);
    }

    private static void check(BitmapType type, int imageCount) throws IconManagerException {
        if (type == null || type == BitmapType.NONE)
            throw new IconManagerException("'header.type' is not set");
        if (type != BitmapType.ICO)
            throw new IconManagerException("'header.type' " + type.name() + " is not supported");
        if (imageCount < 1)
            throw new IconManagerException("'header.imageCount' is illegal: " + imageCount);
    }
}
