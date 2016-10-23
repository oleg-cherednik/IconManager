package cop.icoman.icl;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 08.10.2016
 */
final class Misc {
    /**
     * The file address
     */
    private final long physicalAddress;
    /**
     * The total size of the section when loaded into memory, in bytes. If this value is greater than the {@link SectionHeader#sizeOfRawData} member,
     * the section is filled with zeroes. This field is valid only for executable images and should be set to 0 for object files.
     */
    private final long virtualSize;

    public Misc(ImageInputStream in) throws IOException {
        physicalAddress = in.readUnsignedInt();
        virtualSize = -1;//in.readUnsignedInt();
    }

    public long getPhysicalAddress() {
        return physicalAddress;
    }

    public long getVirtualSize() {
        return virtualSize;
    }
}
