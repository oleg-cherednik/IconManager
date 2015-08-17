package cop.swing.icoman.icns;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 16.08.2015
 */
public final class IcnsFileHeader {
    public static final IcnsFileHeader NULL = new IcnsFileHeader();

    public static IcnsFileHeader read(ImageInputStream in) throws IOException {
        int marker = in.readInt();  // marker,  = 'icns' (offs: 0x0, size: 4)
        long size = in.readUnsignedInt();   // file size, = in.length() (offs: 0x4, size: 4)
        return NULL;
    }

    private IcnsFileHeader() {
    }
}
