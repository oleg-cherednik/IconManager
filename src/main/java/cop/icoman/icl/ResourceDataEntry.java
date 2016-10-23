package cop.icoman.icl;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 21.10.2016
 */
public class ResourceDataEntry {
    public final long rva;
    public final int size;
    public final long codePage;
    public final long reserved;

    public ResourceDataEntry(ImageInputStream in) throws IOException {
        rva = in.readUnsignedInt();
        size = (int)in.readUnsignedInt();
        codePage = in.readUnsignedInt();
        reserved = in.readUnsignedInt();
    }
}
