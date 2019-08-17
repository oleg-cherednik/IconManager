package com.cop.icoman.exceptions;

/**
 * @author Oleg Cherednik
 * @since 14.09.2013
 */
public class IconDuplicationException extends IconManagerException {
	private static final long serialVersionUID = -2579360463735036878L;

	public IconDuplicationException(String icon) {
		super("duplicate icon '" + icon + '\'');
	}
}
