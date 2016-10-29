package cop.icoman.icl;

import cop.icoman.ImageKey;
import cop.icoman.Utils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 21.10.2016
 */
@Data
@EqualsAndHashCode(callSuper = true)
final class ImageHeader extends ImageKey {
    public static final int SIZE = 14;

    private final int pos;
    private final int planes;

    public static ImageHeader read(int pos, ImageInputStream in) throws IOException {
        in.skipBytes(6);
        int width = Utils.zeroTo256(in.readUnsignedByte());
        int height = Utils.zeroTo256(in.readUnsignedByte());
        int colors = in.readUnsignedByte();
        in.skipBytes(1);
        int planes = in.readShort();
        int bitsPerPixel = Utils.bitsPerPixel(in.readShort(), colors);
        return new ImageHeader(pos, width, height, bitsPerPixel, planes);
    }

    public ImageHeader(int pos, int width, int height, int planes, int bitsPerPixel) {
        super(width, height, bitsPerPixel);
        this.pos = pos;
        this.planes = planes;
    }
}
