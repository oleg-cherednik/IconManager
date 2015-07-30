package cop.swing.icoman.imageio.bmp;

import cop.swing.icoman.bitmap.BitmapInfoHeader;

import javax.imageio.stream.ImageInputStream;
import java.awt.Color;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 30.07.2015
 */
public class IconBitmap {
    private final BitmapInfoHeader header;
    private final Color[] data;
    private final byte[] bitMasks;
    private final byte[] colorTable;

    public IconBitmap(ImageInputStream in) throws IOException {
        header = new BitmapInfoHeader(in);
        data = readData(header, in);
        bitMasks = readBitMasks(header, in);
        colorTable = readColorTable(header, in);
    }

    private static Color[] readData(BitmapInfoHeader header, ImageInputStream in) throws IOException {
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

    private static byte[] readColorTable(BitmapInfoHeader header, ImageInputStream in) throws IOException {
        int width = header.getBiWidth();
        int height = header.getBiHeight() / 2;
        byte[] buf = new byte[(width + 31) / 32 * 4 * height];

        in.read(buf);

        return buf;
    }

    public BitmapInfoHeader getHeader() {
        return header;
    }

    public Color[] getData() {
        return data;
    }

    public byte[] getBitMasks() {
        return bitMasks;
    }

    public byte[] getColorTable() {
        return colorTable;
    }
}
