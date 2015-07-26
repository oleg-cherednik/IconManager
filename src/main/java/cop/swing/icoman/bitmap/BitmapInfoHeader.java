package cop.swing.icoman.bitmap;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

/**
 * @author Oleg Cherednik
 * @since 01.09.2013
 */
public final class BitmapInfoHeader {

	/** The size of this header (size: 4, offs: 0x0) */
	private final int biSize;
	/** The bitmap width in pixels (size: 4, offs: 0x0) */
	private final int biWidth;
	/** The bitmap height in pixels (size: 4, offs: 0x0) */
	private final int biHeight;
	/** The number of color planes being used, <b>must be set to 1</b>. (size: 2, offs: 0x0) */
	private final int biPlanes;
	/**
	 * The number of bits per pixel, which is the color depth of the image. Typical values are 1, 4, 8, 16, 24 and 32
	 * (size: 2, offs: 0x0)
	 */
	private final int biBitCount;
	/** The bitmap height in pixels (size: 4, offs: 0x0) */
	private final int biCompression;
	/** The bitmap height in pixels (size: 4, offs: 0x0) */
	private final int biSizeImage;
	/** The bitmap height in pixels (size: 4, offs: 0x0) */
	private final int biXPelsPerMeter;
	/** The bitmap height in pixels (size: 4, offs: 0x0) */
	private final int biYPelsPerMeter;
	/** The bitmap height in pixels (size: 4, offs: 0x0) */
	private final int biColorsUsed;
	/** The bitmap height in pixels (size: 4, offs: 0x0) */
	private final int biColorsImportant;

//	0Eh	4	the size of this header (40 bytes)
//	12h	4	the bitmap width in pixels (signed integer).
//			16h	4	the bitmap height in pixels (signed integer).
//			1Ah	2	the number of color planes being used. Must be set to 1.
//			1Ch	2	the number of bits per pixel, which is the color depth of the image. Typical values are 1, 4, 8, 16, 24 and 32.
//			1Eh	4	the compression method being used. See the next table for a list of possible values.
//	22h	4	the image size. This is the size of the raw bitmap data (see below), and should not be confused with the file size.
//			26h	4	the horizontal resolution of the image. (pixel per meter, signed integer)
//			2Ah	4	the vertical resolution of the image. (pixel per meter, signed integer)
//			2Eh	4	the number of colors in the color palette, or 0 to default to 2n.
//	32h	4	the number of important colors used, or 0 when every color is important; generally ignored.

	public BitmapInfoHeader(InputStream in) throws IOException {
		this(createImageInputStream(in));
	}

	public BitmapInfoHeader(ImageInputStream in) throws IOException {
		biSize = (int)in.readUnsignedInt();
		biWidth = in.readInt();
		biHeight = in.readInt();
		biPlanes = in.readUnsignedShort();
		biBitCount = in.readUnsignedShort();
		biCompression = (int)in.readUnsignedInt();
		biSizeImage = (int)in.readUnsignedInt();
		biXPelsPerMeter = in.readInt();
		biYPelsPerMeter = in.readInt();
		biColorsUsed = (int)in.readUnsignedInt();
		biColorsImportant = (int)in.readUnsignedInt();
	}

	public int getBiSize() {
		return biSize;
	}

	public int getBiWidth() {
		return biWidth;
	}

	public int getBiHeight() {
		return biHeight;
	}

	public int getBiPlanes() {
		return biPlanes;
	}

	public int getBiBitCount() {
		return biBitCount;
	}

	public int getBiCompression() {
		return biCompression;
	}

	public int getBiSizeImage() {
		return biSizeImage;
	}

	public int getBiXPelsPerMeter() {
		return biXPelsPerMeter;
	}

	public int getBiYPelsPerMeter() {
		return biYPelsPerMeter;
	}

	public int getBiColorsUsed() {
		return biColorsUsed;
	}

	public int getBiColorsImportant() {
		return biColorsImportant;
	}

	// ========== static ==========

	private static ImageInputStream createImageInputStream(InputStream in) throws IOException {
		ImageInputStream is = ImageIO.createImageInputStream(in);
		is.setByteOrder(ByteOrder.LITTLE_ENDIAN);
		return is;
	}
}
