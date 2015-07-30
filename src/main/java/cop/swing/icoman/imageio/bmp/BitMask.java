package cop.swing.icoman.imageio.bmp;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 30.07.2015
 */
public final class BitMask {
    public final int red;
    public final int green;
    public final int blue;

    public BitMask(ImageInputStream in) throws IOException {
        blue = fix(in.readByte());
        green = fix(in.readByte());
        red = fix(in.readByte());
        in.skipBytes(1);    // reserved
    }

    private static int fix(byte val) {
        return (int)(((long)val & 255));
    }
}
