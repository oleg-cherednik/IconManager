package cop.icoman.icl;

import cop.icoman.ImageKey;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.util.Comparator;

/**
 * @author Oleg Cherednik
 * @since 21.10.2016
 */
final class ImageHeader {
    public static final Comparator<ImageHeader> SORT_BY_BITS_SIZE_ASC = (header1, header2) -> {
        if (header1 == header2)
            return 0;

        int res;

        if ((res = Integer.compare(header1.height, header2.height)) != 0)
            return res;
        if ((res = Integer.compare(header1.bitsPerPixel, header2.bitsPerPixel)) != 0)
            return res;
        return Integer.compare(header1.width, header2.width);
    };

    public static final int SIZE = 14;

    public final int pos;
    public final int width;
    public final int height;
    public final int planes;
    public final int bitsPerPixel;

    public ImageHeader(int pos, ImageInputStream in) throws IOException {
        in.skipBytes(4);
        in.readUnsignedByte();
        this.pos = pos;
        in.skipBytes(1);
        width = zeroTo256(in.readUnsignedByte());
        height = zeroTo256(in.readUnsignedByte());
        int colors = in.readUnsignedByte();
        in.skipBytes(1);
        planes = in.readShort();
        bitsPerPixel = bitsPerPixel(in.readShort(), colors);
    }

    // ========== Object ==========

    @Override
    public String toString() {
        return ImageKey.parse(Integer.toString(pos), width, height, bitsPerPixel);
    }

    // ========== static ==========

    @SuppressWarnings("StaticMethodNamingConvention")
    private static int zeroTo256(int size) {
        return size != 0 ? size : 256;
    }

    private static int bitsPerPixel(int bitsPerPixel, int colors) {
        return bitsPerPixel != 0 ? bitsPerPixel : (int)Math.sqrt(colors);
    }
}
