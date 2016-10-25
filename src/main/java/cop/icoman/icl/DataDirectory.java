package cop.icoman.icl;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 08.10.2016
 */
final class DataDirectory {
    private final long rva;
    private final long size;

    public static DataDirectory read(ImageInputStream in) throws IOException {
        long rva = in.readUnsignedInt();
        long size = in.readUnsignedInt();
        return rva != 0 || size != 0 ? new DataDirectory(rva, size) : null;
    }

    private DataDirectory(long rva, long size) {
        this.rva = rva;
        this.size = size;
    }

    public long getRva() {
        return rva;
    }

    public long getSize() {
        return size;
    }
}
