package cop.swing.icoman.bitmap;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

/**
 * To store general information about the Bitmap Image File
 *
 * @author Oleg Cherednik
 * @since 01.09.2013
 */
public final class BitmapFileHeader {
	public static final int SIZE = 14;

	/**
	 * The header field used to identify the BMP & DIB file is 0x42 0x4D in hexadecimal, same as BM in ASCII.
	 * <ul>
	 * The following entries are possible:
	 * <li><b>BM</b> – Windows 3.1x, 95, NT, ... etc.
	 * <li><b>BA</b> – OS/2 struct Bitmap Array
	 * <li><b>CI</b> – OS/2 struct Color Icon
	 * <li><b>CP</b> – OS/2 const Color Pointer
	 * <li><b>IC</b> – OS/2 struct Icon
	 * <li><b>PT</b> – OS/2 Pointer
	 * </ul>
	 */
	private final int bfType;
	/** The size of the BMP file in bytes (size: 4, offs: 0x2) */
	private final int bfSize;
	/** Reserved; actual value depends on the application that creates the image (size: 2, offs: 0x6) */
//	private final int bfReserved1;
	/** Reserved; actual value depends on the application that creates the image (size: 2, offs: 0x8) */
//	private final int bfReserved2;
	/**
	 * The offset, i.e. starting address, of the byte where the bitmap image data (pixel array)
	 * can be found (size: 4, offs: 0x10)
	 */
	private final int bfOffBits;

	public static BitmapFileHeader createWindowsBitmap(int size, int offs) {
		return new BitmapFileHeader(BitmapType.WINDOWS, size, offs);
	}

	public BitmapFileHeader(InputStream in) throws IOException {
		this(createImageInputStream(in));
	}

	public BitmapFileHeader(ImageInputStream in) throws IOException {
		bfType = in.readUnsignedShort();
		bfSize = in.readInt();
		in.skipBytes(4);
		bfOffBits = in.readInt();
	}

	private BitmapFileHeader(BitmapType type, int size, int offs) {
		bfType = type.getId();
		bfSize = size;
		bfOffBits = offs;
	}

	public void write(ImageOutputStream os) throws IOException {
		os.writeShort(bfType);
		os.writeInt(bfSize);
		os.skipBytes(4);
		os.writeInt(bfOffBits);
	}

	// ========== static ==========

	private static ImageInputStream createImageInputStream(InputStream in) throws IOException {
		ImageInputStream is = ImageIO.createImageInputStream(in);
		is.setByteOrder(ByteOrder.LITTLE_ENDIAN);
		return is;
	}
}
