package cop.swing.icoman.exceptions;

import cop.swing.icoman.ImageKey;
import cop.swing.icoman.exceptions.IconManagerException;

/**
 * @author Oleg Cherednik
 * @since 05.09.2013
 */
public class ImageNotFoundException extends IconManagerException {
	private static final long serialVersionUID = -6470950436657824550L;

	public ImageNotFoundException(int pos, int total) {
		super("image pos = " + pos + ", total = " + total + " not found");
	}

	public ImageNotFoundException(ImageKey key) {
		super("image key = '" + key + "' not found");
	}
}
