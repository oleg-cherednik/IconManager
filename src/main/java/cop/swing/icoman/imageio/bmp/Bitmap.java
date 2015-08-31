package cop.swing.icoman.imageio.bmp;

import cop.swing.icoman.exceptions.IconManagerException;

import javax.imageio.stream.ImageInputStream;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 01.09.2013
 */
public final class Bitmap {
    public static BufferedImage readImage(ImageInputStream in) throws IOException, IconManagerException {
        BitmapInfoHeader header = new BitmapInfoHeader(in);
        int[] colors = readColorTable(header, in);
        int width = header.getBiWidth();
        int height = header.getBiHeight();
        int bitCount = header.getBiBitCount();

        if (bitCount == 1) {
            byte[] data = read32bitDataBlocks(width, height, bitCount, in);
            byte[] mask = read32bitMaskBlocks(width, height, 1, in);
            return Bit1.createImage(width, -height, colors, data, mask, false);
        }

        if (bitCount == 4) {
            byte[] data = read32bitDataBlocks(width, height, bitCount, in);
            byte[] mask = read32bitMaskBlocks(width, height, 1, in);
            return Bit4.createImage(width, -height, colors, data, mask, false);
        }

        if (bitCount == 8 && width == 16) {
            byte[] data = read32bitDataBlocks(width, height, bitCount, in);
            byte[] mask = read32bitMaskBlocks(width, height, 1, in);
            return Bit8.createImage(width, -height, colors, data, mask, false);
        }

        byte[] mask = readBitMasks(header, in);
        byte[] data = readData(header, in);
        int[] alpha = create1bitAlpha(header, data);

        if (bitCount == 8)
            return createImage8(width, height, colors, alpha, mask);
        if (bitCount == 24)
            return createImage24(width, height, alpha, mask);
        if (bitCount == 32)
            return create32bitImage(width, height, mask);

        throw new IconManagerException("Bitmap with " + bitCount + "bit is not supported");
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
            data[i] = rgb(red, green, blue);
        }

