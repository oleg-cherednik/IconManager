package cop.icoman.icl;

import cop.icoman.ImageKey;
import cop.icoman.Utils;
import lombok.Data;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.util.Comparator;

/**
 * @author Oleg Cherednik
 * @since 21.10.2016
 */
@Data
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

    private final int pos;
    private final int width;
    private final int height;
    private final int planes;
    private final int bitsPerPixel;

    public ImageHeader(int pos, ImageInputStream in) throws IOException {
        in.skipBytes(4);
        in.readUnsignedByte();
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
