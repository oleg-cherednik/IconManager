package cop.icoman.ico;

import cop.icoman.IconImageHeader;
import cop.icoman.ImageKey;
import cop.icoman.Utils;
import lombok.Data;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 01.09.2013
 */
@Data
final class ImageHeader implements IconImageHeader {
    public static final int SIZE = 16;

    private final int pos;
    private final int width;
    private final int height;
    private final int planes;
    private final int bitsPerPixel;
    private final int size;
    private final int offs;

    public ImageHeader(int pos, ImageInputStream in) throws IOException {
        this.pos = pos;
        width = Utils.zeroTo256(in.readUnsignedByte());
        height = Utils.zeroTo256(in.readUnsignedByte());
        int colors = in.readUnsignedByte();
        in.skipBytes(1);
        planes = in.readShort();
        bitsPerPixel = Utils.bitsPerPixel(in.readShort(), colors);
        size = in.readInt();
        offs = in.readInt();
    }

    // ========== Object ==========

    @Override
    public String toString() {
        return ImageKey.parse(Integer.toString(pos), width, height, bitsPerPixel) + ", size: " + size + ", rva: " + offs;
    }
}
