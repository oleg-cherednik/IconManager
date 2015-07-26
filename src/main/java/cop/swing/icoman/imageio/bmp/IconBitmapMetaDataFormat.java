package cop.swing.icoman.imageio.bmp;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormatImpl;

/**
 * @author Oleg Cherednik
 * @see <a href="http://docs.oracle.com/javase/7/docs/technotes/guides/imageio/spec/extending.fm3.html">Java Image I/O
 *      API Guide. Writing Reader Plug-Ins</a>
 * @since 02.09.2013
 */
public final class IconBitmapMetaDataFormat extends IIOMetadataFormatImpl {
	public static final String NAME = IconBitmapMetaDataFormat.class.getName() + "_1.0";

	private static final IconBitmapMetaDataFormat INSTANCE = new IconBitmapMetaDataFormat();

	public static IconBitmapMetaDataFormat getINSTANCE() {
		return INSTANCE;
	}

	private IconBitmapMetaDataFormat() {
		super(NAME, CHILD_POLICY_REPEAT - 1);

		addElement("KeywordValuePair", NAME, CHILD_POLICY_EMPTY);
		addAttribute("KeywordValuePair", "keyword", DATATYPE_STRING, true, null);
		addAttribute("KeywordValuePair", "value", DATATYPE_STRING, true, null);
	}

	// ========== IIOMetadataFormat ==========

	public boolean canNodeAppear(String name, ImageTypeSpecifier imageType) {
		return "KeywordValuePair".equals(name);
	}
}
