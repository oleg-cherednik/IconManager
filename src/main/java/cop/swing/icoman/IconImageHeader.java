package cop.swing.icoman;

import cop.swing.icoman.exceptions.IconManagerException;

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
	// private int res; // size: 1, offs: 0x3 (0 or 255, ignored)
	private final int colorPlanes; // size: 2, offs: 0x4 (...)
	private final int bitsPerPixel; // size: 2, offs: 0x6 (...)
	private final int size; // size: 4, offs: 0x8 (bitmap data size)
	private final int offs; // size: 4, offs: 0xC (bitmap data offset)

	public static IconImageHeader readHeader(int id, ImageInputStream in) throws IconManagerException, IOException {
		int width = fix(in.readUnsignedByte());
		int height = fix(in.readUnsignedByte());
		in.readUnsignedByte();  // colors

		skipByte(id, in);

		int colorPlanes = in.readShort();
		int bitsPerPixel = in.readShort();
		int size = in.readInt();
		int offs = in.readInt();

		ImageKey key = ImageKey.createKey(width, height, bitsPerPixel);

		check(key, colorPlanes, bitsPerPixel, size, offs);

		return new IconImageHeader(id, key, colorPlanes, bitsPerPixel, size, offs);
	}

	private IconImageHeader(int id, ImageKey key, int colorPlanes, int bitsPerPixel, int size, int offs) {
		this.id = id;
		this.key = key;
		this.colorPlanes = colorPlanes;
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

	public int getColorPlanes() {
		return colorPlanes;
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
		return key.toString() + ", planes: " + colorPlanes + ", size: " + size + ", offs: " + offs;
	}

	// ========== static ==========

	private static void check(ImageKey key, int colorPlanes, int bitsPerPixel, int size, int offs) {
	}

	private static void skipByte(int id, DataInput in) throws IOException, IconManagerException {
		int val = in.readUnsignedByte();

		if (val != 0 && val != 255)
			throw new IconManagerException(
					"'header offs:0, size:2' of image no. " + id + " is reserved, should be 0 or 255");
	}

	private static int fix(int size) {
		return size != 0 ? size : 256;
	}
}
