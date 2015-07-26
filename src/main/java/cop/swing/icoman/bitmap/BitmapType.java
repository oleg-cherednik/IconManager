package cop.swing.icoman.bitmap;

/**
 * The header field used to identify the BMP & DIB file is 0x42 0x4D in hexadecimal, same as BM in ASCII
 *
 * @author Oleg Cherednik
 * @since 01.09.2013
 */
enum BitmapType {
	/** Windows 3.1x, 95, NT, ... etc */
	WINDOWS("BM"),
	/** OS/2 struct Bitmap Array */
	BITMAP_ARRAY("BA"),
	/** OS/2 struct Color Icon */
	COLOR_ICON("CI"),
	/** OS/2 const Color Pointer */
	COLOR_POINTER("CP"),
	/** OS/2 struct Icon */
	ICON("IC"),
	/** OS/2 Pointer */
	POINTER("PT");

	private final int id;

	BitmapType(String id) {
		this.id = id.charAt(1) << 8 | id.charAt(0);
	}

	public int getId() {
		return id;
	}
}
