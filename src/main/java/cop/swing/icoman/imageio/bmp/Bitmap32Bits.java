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
        BufferedImage image = new BufferedImage(width, Math.abs(height), BufferedImage.TYPE_4BYTE_ABGR);
        boolean rev = height > 0;

        for (int y = rev ? 0 : Math.abs(height) - 1, offs = 0; rev ? y < Math.abs(height) : y >= 0; y = rev ? y + 1 : y - 1)
            for (int x = 0; x < width; x++, offs += 4)
                image.setRGB(x, y, rgb(data[offs + 2], data[offs + 1], data[offs], data[offs + 3]));

        return image;
    }
}
