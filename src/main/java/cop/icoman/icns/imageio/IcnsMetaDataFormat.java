package cop.icoman.icns.imageio;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormatImpl;

/**
 * @author Oleg Cherednik
 * @see <a href="http://docs.oracle.com/javase/7/docs/technotes/guides/imageio/spec/extending.fm3.html">Java Image I/O
 * API Guide. Writing Reader Plug-Ins</a>
 * @since 14.08.2015
 */
public final class IcnsMetaDataFormat extends IIOMetadataFormatImpl {
    public static final String NAME = IcnsMetaData.class.getName() + "_1.0";

    private static final IcnsMetaDataFormat INSTANCE = new IcnsMetaDataFormat();

    public static IcnsMetaDataFormat getInstance() {
        return INSTANCE;
    }

    private IcnsMetaDataFormat() {
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
