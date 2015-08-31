package cop.swing.icoman.imageio.bmp;

import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 31.08.2015
 */
final class Bitmap32Bits extends Bitmap {
    public static final Bitmap32Bits INSTANCE = new Bitmap32Bits();

    private Bitmap32Bits() {
    }

    // ========== Bitmap ==========

    @Override
    public BufferedImage createImage(int width, int height, int[] colors, ImageInputStream in, boolean inv) throws IOException {
        int[] data = read32bitDataBlocks(width, Math.abs(height), 32, in);
        int[] mask = read32bitMaskBlocks(width, Math.abs(height), in);
        return createImage(width, height, colors, data, mask, false);
    }

    @Override
    public BufferedImage createImage(int width, int height, int[] colors, int[] data, int[] mask, boolean inv) {
        int[] buf = decode(width, height, data);
        int[] alpha = Bitmap1Bit.alpha(width, height, mask, inv);

        print(width, height, alpha);

        BufferedImage image = new BufferedImage(width, Math.abs(height), BufferedImage.TYPE_4BYTE_ABGR);

        for (int y = Math.abs(height) - 1, offs = 0, i = 0; y >= 0; y--)
            for (int x = 0; x < width; x++, offs += 4, i++)
//                image.setRGB(x, y, rgb(buf[offs + 2], buf[offs + 1], buf[offs], alpha[i] != 0x0 ? buf[offs + 3] : 0x0));
                image.setRGB(x, y, rgb(buf[offs + 2], buf[offs + 1], buf[offs], buf[offs + 3]));

        return image;
    }

    // ========== static ==========

    private static int[] decode(int width, int height, int[] buf) {
        return height > 0 ? flipVertical(width, height, buf) : buf;
    }
}
