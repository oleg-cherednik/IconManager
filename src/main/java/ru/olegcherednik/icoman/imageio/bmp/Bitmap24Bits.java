package ru.olegcherednik.icoman.imageio.bmp;

import ru.olegcherednik.icoman.ImageKey;

import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 31.08.2015
 */
@SuppressWarnings("MethodCanBeVariableArityMethod")
final class Bitmap24Bits extends Bitmap {
    public static final Bitmap24Bits INSTANCE = new Bitmap24Bits();

    private Bitmap24Bits() {
    }

    // ========== Bitmap ==========

    @Override
    public BufferedImage createImage(int width, int height, int[] colors, ImageInputStream in) throws IOException {
        int[] data = read32bitDataBlocks(width, Math.abs(height), ImageKey.TRUE_COLOR, in);
        int[] mask = read32bitMaskBlocks(width, Math.abs(height), in);
        return createImage(width, height, colors, data, mask);
    }

    @Override
    public BufferedImage createImage(int width, int height, int[] colors, int[] data, int[] mask) {
        int[] buf = decode(width, height, data);
        int[] alpha = Bitmap1Bit.alpha(width, height, mask);

        BufferedImage image = new BufferedImage(width, Math.abs(height), BufferedImage.TYPE_4BYTE_ABGR);

        for (int y = Math.abs(height) - 1, offs = 0, i = 0; y >= 0; y--)
            for (int x = 0; x < width; x++, offs += 3)
                image.setRGB(x, y, rgb(buf[offs + 2], buf[offs + 1], buf[offs], alpha[i++]));

        return image;
    }

    // ========== static ==========

    private static int[] decode(int width, int height, int[] buf) {
        return height > 0 ? flipVertical(width, height, buf) : buf;
    }
}
