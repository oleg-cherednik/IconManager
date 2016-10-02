package cop.icoman.ico.imageio;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormatImpl;

/**
 * @author Oleg Cherednik
 * @see <a href="http://docs.oracle.com/javase/7/docs/technotes/guides/imageio/spec/extending.fm3.html">Java Image I/O
 *      API Guide. Writing Reader Plug-Ins</a>
 * @since 01.09.2013
 */
public final class IcoMetaDataFormat extends IIOMetadataFormatImpl {
	public static final String NAME = IcoMetaData.class.getName() + "_1.0";

	private static final IcoMetaDataFormat INSTANCE = new IcoMetaDataFormat();

	public static IcoMetaDataFormat getInstance() {
		return INSTANCE;
	}

	private IcoMetaDataFormat() {
		super(NAME, CHILD_POLICY_REPEAT - 1);

		addElement("KeywordValuePair", NAME, CHILD_POLICY_EMPTY);
		addAttribute("KeywordValuePair", "keyword", DATATYPE_STRING, true, null);
		addAttribute("KeywordValuePair", "value", DATATYPE_STRING, true, null);
	}

	// ========== IIOMetadataFormat ==========

	@Override
	public boolean canNodeAppear(String name, ImageTypeSpecifier imageType) {
		return "KeywordValuePair".equals(name);
	}
}
