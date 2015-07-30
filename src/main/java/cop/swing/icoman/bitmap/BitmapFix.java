package cop.swing.icoman.bitmap;

import cop.swing.icoman.exceptions.FormatNotSupportedException;

import javax.imageio.stream.ImageInputStream;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 30.07.2015
 */
public final class BitmapFix extends Bitmap {
    public BitmapFix(BitmapInfoHeader header, ImageInputStream in) throws IOException, FormatNotSupportedException {
        super(header);
        readImage(in);
    }

    private static BufferedImage createImage1(int width, int height, byte[] data, Color[] colors, byte[] alpha, byte... mask) {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        int n;
        int n2 = 0;
        int n3 = width;
        int n4 = data.length / height;
        int n5 = 0;
        int n6 = 0;
        int n7;
        int n8;
        int[] buf1 = new int[width * height];

        n2 = 0;
        n3 = width;
        n4 = data.length / height;
        n5 = 0;
        n6 = 0;
        for (n8 = 0; n8 < mask.length; ++n8) {
            ++n5;
            for (n7 = 7; n7 >= 0; --n7) {
                if (n6 >= n3) continue;
                buf1[n2++] = (1 << n7 & mask[n8]) != 0 ? 1 : 0;
                ++n6;
            }
            if (n5 != n4) continue;
            n5 = 0;
            n6 = 0;
        }

        return createImage(width, height, colors, alpha, buf1);
    }

    private static BufferedImage createImage4(int width, int height, byte[] data, Color[] colors, byte[] alpha, byte[] mask) {
        int n;
        int n2 = 0;
        int n3 = width;
        int n4 = data.length / height;
        int n5 = 0;
        int n6 = 0;
        int n7;
        int n8;
        int[] buf1 = new int[width * height];

        n = 0;
        if (mask.length * 2 == buf1.length) {
            for (n8 = 0; n8 < mask.length; ++n8) {
                buf1[n++] = (mask[n8] & 255) >> 4;
                buf1[n++] = (mask[n8] & 255) >> 4 << 4 ^ mask[n8] & 255;
            }
        } else {
            n2 = 0;
            n4 = n3;
            n5 = 0;
            n6 = mask.length * 2 / height - width;
            n8 = 1;
            for (n7 = 0; n7 < mask.length; ++n7) {
                if (n8 != 0) {
                    buf1[n++] = (mask[n7] & 255) >> 4;
                }
                if (n8 != 0 && ++n5 == n4) {
                    n5 = 0;
                    n8 = 0;
                } else if (n8 == 0 && n5 == n6) {
                    n5 = 0;
                    n8 = 1;
                }
                if (n8 != 0) {
                    buf1[n++] = (mask[n7] & 255) >> 4 << 4 ^ mask[n7] & 255;
                }
                if (n8 != 0 && ++n5 == n4) {
                    n5 = 0;
                    n8 = 0;
                    continue;
                }
                if (n8 != 0 || n5 != n6) continue;
                n5 = 0;
                n8 = 1;
            }
        }

        return createImage(width, height, colors, alpha, buf1);
    }

    private static BufferedImage createImage8(int width, int height, byte[] data, Color[] colors, byte[] alpha, byte... mask) {
        int n;
        int n2 = 0;
        int n3 = width;
        int n4 = data.length / height;
        int n5 = 0;
        int n6 = 0;
        int n7;
        int[] buf = new int[width * height];

        if (mask.length == buf.length)
            for (int offs = 0; offs < buf.length; offs++)
                buf[offs] = mask[offs] & 255;
        else {
            n2 = 0;
            n4 = n3;
            n5 = 0;
            n6 = mask.length / height - width;
            for (int n8 = 0; n8 < mask.length; ++n8) {
                buf[n2++] = mask[n8] & 255;
                if (++n5 != n4) continue;
                n5 = 0;
                n8 += n6;
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

//        for (int y = height - 1, offAlpha = 0; y >= 0; y--) {
//            for (int x = 0; x < width; x++) {
//                int red = colorTable[buf[offAlpha]].getRed();
//                int green = colorTable[buf[offAlpha]].getGreen();
//                int blue = colorTable[buf[offAlpha]].getBlue();
//                int rgb = new Color(red, green, blue, alpha[offAlpha] & 255).getRGB();
//                image.setRGB(x, y, rgb);
//            }
//        }

        int n = 0;
        for (int n7 = height - 1; n7 >= 0; --n7) {
            for (int j = 0; j < width; ++j) {
                Color color = new Color(colors[buf[n]].getRed(), colors[buf[n]].getGreen(), colors[buf[n]].getBlue(),
                        alpha[n] & 255);
                image.setRGB(j, n7, color.getRGB());
                ++n;
            }
        }

        return image;
    }

    // ========== Bitmap ==========

    @Override
    protected BufferedImage createImage(ImageInputStream in) throws IOException, FormatNotSupportedException {
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
            return createImage4(width, height, data, colorTable, alpha, mask);
        if (bitCount == 8)
            return createImage8(width, height, data, colorTable, alpha, mask);
        if (bitCount == 24)
            return createImage24(width, height, alpha, mask);
        if (bitCount == 32)
            return createImage32(width, height, mask);

        throw new FormatNotSupportedException("Bitmap with " + bitCount + "bit is not supported");
    }

    // ========== static ==========

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
}
