package cop.swing.icoman.imageio.ico;

import cop.swing.icoman.IconFile;
import cop.swing.icoman.IconImage;
import cop.swing.icoman.IconImageHeader;
import cop.swing.icoman.ImageKey;
import cop.swing.icoman.exceptions.IconManagerException;
import cop.swing.icoman.exceptions.ImageNotFoundException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class reads Microsoft ICO files and converts them to a BufferedImage. Notice that the transparent pixels are
 * kept transparent. The BufferedImage type is {@link BufferedImage#TYPE_INT_ARGB}
 *
 * @author Oleg Cherednik
 * @since 01.09.2013
 */
public class IconReader extends ImageReader {
	private IconFile icon;

	protected IconReader(ImageReaderSpi originatingProvider) {
		super(originatingProvider);
	}

	private IconFile getIcoFile() throws IOException {
		try {
			if (icon != null)
				return icon;

			ImageInputStream in = (ImageInputStream)input;
			in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
			byte[] buff = new byte[4];
			in.mark();
			in.readFully(buff);
			in.reset();
//check header
			boolean res = (buff[0] == 0x00 && buff[1] == 0x00 && buff[2] == 0x01 && buff[3] == 0x00);
			if (!res) {
				System.err.println(
						"ICOReader: Incorrect header -- this should have been detected by the ICOReaderSpi -- did anyone tamper with the inputstream (for example the WBMPImageReader sometimes does that)? make sure you have a fresh imageinputstream before calling the read method!.....");
			}
			IconFile file = IconFile.read(in);
			icon = file;
			return file;
		} catch(IconManagerException e) {
			throw new IOException(e);
		}
	}

	// ========== ImageReader ==========

	public void setInput(Object input) {
		if (input != null && !(input instanceof ImageInputStream)) {
			try {
				input = ImageIO.createImageInputStream(input);
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
			return getIcoFile().getImage(index).getHeader().getImageKey().getHeight();
		} catch(ImageNotFoundException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Returns an {@link IIOMetadata} object containing metadata associated with the given image, or <code>null</code> if
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
			IconFile file = getIcoFile();
			IconImage image = file.getImage(index);
			IconImageHeader header = image.getHeader();
			ImageKey imageKey = header.getImageKey();

			IconMetaData metaData = new IconMetaData();
			metaData.put("width", Integer.toString(imageKey.getWidth()));
			metaData.put("height", Integer.toString(imageKey.getHeight()));
			metaData.put("colorCount", Integer.toString(imageKey.getColors()));
			metaData.put("bitCount", Integer.toString(header.getBitsPerPixel()));
			metaData.put("bpp", Integer.toString(header.getBitsPerPixel()));
			metaData.put("planes", Integer.toString(header.getColorPlanes()));

			return metaData;
		} catch(Exception e) {
			throw new IOException(e);
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
		return icon == null && !allowSearch ? -1 : getIcoFile().getImagesAmount();
	}

	/**
	 * Returns an {@link IIOMetadata} object representing the metadata associated with the input source as a whole (i.e.,
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
			return getIcoFile().getImage(imageIndex).getHeader().getImageKey().getWidth();
		} catch(ImageNotFoundException e) {
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
		try {
			return (BufferedImage)getIcoFile().getImage(index).getIcon().getImage();
		} catch(ImageNotFoundException e) {
			throw new IOException(e);
		}
	}
}
