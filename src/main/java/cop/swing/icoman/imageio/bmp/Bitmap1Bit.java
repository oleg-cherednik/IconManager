package cop.swing.icoman.imageio.bmp;

import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 31.08.2015
 */
final class Bitmap1Bit extends Bitmap {
    public static final Bitmap1Bit INSTANCE = new Bitmap1Bit();

    private Bitmap1Bit() {
    }

    // ========== Bitmap ==========

    @Override
    public BufferedImage createImage(int width, int height, int[] colors, ImageInputStream in) throws IOException {
        int[] data = read32bitDataBlocks(width, Math.abs(height), 1, in);
        int[] mask = read32bitMaskBlocks(width, Math.abs(height), in);
        return createImage(width, height, colors, data, mask);
    }

    @Override
    public BufferedImage createImage(int width, int height, int[] colors, int[] data, int[] mask) {
        int[] buf = decode(width, height, data);
        int[] alpha = alpha(width, height, mask);
        return createBufferedImage(width, height, colors, alpha, buf);
    }

    // ========== static ==========

    private static int[] decode(int width, int height, int[] data) {
        int[] buf = new int[Math.abs(width * height)];

        for (int i = 0, offs = 0, x = 0; i < data.length; i++, x = i % 2 == 0 ? 0 : x)
            for (int j = 7; j >= 0; j--, x++)
                if (x < width && offs < buf.length)
                    buf[offs++] = (0x1 << j & data[i]) != 0 ? 1 : 0;

        return height > 0 ? flipVertical(width, height, buf) : buf;
    }

    static int[] alpha(int width, int height, int[] mask) {
        int[] buf = new int[Math.abs(width * height)];

        for (int i = 0, offs = 0, x = 0; i < mask.length; i++, x = i % 2 == 0 ? 0 : x)
            for (int j = 7; j >= 0; j--, x++)
                if (x < width && offs < buf.length)
                    buf[offs++] = (1 << j & mask[i]) != 0 ? 0x0 : 0xFF;

        return height > 0 ? flipVertical(width, height, buf) : buf;
    }
}
