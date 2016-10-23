package cop.icoman.icl;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 08.10.2016
 */
final class DataDirectory {
    private final long virtualAddress;
    private final long size;

    public static DataDirectory read(ImageInputStream in) throws IOException {
        long virtualAddress = in.readUnsignedInt();
        long size = in.readUnsignedInt();
        return virtualAddress != 0 || size != 0 ? new DataDirectory(virtualAddress, size) : null;
    }

    private DataDirectory(long virtualAddress, long size) {
        this.virtualAddress = virtualAddress;
        this.size = size;
    }

    public long getVirtualAddress() {
        return virtualAddress;
    }

    public long getSize() {
        return size;
    }
}
