package com.cop.icoman.icl;

import com.cop.icoman.IconIO;
import lombok.Data;
import lombok.ToString;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @see <a href="https://msdn.microsoft.com/ru-ru/library/windows/desktop/ms680341(v=vs.85).aspx">IMAGE_SECTION_HEADER structure</a>
 * @since 08.10.2016
 */
@Data
@ToString(includeFieldNames = false, of = "name")
final class SectionHeader {
    private final String name;
    private final long misc;
    private final long virtualAddress;
    private final long sizeOfRawData;
    private final long pointerToRawData;
    private final long pointerToRelocations;
    private final long pointerToLinenumbers;
    private final int numberOfRelocations;
    private final int numberOfLinenumbers;
    private final long characteristics;

    public SectionHeader(ImageInputStream in) throws IOException {
        name = IconIO.readString(in, 8);
        misc = in.readUnsignedInt();
        virtualAddress = in.readUnsignedInt();
        sizeOfRawData = in.readUnsignedInt();
        pointerToRawData = in.readUnsignedInt();
        pointerToRelocations = in.readUnsignedInt();
        pointerToLinenumbers = in.readUnsignedInt();
        numberOfRelocations = in.readUnsignedShort();
        numberOfLinenumbers = in.readUnsignedShort();
        characteristics = in.readUnsignedInt();
    }
}
