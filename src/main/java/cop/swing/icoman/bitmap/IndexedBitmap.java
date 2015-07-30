package cop.swing.icoman.bitmap;

import cop.swing.icoman.exceptions.IconManagerException;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Oleg Cherednik
 * @since 01.09.2013
 */
public final class IndexedBitmap extends Bitmap {
	private static final Color TRANSPARENT = new Color(0, 0, 0, 0);

	private final byte[] RGBQUAD;
	private final byte[] XOR;
	private final byte[] AND;

	public IndexedBitmap(BitmapInfoHeader header, InputStream in) throws IOException, IconManagerException {
		this(header, ImageIO.createImageInputStream(in));
	}

	public IndexedBitmap(BitmapInfoHeader header, ImageInputStream in) throws IOException, IconManagerException {
		super(header);

		int XORmaskSize = header.getBiWidth() * header.getBiHeight() / 2 * header.getBiBitCount() / 8;
		int ANDMaskSize = Math.max(header.getBiWidth(), 32) * header.getBiHeight() / 2 / 8;
		int RGBQuardSize = header.getBiBitCount() <= 8 ? (int)Math.pow(2, header.getBiBitCount()) * 4 : 0;

		check(XORmaskSize, ANDMaskSize, RGBQuardSize);

		RGBQUAD = readBytes(in, RGBQuardSize);
		XOR = readBytes(in, XORmaskSize);
		AND = readBytes(in, ANDMaskSize);

		readImage(in);
	}

	protected BufferedImage createImage(ImageInputStream in) throws IOException {
		if (width < 1 || height < 1) {
			System.err.println("java.lang.IllegalArgumentException: Width (0) and height (0) cannot be <= 0");
			return null;
		}

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		//g.setBackground(Color.white);

		for (int y = header.getBiHeight() / 2 - 1; y >= 0; y--) {
			for (int x = 0; x < header.getBiWidth(); x++) {
				if (!hasAlpha(x, y)) {
					g.setColor(getRGB(x, y));
					//        int[] rgb = getRGB(x, y);
					//      currentColor = new Color(rgb[0], rgb[1], rgb[2]);
				} else {
					g.setColor(TRANSPARENT);
				}
				// System.out.println("FillRect: " + x +","+(h - y) + "     y="+y);
				/**
				 * changed: g.fillRect(x, h - y , 1, 1);
				 * to:  g.fillRect(x, h - y - 1, 1, 1);
				 *  because the icons were missing the bottom line of pixels
				 * I'm not sure this is the correct sollution, but it seems to work.
				 */
				g.fillRect(x, height - y - 1, 1, 1);
			}
		}
//System.out.println("--------");
		return image;
	}


	/**
	 * returns the rgb value of the color
	 *
	 * @param xx int
	 * @param yy int
	 * @return int[]{red, green, blue} or int[]{red, green, blue,alpha}
	 */
	private /*int[]*/Color getRGB(int xx, int yy) {
		int bbc = header.getBiBitCount(); // (Integer) meta.get("biBitCount");
		if (bbc > 8) {
			System.err.println(
					"This class can only handle bpp values of < 16.... (<=8 actually) but the current bpp value is " + bbc + "  you may get unexpected results");
		}
		int bbyte = yy * header.getBiWidth() + xx; // en alpha es 32 fijo
		int pixelsPerByte = 8 / header.getBiBitCount(); // can be 1 (biBitCount=8), 2 (biBitCount=4) or 8 (biBitCount=1)
		bbyte /= pixelsPerByte; // $n=$xx%8 $n => 0..7  7-$n => 7..0        0..7 0..1 0
		int shift = (pixelsPerByte - xx % pixelsPerByte - 1) * header.getBiBitCount();
		int colIdx = ord(XOR[bbyte]) >> shift & (1 << header.getBiBitCount()) - 1;
		// 1 bit   8ppb   0,1,2,3,4,5,6,7   >> 0,1,2,3,4,5,6,7   % 8 = 0,1,2,3,4,5,6,7 * 1
		// 4 bits  2ppb   0,4               >> 0,4               % 2 = 0,1   * 4 = 0,4
		// 8 bits  1ppb   0                 >> 0                 % 1 = 0     * 8 = 0
		int b = ord(this.RGBQUAD[4 * colIdx]);
		int g = ord(this.RGBQUAD[4 * colIdx + 1]);
		int r = ord(this.RGBQUAD[4 * colIdx + 2]);

		return new Color(r, g, b);
	}


	/**
	 * make unsigned
	 *
	 * @param c byte
	 * @return int
	 */
	private int ord(byte c) {
		return (int)((c < 0) ? c + 256 : c);
	}

	/**
	 * ****************************
	 * alpha, returns 1 if mask pixel
	 * is transparent
	 * *****************************
	 */
	private boolean hasAlpha(int xx, int yy) {
		int bbyte = yy * 32 + xx; // super.biWidth... hmmm maybe aligned to long? that's it i think... fix this
		bbyte = (int)(bbyte / 8);
		int c = ord(this.AND[bbyte]);
		int res = (c >> (7 - xx % 8)) & 1;
		return (res == 1);
	}

	// ========== static ==========

	private static byte[] readBytes(ImageInputStream is, int length) throws IOException {
		byte[] buf = new byte[length];
		is.read(buf);
		return buf;
	}

	private static void check(int XORmaskSize, int ANDMaskSize, int RGBQuardSize) throws IconManagerException {
		if (XORmaskSize > 500000)
			throw new IconManagerException("XOR mask to large... " + XORmaskSize);
		if (ANDMaskSize > 500000)
			throw new IconManagerException("AND mask to large... " + ANDMaskSize);
		if (RGBQuardSize > 500000)
			throw new IconManagerException("RGBQUAD mask to large... " + RGBQuardSize);
	}
}
