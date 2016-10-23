package cop.icoman.icl;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

/**
 * @author Oleg Cherednik
 * @since 03.10.2016
 */
final class OptionalHeader {
    private final int magic;
    private final int majorLinkerVersion;
    private final int minorLinkerVersion;
    private final long sizeOfCode;
    private final long sizeOfInitializedData;
    private final long sizeOfUninitializedData;
    private final long addressOfEntryPoint;
    private final long baseOfCode;
    private final long baseOfData;
    private final long imageBase;
    private final long sectionAlignment;
    private final long fileAlignment;
    private final int majorOperatingSystemVersion;
    private final int minorOperatingSystemVersion;
    private final int majorImageVersion;
    private final int minorImageVersion;
    private final int majorSubsystemVersion;
    private final int minorSubsystemVersion;
    private final long win32VersionValue;
    private final long sizeOfImage;
    private final long sizeOfHeaders;
    private final long checkSum;
    private final int subsystem;
    private final int dllCharacteristics;
    private final long sizeOfStackReserve;
    private final long sizeOfStackCommit;
    private final long sizeOfHeapReserve;
    private final long sizeOfHeapCommit;
    private final long loaderFlags;
    private final long numberOfRvaAndSizes;
    private final Map<DirectoryEntry, DataDirectory> dataDirectories = new EnumMap<>(DirectoryEntry.class);

    public OptionalHeader(ImageInputStream in) throws IOException {
        magic = in.readUnsignedShort();
        majorLinkerVersion = in.readUnsignedByte();
        minorLinkerVersion = in.readUnsignedByte();
        sizeOfCode = in.readUnsignedInt();
        sizeOfInitializedData = in.readUnsignedInt();
        sizeOfUninitializedData = in.readUnsignedInt();
        addressOfEntryPoint = in.readUnsignedInt();
        baseOfCode = in.readUnsignedInt();
        baseOfData = in.readUnsignedInt();
        imageBase = in.readUnsignedInt();
        sectionAlignment = in.readUnsignedInt();
        fileAlignment = in.readUnsignedInt();
        majorOperatingSystemVersion = in.readUnsignedShort();
        minorOperatingSystemVersion = in.readUnsignedShort();
        majorImageVersion = in.readUnsignedShort();
        minorImageVersion = in.readUnsignedShort();
        majorSubsystemVersion = in.readUnsignedShort();
        minorSubsystemVersion = in.readUnsignedShort();
        win32VersionValue = in.readUnsignedInt();
        sizeOfImage = in.readUnsignedInt();
        sizeOfHeaders = in.readUnsignedInt();
        checkSum = in.readUnsignedInt();
        subsystem = in.readUnsignedShort();
        dllCharacteristics = in.readUnsignedShort();
        sizeOfStackReserve = in.readUnsignedInt();
        sizeOfStackCommit = in.readUnsignedInt();
        sizeOfHeapReserve = in.readUnsignedInt();
        sizeOfHeapCommit = in.readUnsignedInt();
        loaderFlags = in.readUnsignedInt();
        numberOfRvaAndSizes = in.readUnsignedInt();
        readDataDirectories(in);
    }

    private void readDataDirectories(ImageInputStream in) throws IOException {
        DataDirectory dataDirectory;

        for (DirectoryEntry entry : DirectoryEntry.values())
            if ((dataDirectory = DataDirectory.read(in)) != null)
                dataDirectories.put(entry, dataDirectory);
    }

    public int getMagic() {
        return magic;
    }

    public int getMajorLinkerVersion() {
        return majorLinkerVersion;
    }

    public int getMinorLinkerVersion() {
        return minorLinkerVersion;
    }

    public long getSizeOfCode() {
        return sizeOfCode;
    }

    public long getSizeOfInitializedData() {
        return sizeOfInitializedData;
    }

    public long getSizeOfUninitializedData() {
        return sizeOfUninitializedData;
    }

    public long getAddressOfEntryPoint() {
        return addressOfEntryPoint;
    }

    public long getBaseOfCode() {
        return baseOfCode;
    }

    public long getBaseOfData() {
        return baseOfData;
    }

    public long getImageBase() {
        return imageBase;
    }

    public long getSectionAlignment() {
        return sectionAlignment;
    }

    public long getFileAlignment() {
        return fileAlignment;
    }

    public int getMajorOperatingSystemVersion() {
        return majorOperatingSystemVersion;
    }

    public int getMinorOperatingSystemVersion() {
        return minorOperatingSystemVersion;
    }

    public int getMajorImageVersion() {
        return majorImageVersion;
    }

    public int getMinorImageVersion() {
        return minorImageVersion;
    }

    public int getMajorSubsystemVersion() {
        return majorSubsystemVersion;
    }

    public int getMinorSubsystemVersion() {
        return minorSubsystemVersion;
    }

    public long getWin32VersionValue() {
        return win32VersionValue;
    }

    public long getSizeOfImage() {
        return sizeOfImage;
    }

    public long getSizeOfHeaders() {
        return sizeOfHeaders;
    }

    public long getCheckSum() {
        return checkSum;
    }

    public int getSubsystem() {
        return subsystem;
    }

    public int getDllCharacteristics() {
        return dllCharacteristics;
    }

    public long getSizeOfStackReserve() {
        return sizeOfStackReserve;
    }

    public long getSizeOfStackCommit() {
        return sizeOfStackCommit;
    }

    public long getSizeOfHeapReserve() {
        return sizeOfHeapReserve;
    }

    public long getSizeOfHeapCommit() {
        return sizeOfHeapCommit;
    }

    public long getLoaderFlags() {
        return loaderFlags;
    }

    public long getNumberOfRvaAndSizes() {
        return numberOfRvaAndSizes;
    }

    public DataDirectory getDataDirectory(DirectoryEntry directoryEntry) {
        return dataDirectories.get(directoryEntry);
    }

    // ========== enum ==========

    public enum DirectoryEntry {
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
        COM_DESCRIPTOR
    }
}
