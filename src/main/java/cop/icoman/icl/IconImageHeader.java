package cop.icoman.icl;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 21.10.2016
 */
final class IconImageHeader {
    public static final int SIZE = 14;

    public final int num;
    public final int width;
    public final int height;
    public final int planes;
    public final int bitsPerPixel;

    public IconImageHeader(ImageInputStream in) throws IOException {
        in.skipBytes(4);
        num = in.readUnsignedByte();
        in.skipBytes(1);
        width = zeroTo256(in.readUnsignedByte());
        height = zeroTo256(in.readUnsignedByte());
        int colors = in.readUnsignedByte();
        in.skipBytes(1);
        planes = in.readShort();
        bitsPerPixel = bitsPerPixel(in.readShort(), colors);
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
