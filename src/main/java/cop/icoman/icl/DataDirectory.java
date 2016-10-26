package cop.icoman.icl;

import lombok.Data;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @see <a href="https://msdn.microsoft.com/ru-ru/library/windows/desktop/ms680305(v=vs.85).aspx">IMAGE_DATA_DIRECTORY structure</a>
 * @since 08.10.2016
 */
@Data
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
}
