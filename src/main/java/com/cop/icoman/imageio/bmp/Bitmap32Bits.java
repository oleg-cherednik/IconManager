package com.cop.icoman.imageio.bmp;

import com.cop.icoman.ImageKey;

import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 31.08.2015
 */
@SuppressWarnings("MethodCanBeVariableArityMethod")
final class Bitmap32Bits extends Bitmap {
    public static final Bitmap32Bits INSTANCE = new Bitmap32Bits();

    private Bitmap32Bits() {
    }

    @Override
    public int[] invertMask(int[] mask) {
        return mask;
    }

    // ========== Bitmap ==========

    @Override
    public BufferedImage createImage(int width, int height, int[] colors, ImageInputStream in) throws IOException {
        int[] data = read32bitDataBlocks(width, Math.abs(height), ImageKey.XP, in);
        int[] mask = read32bitMaskBlocks(width, Math.abs(height), in);
        return createImage(width, height, colors, data, mask);
    }

    @Override
    public BufferedImage createImage(int width, int height, int[] colors, int[] data, int[] mask) {
        BufferedImage image = new BufferedImage(width, Math.abs(height), BufferedImage.TYPE_4BYTE_ABGR);
        boolean rev = height > 0;

        for (int y = rev ? 0 : Math.abs(height) - 1, offs = 0; rev ? y < Math.abs(height) : y >= 0; y = rev ? y + 1 : y - 1)
            for (int x = 0; x < width; x++, offs += 4)
                image.setRGB(x, y, rgb(data[offs + 2], data[offs + 1], data[offs], data[offs + 3]));

        return image;
    }
}
