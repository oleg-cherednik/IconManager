package cop.icoman.icl;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 08.10.2016
 */
public class ResourceDirectoryEntry {
    public static final int SIZE = 8;

    public final boolean leaf;
    public final int id;
    public final long offsName;
    public final long offsData;

    public ResourceDirectoryEntry(ImageInputStream in) throws IOException {
        int nameId = in.readInt();
        int dataPtr = in.readInt();

        leaf = dataPtr >= 0;
        id = nameId >= 0 ? (nameId & 0xFF) : -1;
        offsName = nameId >= 0 ? -1 : (nameId & 0x7FFFFFFF);
        offsData = leaf ? dataPtr : (dataPtr & 0x7FFFFFFF);
    }
}
