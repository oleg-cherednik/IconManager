package cop.swing.icoman.exceptions;

import cop.swing.icoman.ImageKey;
import cop.swing.icoman.exceptions.IconManagerException;

/**
 * @author Oleg Cherednik
 * @since 05.09.2013
 */
public class IconNotFoundException extends IconManagerException {
	private static final long serialVersionUID = -6470950436657824550L;

	public IconNotFoundException(int id, int total) {
		super("icon id = " + id + ", total = " + total + " not found");
	}

	public IconNotFoundException(ImageKey key) {
		super("icon key = '" + key + "' not found");
	}

	public IconNotFoundException(String name) {
		super("icon file = '" + name + "' not found");
	}
}
