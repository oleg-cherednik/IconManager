package ru.olegcherednik.icoman.ico;

import ru.olegcherednik.icoman.ImageKey;
import ru.olegcherednik.icoman.Utils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 01.09.2013
 */
@Data
@EqualsAndHashCode(callSuper = true)
final class ImageHeader extends ImageKey {
    public static final int SIZE = 16;

    private final int pos;
    private final int planes;
    private final int size;
    private final int offs;

    public static ImageHeader read(int pos, ImageInputStream in) throws IOException {
        int width = Utils.zeroTo256(in.readUnsignedByte());
        int height = Utils.zeroTo256(in.readUnsignedByte());
        int colors = in.readUnsignedByte();
        in.skipBytes(1);
        int planes = in.readShort();
        int bitsPerPixel = Utils.bitsPerPixel(in.readShort(), colors);
        int size = in.readInt();
        int offs = in.readInt();
        return new ImageHeader(pos, width, height, bitsPerPixel, planes, size, offs);
    }

    public ImageHeader(int pos, int width, int height, int bitsPerPixel, int planes, int size, int offs) {
        super(width, height, bitsPerPixel);
        this.pos = pos;
        this.planes = planes;
        this.size = size;
        this.offs = offs;
    }
}
