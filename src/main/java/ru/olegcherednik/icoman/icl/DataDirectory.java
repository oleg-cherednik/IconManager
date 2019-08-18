package ru.olegcherednik.icoman.icl;

import lombok.Data;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

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

    public enum Entry {
        EXPORT,
        IMPORT,
        RESOURCE,
        EXCEPTION,
        SECURITY,
        BASERELOC,
        DEBUG,
        COPYRIGHT,
        ARCHITECTURE,
        GLOBALPTR,
        TLS,
        LOAD_CONFIG,
        BOUND_IMPORT,
        IAT,
        DELAY_IMPORT,
        COM_DESCRIPTOR;

        public long getRva(OptionalHeader optionalHeader) {
            return optionalHeader.getDataDirectories().get(this).getRva();
        }

        // ========== static ==========

        public static Map<DataDirectory.Entry, DataDirectory> read(ImageInputStream in) throws IOException {
            DataDirectory dataDirectory;
            Map<DataDirectory.Entry, DataDirectory> dataDirectories = new EnumMap<>(DataDirectory.Entry.class);

            for (DataDirectory.Entry entry : values())
                if ((dataDirectory = DataDirectory.read(in)) != null)
                    dataDirectories.put(entry, dataDirectory);

            return Collections.unmodifiableMap(dataDirectories);
        }
    }
}
