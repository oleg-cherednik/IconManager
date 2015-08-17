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
        Color[] colorTable = readColorTable(header, in);
        byte[] mask = readBitMasks(header, in);
        byte[] data = readData(header, in);
        byte[] alpha = createAlphaTable(header, data);

        int width = header.getBiWidth();
        int height = header.getBiHeight();
        int bitCount = header.getBiBitCount();

        if (bitCount == 1)
            return createImage1(width, height, data, colorTable, alpha, mask);
        if (bitCount == 4)
            return createImage4(width, height, colorTable, alpha, mask);
        if (bitCount == 8)
            return createImage8(width, height, colorTable, alpha, mask);
        if (bitCount == 24)
            return createImage24(width, height, alpha, mask);
        if (bitCount == 32)
            return createImage32(width, height, mask);

        throw new IconManagerException("Bitmap with " + bitCount + "bit is not supported");
    }

    private static Color[] readColorTable(BitmapInfoHeader header, ImageInputStream in) throws IOException {
        int bitCount = header.getBiBitCount();
        int size = bitCount <= 8 ? (int)Math.pow(2.0, bitCount) : 0;

        if (size == 0)
            return null;

        Color[] data = new Color[size];

        for (int i = 0; i < data.length; ++i) {
            int blue = (int)in.readByte() & 255;
            int green = (int)in.readByte() & 255;
            int red = (int)in.readByte() & 255;
            in.skipBytes(1);    // reserved
            data[i] = new Color(red, green, blue);
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

    private static byte[] readData(BitmapInfoHeader header, ImageInputStream in) throws IOException {
        int width = header.getBiWidth();
        int height = header.getBiHeight();
        byte[] buf = new byte[(width + 31) / 32 * 4 * height];

        in.read(buf);

        return buf;
    }

    private static byte[] createAlphaTable(BitmapInfoHeader header, byte... data) {
        int width = header.getBiWidth();
        int height = header.getBiHeight();
        byte[] buf = new byte[width * height];

        int pos = 0;
        int n4 = data.length / height;
        int n5 = 0;
        int n6 = 0;

        for (int i = 0; i < data.length; ++i) {
            ++n5;
            for (int j = 7; j >= 0; j--) {
                if (n6 >= width)
                    continue;
                buf[pos++] = (byte)((1 << j & data[i]) != 0 ? 0 : -1);
                ++n6;
            }
            if (n5 != n4)
                continue;
            n5 = 0;
            n6 = 0;
        }

        return buf;
    }

    private static BufferedImage createImage1(int width, int height, byte[] data, Color[] colors, byte[] alpha, byte... mask) {
        int[] buf = new int[width * height];
        int n4 = data.length / height;

        for (int offsMask = 0, offs = 0, n5 = 0, n6 = 0; offsMask < mask.length; ++offsMask) {
            n5++;

            for (int i = 7; i >= 0; i--) {
                if (n6 >= width)
                    continue;

                buf[offs++] = (1 << i & mask[offsMask]) != 0 ? 1 : 0;
                ++n6;
            }

            if (n5 != n4)
                continue;

            n5 = 0;
            n6 = 0;
        }

        return createImage(width, height, colors, alpha, buf);
    }

    private static BufferedImage createImage4(int width, int height, Color[] colors, byte[] alpha, byte... mask) {
        int[] buf = new int[width * height];

        if (mask.length * 2 == buf.length) {
            for (int offsMask = 0, offs = 0; offsMask < mask.length; offsMask++) {
                buf[offs++] = (mask[offsMask] & 255) >> 4;
                buf[offs++] = (mask[offsMask] & 255) >> 4 << 4 ^ mask[offsMask] & 255;
            }
        } else {
            int n6 = mask.length * 2 / height - width;

            for (int offsMask = 0, offs = 0, n5 = 0, n8 = 1; offsMask < mask.length; offsMask++) {
                if (n8 != 0)
                    buf[offs++] = (mask[offsMask] & 255) >> 4;
                if (n8 != 0 && ++n5 == width) {
                    n5 = 0;
                    n8 = 0;
                } else if (n8 == 0 && n5 == n6) {
                    n5 = 0;
                    n8 = 1;
                }

                if (n8 != 0)
                    buf[offs++] = (mask[offsMask] & 255) >> 4 << 4 ^ mask[offsMask] & 255;
                if (n8 != 0 && ++n5 == width) {
                    n5 = 0;
                    n8 = 0;
                    continue;
                }
                if (n8 != 0 || n5 != n6)
                    continue;
                n5 = 0;
                n8 = 1;
            }
        }

        return createImage(width, height, colors, alpha, buf);
    }

    private static BufferedImage createImage8(int width, int height, Color[] colors, byte[] alpha, byte... mask) {
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

        return createImage(width, height, colors, alpha, buf);
    }

    private static BufferedImage createImage24(int width, int height, byte[] alpha, byte... mask) {
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

    private static BufferedImage createImage32(int width, int height, byte... mask) {
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

    private static BufferedImage createImage(int width, int height, Color[] colors, byte[] alpha, int... buf) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

        for (int y = height - 1, offs = 0; y >= 0; y--) {
            for (int x = 0; x < width; x++, offs++) {
                int red = colors[buf[offs]].getRed();
                int green = colors[buf[offs]].getGreen();
                int blue = colors[buf[offs]].getBlue();
                int rgb = new Color(red, green, blue, alpha[offs] & 255).getRGB();
                image.setRGB(x, y, rgb);
            }
        }

        return image;
    }

    private Bitmap() {
    }
}
