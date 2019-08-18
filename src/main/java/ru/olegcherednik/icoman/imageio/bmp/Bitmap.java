package ru.olegcherednik.icoman.imageio.bmp;

import ru.olegcherednik.icoman.ImageKey;
import ru.olegcherednik.icoman.exceptions.IconManagerException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 31.08.2015
 */
@SuppressWarnings("MethodCanBeVariableArityMethod")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Bitmap {
    public abstract BufferedImage createImage(int width, int height, int[] colors, ImageInputStream in) throws IOException;

    public abstract BufferedImage createImage(int width, int height, int[] colors, int[] data, int[] mask);

    public int[] invertMask(int[] mask) {
        if (mask != null)
            for (int i = 0; i < mask.length; i++)
                mask[i] = ~mask[i] & 0xFF;

        return mask;
    }

    // ========== static ==========

    static BufferedImage readImage(ImageInputStream in) throws IOException, IconManagerException {
        BitmapInfoHeader header = new BitmapInfoHeader(in);
        int width = header.getBiWidth();
        int height = header.getBiHeight();
        int bitsPerPixel = header.getBiBitCount();
        int[] colors = readColorTable(header, in);
        return getInstanceForBits(bitsPerPixel).createImage(width, -height, colors, in);
    }

    public static Bitmap getInstanceForBits(int bitsPerPixel) throws IconManagerException {
        if (bitsPerPixel == 1)
            return Bitmap1Bit.INSTANCE;
        if (bitsPerPixel == 4)
            return Bitmap4Bits.INSTANCE;
        if (bitsPerPixel == 8)
            return Bitmap8Bits.INSTANCE;
        if (bitsPerPixel == ImageKey.TRUE_COLOR)
            return Bitmap24Bits.INSTANCE;
        if (bitsPerPixel == ImageKey.XP)
            return Bitmap32Bits.INSTANCE;

        throw new IconManagerException("Bitmap with " + bitsPerPixel + "bitsPerPixel is not supported");
    }

    private static int[] readColorTable(BitmapInfoHeader header, ImageInputStream in) throws IOException {
        int bitCount = header.getBiBitCount();
        int size = bitCount <= 8 ? (int)Math.pow(2, bitCount) : 0;

        if (size == 0)
            return null;

        int[] data = new int[size];

        for (int i = 0; i < data.length; ++i) {
            int blue = in.readUnsignedByte();
            int green = in.readUnsignedByte();
            int red = in.readUnsignedByte();
            in.skipBytes(1);    // reserved
            data[i] = rgb(red, green, blue);
        }

        return data;
    }

    @SuppressWarnings("StaticMethodNamingConvention")
    protected static int[] read32bitDataBlocks(int width, int height, int bitCount, ImageInputStream in) throws IOException {
        int[] buf = new int[width * height * bitCount / 8];
        int used = width * bitCount / 8;

        for (int i = 0, offs = 0, total = (width * bitCount + 31) / 32 * 4 * height; i < total; i++) {
            int val = in.readUnsignedByte();

            if (i % 4 < used)
                buf[offs++] = val;
        }

        return buf;
    }

    @SuppressWarnings("StaticMethodNamingConvention")
    protected static int[] read32bitMaskBlocks(int width, int height, ImageInputStream in) throws IOException {
        int[] buf = new int[(width + 31) / 32 * 4 * height];
        int used = width / 8;
        int div = 4 * (used / 4 + (used % 4 > 0 ? 1 : 0));

        for (int i = 0, offs = 0; i < buf.length; i++) {
            int val = in.readUnsignedByte();

            if (i % div < used)
                buf[offs++] = val;
        }

        return buf;
    }

    protected static int[] flipVertical(int width, int height, int[] data) {
        for (int i = 0; i < height / 2; i++) {
            for (int j = 0; j < width; j++) {
                int a = i * width + j;
                int b = data.length - i * width - width + j;
                int tmp = data[a];
                data[a] = data[b];
                data[b] = tmp;
            }
        }

        return data;
    }

    protected static BufferedImage createBufferedImage(int width, int height, int[] colors, int[] alpha, int[] buf) {
        BufferedImage image = new BufferedImage(width, Math.abs(height), BufferedImage.TYPE_4BYTE_ABGR);

        for (int y = Math.abs(height) - 1, offs = 0; y >= 0; y--)
            for (int x = 0; x < width; x++, offs++)
                image.setRGB(x, y, rgb(colors[buf[offs]], alpha[offs]));

        return image;
    }

    static int rgb(int rgb, int alpha) {
        return rgb(rgb >> 16, rgb >> 8, rgb, alpha);
    }

    public static int rgb(int red, int green, int blue) {
        return rgb(red, green, blue, 0xFF);
    }

    static int rgb(int red, int green, int blue, int alpha) {
        return ((alpha & 0xFF) << 24) | ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | (blue & 0xFF);
    }
}
