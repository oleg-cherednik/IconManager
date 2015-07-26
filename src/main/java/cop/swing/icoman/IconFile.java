package cop.swing.icoman;

import cop.swing.icoman.exceptions.ImageDuplicationException;
import cop.swing.icoman.exceptions.IconManagerException;
import cop.swing.icoman.exceptions.ImageNotFoundException;

import javax.imageio.stream.ImageInputStream;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Oleg Cherednik
 * @since 03.07.2013
 */
public final class IconFile implements Iterable<IconImage> {
	private final IconFileHeader header;
	private final Map<ImageKey, IconImage> images;

	public static IconFile read(ImageInputStream in) throws IOException, IconManagerException {
		IconFileHeader header = IconFileHeader.readHeader(in);
		Map<ImageKey, IconImage> images = readImages(header, in);
		return new IconFile(header, images);
	}

	private IconFile(IconFileHeader header, Map<ImageKey, IconImage> images) {
		assert header != null && header != IconFileHeader.NULL;
		assert images != null && !images.isEmpty();

		this.header = header;
		this.images = images;
	}

	public IconFileHeader getHeader() {
		return header;
	}

	@NotNull
	public Set<ImageKey> getKeys() {
		return images.isEmpty() ? Collections.<ImageKey>emptySet() : Collections.unmodifiableSet(images.keySet());
	}

	@NotNull
	public Set<ImageKey> getSortedKeys() {
		if (images.isEmpty())
			return Collections.emptySet();
		return Collections.unmodifiableSet(new TreeSet<ImageKey>(images.keySet()));
	}

	@NotNull
	public IconImage getImage(int pos) throws ImageNotFoundException {
		int i = 0;

		for (IconImage image : images.values())
			if (i++ == pos)
				return image;

		throw new ImageNotFoundException(pos, images.size());
	}

	@NotNull
	public IconImage getImage(ImageKey key) throws ImageNotFoundException {
		IconImage image = images.get(key);

		if (image == null)
			throw new ImageNotFoundException(key);

		return image;
	}

	public int getImagesAmount() {
		return images.size();
	}

	// ========== Object ==========

	@Override
	public String toString() {
		return header.toString();
	}

	// ========== static ==========

	private static List<IconImageHeader> readImageHeaders(int total, ImageInputStream in)
			throws IOException, IconManagerException {
		assert total > 0;
		assert in != null;

		List<IconImageHeader> headers = new ArrayList<>(total);

		for (int i = 0; i < total; i++)
			headers.add(BitmapType.ICO.createImageHeader(i, in));

		return Collections.unmodifiableList(headers);
	}

	private static Map<ImageKey, IconImage> readImages(IconFileHeader fileHeader, ImageInputStream in)
			throws IOException, IconManagerException {
		List<IconImageHeader> imageHeaders = readImageHeaders(fileHeader.getImageCount(), in);
		Map<ImageKey, IconImage> images = new LinkedHashMap<>(imageHeaders.size());
		int offs = IconFileHeader.SIZE + imageHeaders.size() * IconImageHeader.SIZE;

		for (IconImageHeader imageHeader : imageHeaders) {
			checkOffs(offs, imageHeader);
			byte[] data = readData(imageHeader.getSize(), in);

			if (images.put(imageHeader.getImageKey(), IconImage.createImage(imageHeader, data)) != null)
				throw new ImageDuplicationException(imageHeader.getImageKey());

			offs += imageHeader.getSize();
		}

		return Collections.unmodifiableMap(images);
	}

	private static byte[] readData(int size, ImageInputStream in) throws IOException {
		byte[] data = new byte[size];
		in.readFully(data);
		return data;
	}

	private static void checkOffs(int expected, IconImageHeader imageHeader) throws IconManagerException {
		if (expected != imageHeader.getOffs())
			throw new IconManagerException("offs image no. " + imageHeader.getId() + " incorrect. actual=" +
					imageHeader.getOffs() + ", expected=" + expected);
	}

	// ========== Iterable ==========

	@Override
	public Iterator<IconImage> iterator() {
		return images.values().iterator();
	}
}
