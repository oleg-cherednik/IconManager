package cop.icoman.icl;

import cop.icoman.IconImageHeader;
import cop.icoman.ImageKey;
import cop.icoman.Utils;
import lombok.Data;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 21.10.2016
 */
@Data
final class ImageHeader implements IconImageHeader {
    public static final int SIZE = 14;

    private final int pos;
    private final int width;
    private final int height;
    private final int planes;
    private final int bitsPerPixel;

    public ImageHeader(int pos, ImageInputStream in) throws IOException {
        in.skipBytes(5);
        this.pos = pos;
        in.skipBytes(1);
        width = Utils.zeroTo256(in.readUnsignedByte());
        height = Utils.zeroTo256(in.readUnsignedByte());
        int colors = in.readUnsignedByte();
        in.skipBytes(1);
        planes = in.readShort();
        bitsPerPixel = Utils.bitsPerPixel(in.readShort(), colors);
    }

    // ========== Object ==========

    @Override
    public String toString() {
        return ImageKey.parse(Integer.toString(pos), width, height, bitsPerPixel);
    }
}
