package cop.icoman.imageio.bmp;

import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 31.08.2015
 */
@SuppressWarnings("MethodCanBeVariableArityMethod")
final class Bitmap8Bits extends Bitmap {
    public static final Bitmap8Bits INSTANCE = new Bitmap8Bits();

    private Bitmap8Bits() {
    }

    // ========== Bitmap ==========

    @Override
    public BufferedImage createImage(int width, int height, int[] colors, ImageInputStream in) throws IOException {
        int[] data = read32bitDataBlocks(width, Math.abs(height), 8, in);
        int[] mask = read32bitMaskBlocks(width, Math.abs(height), in);
        return createImage(width, height, colors, data, mask);
    }

    @Override
    public BufferedImage createImage(int width, int height, int[] colors, int[] data, int[] mask) {
        int[] buf = decode(width, height, data);
        int[] alpha = Bitmap1Bit.alpha(width, height, mask);
        return createBufferedImage(width, height, colors, alpha, buf);
    }

    // ========== static ==========

    private static int[] decode(int width, int height, int[] data) {
        int[] buf = new int[Math.abs(width * height)];

        for (int i = 0; i < data.length; i++)
            buf[i] = data[i] & 0xFF;

        return height > 0 ? flipVertical(width, height, buf) : buf;
    }
}
