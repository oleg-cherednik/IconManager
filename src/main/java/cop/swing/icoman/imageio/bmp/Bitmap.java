package cop.swing.icoman.imageio.bmp;

import cop.swing.icoman.exceptions.IconManagerException;

import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 31.08.2015
 */
public abstract class Bitmap {
    public abstract BufferedImage createImage(int width, int height, int[] colors, ImageInputStream in, boolean inv) throws IOException;

    public abstract BufferedImage createImage(int width, int height, int[] colors, byte[] data, byte[] mask, boolean inv);

    // ========== static ==========

    public static BufferedImage readImage(ImageInputStream in) throws IOException, IconManagerException {
        BitmapInfoHeader header = new BitmapInfoHeader(in);
        int[] colors = readColorTable(header, in);
        int width = header.getBiWidth();
        int height = header.getBiHeight();
        int bitCount = header.getBiBitCount();

//        return getInstanceForBits(bitCount).createImage(width, -height, colors, in, false);

        if (bitCount == 1)
            return getInstanceForBits(1).createImage(width, -height, colors, in, false);
        if (bitCount == 4)
            return getInstanceForBits(4).createImage(width, -height, colors, in, false);
        if (bitCount == 8)
            return getInstanceForBits(8).createImage(width, -height, colors, in, false);

        byte[] data = BitmapUtils.readBitMasks(header, in);
        byte[] mask = BitmapUtils.readData(header, in);
        int[] alpha = BitmapUtils.create1bitAlpha(header, mask);

        if (bitCount == 24)
            return BitmapUtils.createImage24(width, height, alpha, data);
        if (bitCount == 32)
            return BitmapUtils.create32bitImage(width, height, data);

        throw new IconManagerException("Bitmap with " + bitCount + "bit is not supported");
    }

    public static Bitmap getInstanceForBits(int bitCount) {
        if (bitCount == 1)
            return Bitmap1Bit.INSTANCE;
        if (bitCount == 4)
            return Bitmap4Bits.INSTANCE;
        if (bitCount == 8)
            return Bitmap8Bits.INSTANCE;

        throw new IllegalArgumentException("Unsupported bit count");
    }

    public static Bitmap getInstanceForColors(int colorCount) {
        if (colorCount == 2)
            return Bitmap1Bit.INSTANCE;
        if (colorCount == 16)
            return Bitmap4Bits.INSTANCE;
        if (colorCount == 256)
            return Bitmap8Bits.INSTANCE;

        throw new IllegalArgumentException("Unsupported colorCount count");
    }

    private static int[] readColorTable(BitmapInfoHeader header, ImageInputStream in) throws IOException {
        int bitCount = header.getBiBitCount();
        int size = bitCount <= 8 ? (int)Math.pow(2.0, bitCount) : 0;

        if (size == 0)
            return null;

        int[] data = new int[size];

        for (int i = 0; i < data.length; ++i) {
            int blue = in.readUnsignedByte();
            int green = in.readUnsignedByte();
            int red = in.readUnsignedByte();
            in.skipBytes(1);    // reserved
            data[i] = BitmapUtils.rgb(red, green, blue);
        }

        return data;
    }

    protected static byte[] read32bitDataBlocks(int width, int height, int bitCount, ImageInputStream in) throws IOException {
        byte[] buf = new byte[width * height * bitCount / 8];
        int used = width * bitCount / 8;

        for (int i = 0, offs = 0, total = (width * bitCount + 31) / 32 * 4 * height; i < total; i++) {
            byte val = in.readByte();

            if (i % 4 < used)
                buf[offs++] = val;
        }

        return buf;
    }

    protected static byte[] read32bitMaskBlocks(int width, int height, ImageInputStream in) throws IOException {
        byte[] buf = new byte[(width + 31) / 32 * 4 * height];
        int used = width / 8;
        int div = 4 * (used / 4 + (used % 4 > 0 ? 1 : 0));

        for (int i = 0, offs = 0; i < buf.length; i++) {
            byte val = in.readByte();

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

    protected static BufferedImage createImage(int width, int height, int[] colors, int[] alpha, int[] buf) {
        BufferedImage image = new BufferedImage(width, Math.abs(height), BufferedImage.TYPE_4BYTE_ABGR);

        for (int y = Math.abs(height) - 1, offs = 0; y >= 0; y--)
            for (int x = 0; x < width; x++, offs++)
                image.setRGB(x, y, BitmapUtils.rgb(colors[buf[offs]], alpha[offs]));

        return image;
    }

    protected Bitmap() {
    }
}
