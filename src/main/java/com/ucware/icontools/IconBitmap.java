package com.ucware.icontools;

import com.ucware.coff.A.IconFileImage;
import com.ucware.coff.Header;

import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class IconBitmap {
    private Bitmap bitmap;
    private byte[] bitMasks;
    private byte[] colorTable;

    public IconBitmap(IconFileImage e) {
        this(e.getData());
    }

    public IconBitmap(byte[] buf) {
        try (ByteArrayInputStream in = new ByteArrayInputStream(buf)) {
            BitmapInfoHeader header = Header.read(in, new BitmapInfoHeader());
            int width = header.getWidth();
            int height = header.getHeight();
            int bitCount = header.getField("biBitCount").readInt();
            int size = bitCount <= 8 ? (int)Math.pow(2.0, bitCount) : 0;

            BitMask[] data = null;

            if (size > 0) {
                data = new BitMask[size];

                for (int i = 0; i < data.length; ++i)
                    data[i] = Header.read(in, new BitMask());
            }

            bitmap = new Bitmap(header, data);
            bitMasks = new byte[(width * bitCount + 31) / 32 * 4 * height];
            colorTable = new byte[(width + 31) / 32 * 4 * height];
            in.read(bitMasks);
            in.read(colorTable);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public IconBitmap(ImageInputStream in) {
        try {
            BitmapInfoHeader header = Header.read(in, new BitmapInfoHeader());
            int width = header.getWidth();
            int height = header.getHeight();
            int bitCount = header.getField("biBitCount").readInt();
            int size = bitCount <= 8 ? (int)Math.pow(2.0, bitCount) : 0;

            BitMask[] data = null;

            if (size > 0) {
                data = new BitMask[size];

                for (int i = 0; i < data.length; ++i)
                    data[i] = Header.read(in, new BitMask());
            }

            bitmap = new Bitmap(header, data);
            bitMasks = new byte[(width * bitCount + 31) / 32 * 4 * height];
            colorTable = new byte[(width + 31) / 32 * 4 * height];
            in.read(bitMasks);
            in.read(colorTable);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public byte[] getBitMasks() {
        return bitMasks;
    }

    public byte[] getColorTable() {
        return colorTable;
    }
}
