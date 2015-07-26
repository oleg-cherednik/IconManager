package cop.swing.icoman.imageio.bmp;

import cop.swing.icoman.exceptions.IconManagerException;
import cop.swing.icoman.bitmap.Bitmap;
import cop.swing.icoman.bitmap.BitmapInfoHeader;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Oleg Cherednik
 * @since 02.09.2013
 */
public class IconBitmapReader extends ImageReader {
	private BitmapInfoHeader header;
	private BufferedImage image;

	protected IconBitmapReader(IconBitmapReaderSpi originatingProvider) {
		super(originatingProvider);
	}

	private BufferedImage getImage() throws IOException {
		try {
			if (image != null)
				return image;

			ImageInputStream in = (ImageInputStream)input;
			in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
			in.mark();
			header = new BitmapInfoHeader(in);
			in.reset();

			int bitsPerPixel = header.getBiBitCount();
			int width = header.getBiWidth();
			int height = header.getBiWidth();   // height is 2*width I don't know why

			return image = Bitmap.getBitmap(bitsPerPixel, width, height, in).getImage();
		} catch(IconManagerException e) {
			header = null;
			image = null;
			throw new IOException(e);
		}
	}

	// ========== ImageReader ==========

	public void setInput(Object input) {
		if (input != null && !(input instanceof ImageInputStream)) {
			try {
				input = ImageIO.createImageInputStream(input);
				((ImageInputStream)input).setByteOrder(ByteOrder.LITTLE_ENDIAN);
			} catch(IOException e) {
				e.printStackTrace();
			}
		}

		setInput(input, false, false);
	}

	/**
	 * Returns the height in pixels of the given image within the input source.
	 *
	 * @param index the index of the image to be queried
	 * @return the height of the image, as an <code>int</code>
	 * @throws IOException if an error occurs reading the height information from the input source
	 */
	public int getHeight(int index) throws IOException {
		try {
			return header.getBiHeight();
		} catch(Exception e) {
			throw new IOException(e);
		}
	}

	/**
	 * Returns an {@link IIOMetadata} object containing metadata associated with the given image, or
	 * <code>null</code> if
	 * the reader does not support  reading metadata, is set to ignore metadata, or if no metadata is  available.
	 * <p/>
	 * example:
	 * <pre>
	 * IIOMetadata meta = reader.getImageMetadata(imageNr);
	 * IIOMetadataNode n = (IIOMetadataNode)meta.getAsTree(meta.getNativeMetadataFormatName());
	 * if (n.hasChildNodes()) {
	 * org.w3c.dom.NodeList nl = n.getChildNodes();
	 * for (int childNr=0;childNr<nl.getLength();childNr++) {
	 * IIOMetadataNode child =(IIOMetadataNode) nl.item(childNr);
	 * String key = child.getAttribute("keyword");
	 * if (key != null && key.equals("bpp")) {
	 * bpp = child.getAttribute("value");
	 * break;
	 * }
	 * }
	 * }
	 * </pre>
	 * the available keywords are:<br />
	 * width, height, colorCount, bitCount, bpp, reserved, planes <br />
	 * note that bitCount & bpp are the same.<br />
	 * <br />
	 *
	 * @param index the index of the image whose metadata is to be
	 *              retrieved.
	 * @return an <code>IIOMetadata</code> object, or <code>null</code>.
	 * @throws IOException if an error occurs during reading.
	 * @todo Implement this javax.imageio.ImageReader method
	 */
	public IIOMetadata getImageMetadata(int index) throws IOException {
		try {
			getImage();

			IconBitmapMetaData metaData = new IconBitmapMetaData();
			metaData.put("width", Integer.toString(header.getBiWidth()));
			metaData.put("height", Integer.toString(header.getBiHeight()));
			metaData.put("colorCount", Integer.toString(header.getBiColorsUsed()));
			metaData.put("bitCount", Integer.toString(header.getBiBitCount()));
			metaData.put("bpp", Integer.toString(header.getBiBitCount()));
			metaData.put("planes", Integer.toString(header.getBiPlanes()));

			return metaData;
		} catch(Exception e) {
			throw new IIOException("Exception reading metadata", e);
		}
	}

	/**
	 * Returns an <code>Iterator</code> containing possible image types to which
	 * the given image may be decoded, in the form of
	 * <code>ImageTypeSpecifiers</code>s.
	 *
	 * @param index the index of the image to be <code>retrieved</code>.
	 * @return an <code>Iterator</code> containing at least one
	 *         <code>ImageTypeSpecifier</code> representing suggested image types for
	 *         decoding the current given image.
	 * @throws IOException if an error occurs reading the format information
	 *                     from the input source.
	 * @todo Implement this javax.imageio.ImageReader method
	 */
	public Iterator<ImageTypeSpecifier> getImageTypes(int index) throws IOException {
		return new Iterator<ImageTypeSpecifier>() {
			boolean hasN = true;

			public boolean hasNext() {
				return hasN;
			}

			public ImageTypeSpecifier next() {
				if (!hasN) {
					throw new NoSuchElementException();
				}
				hasN = false;
				return ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_ARGB);
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * Returns the number of images, not including thumbnails, available from the current input source.
	 *
	 * @param allowSearch if <code>true</code>, the true number of images will  be returned even if a search is required.
	 *                    If <code>false</code>, the  reader may return <code>-1</code> without performing the search.
	 * @return the number of images, as an <code>int</code>, or <code>-1</code>
	 *         if <code>allowSearch</code> is <code>false</code> and a search would be
	 *         required.
	 * @throws IOException if an error occurs reading the information from the
	 *                     input source.
	 * @todo Implement this javax.imageio.ImageReader method
	 */
	public int getNumImages(boolean allowSearch) throws IOException {
		return 1;
	}

	/**
	 * Returns an {@link IIOMetadata} object representing the metadata associated with the input
	 * source as a whole (i.e.,
	 * not associated with any  particular image), or <code>null</code> if the reader does not support  reading metadata,
	 * is set to ignore metadata, or if no metadata is  available.
	 *
	 * @return an <code>IIOMetadata</code> object, or <code>null</code>.
	 * @throws IOException if an error occurs during reading.
	 * @todo Implement this javax.imageio.ImageReader method
	 */
	public IIOMetadata getStreamMetadata() throws IOException {
		return null;
	}

	/**
	 * Returns the width in pixels of the given image within the input source.
	 *
	 * @param imageIndex the index of the image to be queried.
	 * @return the width of the image, as an <code>int</code>.
	 * @throws IOException if an error occurs reading the width information from
	 *                     the input source.
	 * @todo Implement this javax.imageio.ImageReader method
	 */
	public int getWidth(int imageIndex) throws IOException {
		try {
			return header.getBiWidth();
		} catch(Exception e) {
			throw new IOException(e);
		}
	}

	/**
	 * Reads the image indexed by <code>index</code> and returns it as a complete <code>BufferedImage</code>, using a
	 * supplied  <code>ImageReadParam</code>.
	 * <p/>
	 * The BufferedImage that is returned is of the type:  BufferedImage.TYPE_INT_ARGB
	 * and all transparent pixels in the source ico will be transparent in the buffered image
	 *
	 * @param index the index of the image to be retrieved.
	 * @param param an <code>ImageReadParam</code> used to control the reading
	 *              process, or <code>null</code>.
	 * @return the desired portion of the image as a <code>BufferedImage</code>.
	 * @throws IOException if an error occurs during reading.
	 */
	public BufferedImage read(int index, ImageReadParam param) throws IOException {
		return getImage();
	}
}
