package cop.swing.icoman.bitmap;

import cop.swing.icoman.imageio.bmp.IconBitmap;

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

    // ========== Bitmap ==========

    @Override
    protected BufferedImage createImage(ImageInputStream in) throws IOException {
        //		IconFileImage iconImage
        int n;
        IconBitmap iconBitmap = new IconBitmap(in);
        int bitCount = iconBitmap.getHeader().getBiBitCount();
        byte[] buf = new byte[width * height];
        byte[] colorTable = iconBitmap.getColorTable();
        int n2 = 0;
        int n3 = width;
        int n4 = colorTable.length / height;
        int n5 = 0;
        int n6 = 0;
        for (int i = 0; i < colorTable.length; ++i) {
            ++n5;
            for (n = 7; n >= 0; --n) {
                if (n6 >= n3) continue;
                buf[n2++] = (byte)((1 << n & colorTable[i]) != 0 ? 0 : -1);
                ++n6;
            }
            if (n5 != n4) continue;
            n5 = 0;
            n6 = 0;
        }
        n = 0;
        byte[] bitMasks = iconBitmap.getBitMasks();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        if (bitCount <= 8) {
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
                n4 = colorTable.length / height;
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
            Color[] data = iconBitmap.getData();
            for (n7 = height - 1; n7 >= 0; --n7) {
                for (int j = 0; j < width; ++j) {
                    Color color = new Color(data[buf1[n]].getRed(), data[buf1[n]].getGreen(), data[buf1[n]].getBlue(), buf[n] & 255);
                    bufferedImage.setRGB(j, n7, color.getRGB());
                    ++n;
                }
            }
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
}
