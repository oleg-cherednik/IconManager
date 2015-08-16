package cop.swing.icoman;

import cop.swing.icoman.exceptions.IconManagerException;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 03.07.2013
 */
public enum BitmapType {
	NONE(0),
	ICO(1) {
		@Override
		public IconImageHeader createImageHeader(int id, ImageInputStream in) throws IOException, IconManagerException {
			return IconImageHeader.readHeader(id, in);
		}
	},
	CUR(2);

	private final int code;

	BitmapType(int code) {
		this.code = code;
	}

	public IconImageHeader createImageHeader(int id, ImageInputStream in) throws IOException, IconManagerException {
		throw new IconManagerException("'header.type' " + name() + " if image no. " + id + " + is not supported");
	}

	// ========== static ==========

	public static BitmapType parseCode(int code) {
		for (BitmapType type : values())
			if (type.code == code)
				return type;

		return NONE;
	}
}
