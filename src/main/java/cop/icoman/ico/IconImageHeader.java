package cop.icoman.ico;

import cop.icoman.ImageKey;
import cop.icoman.exceptions.IconManagerException;

import javax.imageio.stream.ImageInputStream;
import java.io.DataInput;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 01.09.2013
 */
public final class IconImageHeader {
    public static final int SIZE = 16;

    private final int id;
    private final ImageKey key;
    // private int res; // size: 1, rva: 0x3 (0 or 255, ignored)
    private final int planes; // size: 2, rva: 0x4 (...)
    private final int bitsPerPixel; // size: 2, rva: 0x6 (...)
    private final int size; // size: 4, rva: 0x8 (bitmap data size)
    private final int offs; // size: 4, rva: 0xC (bitmap data offset)

    public static IconImageHeader readHeader(int id, ImageInputStream in) throws IconManagerException, IOException {
        int width = zeroTo256(in.readUnsignedByte());
        int height = zeroTo256(in.readUnsignedByte());
        int colors = in.readUnsignedByte();

        skipByte(id, in);

        int planes = in.readShort();
        int bitsPerPixel = bitsPerPixel(in.readShort(), colors);
        int size = in.readInt();
        int offs = in.readInt();

        ImageKey key = ImageKey.custom(width, height, bitsPerPixel);

        return new IconImageHeader(id, key, planes, bitsPerPixel, size, offs);
    }

    private IconImageHeader(int id, ImageKey key, int planes, int bitsPerPixel, int size, int offs) {
        this.id = id;
        this.key = key;
        this.planes = planes;
        this.bitsPerPixel = bitsPerPixel;
        this.size = size;
        this.offs = offs;
    }

    public int getId() {
        return id;
    }

    public ImageKey getImageKey() {
        return key;
    }

    public int getPlanes() {
        return planes;
    }

    public int getBitsPerPixel() {
        return bitsPerPixel;
    }

    public int getSize() {
        return size;
    }

    public int getOffs() {
        return offs;
    }

    // ========== Object ==========

    @Override
    public String toString() {
        return key.toString() + ", planes: " + planes + ", size: " + size + ", rva: " + offs;
    }

    // ========== static ==========

    private static void skipByte(int id, DataInput in) throws IOException, IconManagerException {
        int val = in.readUnsignedByte();

        if (val != 0 && val != 255)
            throw new IconManagerException("'header rva:0, size:2' of image no. " + id + " is reserved, should be 0 or 255");
    }

    @SuppressWarnings("StaticMethodNamingConvention")
    private static int zeroTo256(int size) {
        return size != 0 ? size : 256;
    }

    private static int bitsPerPixel(int bitsPerPixel, int colors) {
        return bitsPerPixel != 0 ? bitsPerPixel : (int)Math.sqrt(colors);
    }
}
