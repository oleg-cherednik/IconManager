package ru.olegcherednik.icoman.icl;

import lombok.Data;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.util.Map;

/**
 * @author Oleg Cherednik
 * @see <a href="https://msdn.microsoft.com/ru-ru/library/windows/desktop/ms680339(v=vs.85).aspx">IMAGE_OPTIONAL_HEADER structure</a>
 * @since 03.10.2016
 */
@Data
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
    private final Map<DataDirectory.Entry, DataDirectory> dataDirectories;

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
        dataDirectories = DataDirectory.Entry.read(in);
    }
}
