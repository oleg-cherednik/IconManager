package cop.icoman.icl;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 08.10.2016
 */
final class ResourceDirectory {
    private final long characteristics;
    private final long timeDateStamp;
    private final int majorVersion;
    private final int minorVersion;
    private final int numberOfNamedEntries;
    private final int numberOfIdEntries;

    public ResourceDirectory(ImageInputStream in) throws IOException {
        characteristics = in.readUnsignedInt();
        timeDateStamp = in.readUnsignedInt();
        majorVersion = in.readUnsignedShort();
        minorVersion = in.readUnsignedShort();
        numberOfNamedEntries = in.readUnsignedShort();
        numberOfIdEntries = in.readUnsignedShort();
    }

    public long getCharacteristics() {
        return characteristics;
    }

    public long getTimeDateStamp() {
        return timeDateStamp;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public int getNumberOfNamedEntries() {
        return numberOfNamedEntries;
    }

    public int getNumberOfIdEntries() {
        return numberOfIdEntries;
    }
}