        return data;
    }

    private static byte[] readBitMasks(BitmapInfoHeader header, ImageInputStream in) throws IOException {
        int width = header.getBiWidth();
        int height = header.getBiHeight();
        int bitCount = header.getBiBitCount();
        byte[] buf = new byte[(width * bitCount + 31) / 32 * 4 * height];

        in.read(buf);

        return buf;
    }

    private static byte[] read32bitDataBlocks(int width, int height, int bitCount, ImageInputStream in) throws IOException {
        int total = (width * bitCount + 31) / 32 * 4 * height;
        byte[] buf = new byte[width * height * bitCount / 8];
        int skip = width * bitCount / 8;

        for (int i = 0, offs = 0; i < total; i++) {
            byte val = in.readByte();

            if (i % 4 < skip)
                buf[offs++] = val;
        }

        return buf;
    }

    private static byte[] read32bitMaskBlocks(int width, int height, int bitCount, ImageInputStream in) throws IOException {
        int total = (width + 31) / 32 * 4 * height;
        byte[] buf = new byte[total];
        int skip = width * bitCount / 8;

        for (int i = 0, offs = 0; i < total; i++) {
            byte val = in.readByte();

            if (i % 4 < skip)
                buf[offs++] = val;
        }

        return buf;
    }

    private static byte[] readData(BitmapInfoHeader header, ImageInputStream in) throws IOException {
        int width = header.getBiWidth();
        int height = header.getBiHeight();
        byte[] buf = new byte[(width + 31) / 32 * 4 * height];

        in.read(buf);

        return buf;
    }

    private static int[] create1bitAlpha(BitmapInfoHeader header, byte[] data) {
        int width = header.getBiWidth();
        int height = header.getBiHeight();
        int[] buf = new int[width * height];

        int pos = 0;
        int n4 = data.length / height;
        int n5 = 0;
        int n6 = 0;

        for (byte val : data) {
            ++n5;
            for (int j = 7; j >= 0; j--) {
                if (n6 >= width)
                    continue;
                buf[pos++] = (byte)((1 << j & val) != 0 ? 0 : -1);
                ++n6;
            }
            if (n5 != n4)
                continue;
            n5 = 0;
            n6 = 0;
        }

        return buf;
    }

    private static int[] rotate180(int width, int height, int[] data) {
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

    public static void print(int width, int height, int[] buf) {
        for (int i = 0, offs = 0; i < height && offs < buf.length; i++) {
            for (int j = 0; j < width && offs < buf.length; j++, offs++)
                System.out.print(buf[offs] == 0x0 ? '.' : '#');
            System.out.println();
        }
    }

    public static BufferedImage createImage8(int width, int height, int[] colors, int[] alpha, byte... mask) {
        int[] buf = new int[width * height];

        if (mask.length == buf.length)
            for (int offs = 0; offs < buf.length; offs++)
                buf[offs] = mask[offs] & 255;
        else {
            int size = mask.length / height - width;

            for (int offsMask = 0, offs = 0, n5 = 0; offsMask < mask.length; offsMask++, offs++) {
                buf[offs] = mask[offsMask] & 255;

                if (++n5 != width)
                    continue;

                n5 = 0;
                offsMask += size;
            }
        }

        return createImageOld(width, height, colors, alpha, buf);
    }

    private static BufferedImage createImage24(int width, int height, int[] alpha, byte... mask) {
        int offsMask = 0;
        int offsAlpha = 0;
        int size = mask.length == 3 * height * width ? 0 : mask.length / height - width * 3;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

        for (int y = height - 1; y >= 0; --y) {
            for (int x = 0; x < width; ++x) {
                int blue = mask[offsMask++] & 255;
                int green = mask[offsMask++] & 255;
                int red = mask[offsMask++] & 255;
                int rgb = new Color(red, green, blue, alpha[offsAlpha++] & 255).getRGB();
                image.setRGB(x, y, rgb);
            }
            offsMask += size;
        }

        return image;
    }

    public static BufferedImage create32bitImage(int width, int height, byte... mask) {
        int offsMask = 0;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                int blue = mask[offsMask++] & 255;
                int green = mask[offsMask++] & 255;
                int red = mask[offsMask++] & 255;
                int alpha = mask[offsMask++] & 255;
                int rgb = new Color(red, green, blue, alpha).getRGB();
                image.setRGB(x, y, rgb);
            }
        }

        return image;
    }

    public static BufferedImage createImageOld(int width, int height, int[] colors, int[] alpha, int[] buf) {
        BufferedImage image = new BufferedImage(width, Math.abs(height), BufferedImage.TYPE_4BYTE_ABGR);

        for (int y = Math.abs(height) - 1, offs = 0; y >= 0; y--)
            for (int x = 0; x < width; x++, offs++)
                image.setRGB(x, y, rgb(colors[buf[offs]], alpha[offs]));

        return image;
    }

    private static int rgb(int rgb, int alpha) {
        return rgb(rgb >> 16, rgb >> 8, rgb, alpha);
    }

    public static int rgb(int red, int green, int blue) {
        return rgb(red, green, blue, 0xFF);
    }

    private static int rgb(int red, int green, int blue, int alpha) {
        return ((alpha & 0xFF) << 24) | ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | (blue & 0xFF);
    }

    public static final class Bit1 {
        public static BufferedImage createImage(int width, int height, int[] colors, byte[] data, byte[] mask, boolean inv) {
            int[] buf = decode(width, height, data);
            int[] alpha = alpha(width, height, mask, inv);
            return createImageOld(width, height, colors, alpha, buf);
        }

        private static int[] decode(int width, int height, byte[] data) {
            int[] buf = new int[Math.abs(width * height)];

            for (int i = 0, offs = 0, x = 0; i < data.length; i++, x = i % 2 == 0 ? 0 : x)
                for (int j = 7; j >= 0; j--, x++)
                    if (x < width && offs < buf.length)
                        buf[offs++] = (0x1 << j & data[i]) != 0 ? 1 : 0;

            return height > 0 ? rotate180(width, height, buf) : buf;
        }

        private static int[] alpha(int width, int height, byte[] mask, boolean inv) {
            int[] buf = new int[Math.abs(width * height)];

            for (int i = 0, offs = 0, x = 0; i < mask.length; i++, x = i % 2 == 0 ? 0 : x)
                for (int j = 7; j >= 0; j--, x++)
                    if (x < width && offs < buf.length) {
                        buf[offs++] = (1 << j & mask[i]) != 0 ? inv ? 0xFF : 0x0 : inv ? 0x0 : 0xFF;
                    }

            return height > 0 ? rotate180(width, height, buf) : buf;
        }

        private Bit1() {
        }
    }

    public static final class Bit4 {
        public static BufferedImage createImage(int width, int height, int[] colors, byte[] data, byte[] mask, boolean inv) {
            int[] buf = decode(width, height, data);
            int[] alpha = Bit1.alpha(width, height, mask, inv);
            return createImageOld(width, height, colors, alpha, buf);
        }

        private static int[] decode(int width, int height, byte[] data) {
            int[] buf = new int[Math.abs(width * height)];

            for (int i = 0, offs = 0; i < data.length; i++) {
                buf[offs++] = (data[i] >> 4) & 0xF;
                buf[offs++] = data[i] & 0xF;
            }

            return height > 0 ? rotate180(width, height, buf) : buf;
        }

        private Bit4() {
        }
    }

    public static final class Bit8 {
        public static BufferedImage createImage(int width, int height, int[] colors, byte[] data, byte[] mask, boolean inv) {
            int[] buf = decode(width, height, data);
            int[] alpha = Bit1.alpha(width, height, mask, inv);
            return createImageOld(width, height, colors, alpha, buf);
        }

        private static int[] decode(int width, int height, byte[] data) {
            int[] buf = new int[Math.abs(width * height)];

            for (int i = 0; i < data.length; i++)
                buf[i] = data[i] & 0xFF;

            return height > 0 ? rotate180(width, height, buf) : buf;
        }

        private Bit8() {
        }
    }

    private Bitmap() {
    }
}
