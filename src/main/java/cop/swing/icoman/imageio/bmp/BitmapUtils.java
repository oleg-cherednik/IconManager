package cop.swing.icoman.imageio.bmp;

import javax.imageio.stream.ImageInputStream;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 01.09.2013
 */
public final class BitmapUtils {
    static byte[] readBitMasks(BitmapInfoHeader header, ImageInputStream in) throws IOException {
        int width = header.getBiWidth();
        int height = header.getBiHeight();
        int bitCount = header.getBiBitCount();
        byte[] buf = new byte[(width * bitCount + 31) / 32 * 4 * height];

        in.read(buf);

        return buf;
    }

    static byte[] readData(BitmapInfoHeader header, ImageInputStream in) throws IOException {
        int width = header.getBiWidth();
        int height = header.getBiHeight();
        byte[] buf = new byte[(width + 31) / 32 * 4 * height];

        in.read(buf);

        return buf;
    }

    static int[] create1bitAlpha(BitmapInfoHeader header, byte[] data) {
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

    public static void print(int width, int height, int[] buf) {
        height = Math.abs(height);

        for (int i = 0, offs = 0; i < height && offs < buf.length; i++) {
            for (int j = 0; j < width && offs < buf.length; j++, offs++)
                System.out.print(buf[offs] == 0x0 ? '.' : '#');
            System.out.println();
        }
    }

    static BufferedImage createImage24(int width, int height, int[] alpha, byte... mask) {
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

    static BufferedImage create32bitImage(int width, int height, byte... mask) {
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

    static int rgb(int rgb, int alpha) {
        return rgb(rgb >> 16, rgb >> 8, rgb, alpha);
    }

    public static int rgb(int red, int green, int blue) {
        return rgb(red, green, blue, 0xFF);
    }

    private static int rgb(int red, int green, int blue, int alpha) {
        return ((alpha & 0xFF) << 24) | ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | (blue & 0xFF);
    }

    private BitmapUtils() {
    }
}
