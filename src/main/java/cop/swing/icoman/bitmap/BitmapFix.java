package cop.swing.icoman.bitmap;

import javax.imageio.stream.ImageInputStream;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 30.07.2015
 */
public final class BitmapFix extends Bitmap {
    public BitmapFix(BitmapInfoHeader header, ImageInputStream in) throws IOException {
        super(header);
        readImage(in);
    }

    private void bitLess8(BitmapInfoHeader header, byte[] buf, Color[] colorTable, byte[] bitMasks, byte[] data, BufferedImage bufferedImage) {
        int bitCount = header.getBiBitCount();
        int n;
        int n2 = 0;
        int n3 = width;
        int n4 = data.length / height;
        int n5 = 0;
        int n6 = 0;
        int n7;
        int n8;
        int[] buf1 = new int[width * height];
        if (bitCount == 8) {
            if (bitMasks.length == buf1.length) {
                for (n8 = 0; n8 < buf1.length; ++n8) {
                    buf1[n8] = bitMasks[n8] & 255;
                }
            } else {
                n2 = 0;
                n4 = n3;
                n5 = 0;
                n6 = bitMasks.length / height - width;
                for (n8 = 0; n8 < bitMasks.length; ++n8) {
                    buf1[n2++] = bitMasks[n8] & 255;
                    if (++n5 != n4) continue;
                    n5 = 0;
                    n8 += n6;
                }
            }
        } else if (bitCount == 4) {
            n = 0;
            if (bitMasks.length * 2 == buf1.length) {
                for (n8 = 0; n8 < bitMasks.length; ++n8) {
                    buf1[n++] = (bitMasks[n8] & 255) >> 4;
                    buf1[n++] = (bitMasks[n8] & 255) >> 4 << 4 ^ bitMasks[n8] & 255;
                }
            } else {
                n2 = 0;
                n4 = n3;
                n5 = 0;
                n6 = bitMasks.length * 2 / height - width;
                n8 = 1;
                for (n7 = 0; n7 < bitMasks.length; ++n7) {
                    if (n8 != 0) {
                        buf1[n++] = (bitMasks[n7] & 255) >> 4;
                    }
                    if (n8 != 0 && ++n5 == n4) {
                        n5 = 0;
                        n8 = 0;
                    } else if (n8 == 0 && n5 == n6) {
                        n5 = 0;
                        n8 = 1;
                    }
                    if (n8 != 0) {
                        buf1[n++] = (bitMasks[n7] & 255) >> 4 << 4 ^ bitMasks[n7] & 255;
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
        } else if (bitCount == 1) {
            n2 = 0;
            n3 = width;
            n4 = data.length / height;
            n5 = 0;
            n6 = 0;
            for (n8 = 0; n8 < bitMasks.length; ++n8) {
                ++n5;
                for (n7 = 7; n7 >= 0; --n7) {
                    if (n6 >= n3) continue;
                    buf1[n2++] = (1 << n7 & bitMasks[n8]) != 0 ? 1 : 0;
                    ++n6;
                }
                if (n5 != n4) continue;
                n5 = 0;
                n6 = 0;
            }
        }
        n = 0;
        for (n7 = height - 1; n7 >= 0; --n7) {
            for (int j = 0; j < width; ++j) {
                Color color = new Color(colorTable[buf1[n]].getRed(), colorTable[buf1[n]].getGreen(), colorTable[buf1[n]].getBlue(),
                        buf[n] & 255);
                bufferedImage.setRGB(j, n7, color.getRGB());
                ++n;
            }
        }
    }

    // ========== Bitmap ==========

    @Override
    protected BufferedImage createImage(ImageInputStream in) throws IOException {
        BitmapInfoHeader header = new BitmapInfoHeader(in);
        Color[] colorTable = readColorTable(header, in);
        byte[] bitMasks = readBitMasks(header, in);
        byte[] data = readData(header, in);
        int bitCount = header.getBiBitCount();
        byte[] buf = new byte[width * height];

        int n;
        int n2 = 0;
        int n3 = width;
        int n4 = data.length / height;
        int n5 = 0;
        int n6 = 0;
        for (int i = 0; i < data.length; ++i) {
            ++n5;
            for (n = 7; n >= 0; --n) {
                if (n6 >= n3) continue;
                buf[n2++] = (byte)((1 << n & data[i]) != 0 ? 0 : -1);
                ++n6;
            }
            if (n5 != n4) continue;
            n5 = 0;
            n6 = 0;
        }
        n = 0;
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

        if (bitCount <= 8) {
            bitLess8(header, buf, colorTable, bitMasks, data, bufferedImage);
        } else {
            n = 0;
            if (bitCount == 24) {
                int n9 = 0;
                n6 = bitMasks.length == 3 * height * width ? 0 : bitMasks.length / height - width * 3;
                for (int j = height - 1; j >= 0; --j) {
                    for (int k = 0; k < width; ++k) {
                        int n10 = bitMasks[n++] & 255;
                        int n11 = bitMasks[n++] & 255;
                        int n12 = bitMasks[n++] & 255;
                        Color color = new Color(n12, n11, n10, buf[n9++] & 255);
                        bufferedImage.setRGB(k, j, color.getRGB());
                    }
                    n += n6;
                }
            } else if (bitCount == 32) {
                for (int j = height - 1; j >= 0; --j) {
                    for (int k = 0; k < width; ++k) {
                        int n13 = bitMasks[n++] & 255;
                        int n14 = bitMasks[n++] & 255;
                        int n15 = bitMasks[n++] & 255;
                        int n16 = bitMasks[n++] & 255;
                        Color color = new Color(n15, n14, n13, n16);
                        bufferedImage.setRGB(k, j, color.getRGB());
                    }
                }
            }
        }

        return bufferedImage;
    }

    // ========== static ==========

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
        int height = header.getBiHeight() / 2;
        int bitCount = header.getBiBitCount();
        byte[] buf = new byte[(width * bitCount + 31) / 32 * 4 * height];

        in.read(buf);

        return buf;
    }

    private static byte[] readData(BitmapInfoHeader header, ImageInputStream in) throws IOException {
        int width = header.getBiWidth();
        int height = header.getBiHeight() / 2;
        byte[] buf = new byte[(width + 31) / 32 * 4 * height];

        in.read(buf);

        return buf;
    }
}
