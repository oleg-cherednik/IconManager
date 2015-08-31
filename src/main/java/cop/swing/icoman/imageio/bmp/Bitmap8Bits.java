package cop.swing.icoman.imageio.bmp;

import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 31.08.2015
 */
final class Bitmap8Bits extends Bitmap {
    public static final Bitmap8Bits INSTANCE = new Bitmap8Bits();

    private Bitmap8Bits() {
    }

    // ========== Bitmap ==========

    @Override
    public BufferedImage createImage(int width, int height, int[] colors, ImageInputStream in, boolean inv) throws IOException {
        byte[] data = read32bitDataBlocks(width, Math.abs(height), 8, in);
        byte[] mask = read32bitMaskBlocks(width, Math.abs(height), in);
        return createImage(width, height, colors, data, mask, false);
    }

    @Override
    public BufferedImage createImage(int width, int height, int[] colors, byte[] data, byte[] mask, boolean inv) {
        int[] buf = decode(width, height, data);
        int[] alpha = Bitmap1Bit.alpha(width, height, mask, inv);
        return createImage(width, height, colors, alpha, buf);
    }

    // ========== static ==========

    private static int[] decode(int width, int height, byte[] data) {
        int[] buf = new int[Math.abs(width * height)];

        for (int i = 0; i < data.length; i++)
            buf[i] = data[i] & 0xFF;

        return height > 0 ? flipVertical(width, height, buf) : buf;
    }
}
