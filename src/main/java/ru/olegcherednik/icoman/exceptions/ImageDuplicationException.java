package ru.olegcherednik.icoman.exceptions;

import ru.olegcherednik.icoman.ImageKey;

/**
 * @author Oleg Cherednik
 * @since 05.09.2013
 */
public class ImageDuplicationException extends IconManagerException {
	private static final long serialVersionUID = 599978908932500627L;

	public ImageDuplicationException(ImageKey key) {
		super("duplicate image key '" + key + '\'');
	}
}
