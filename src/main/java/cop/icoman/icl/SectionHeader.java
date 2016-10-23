package cop.icoman.icl;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 08.10.2016
 */
final class SectionHeader {
    /**
     * An 8-byte, null-padded UTF-8 string. There is no terminating null character if the string is exactly eight characters long. For longer names,
     * this member contains a forward slash (/) followed by an ASCII representation of a decimal number that is an offset into the string table.
     * Executable images do not use a string table and do not support section names longer than eight characters.
     * For {@link OptionalHeader.DirectoryEntry#RESOURCE} should be <t>.rsrc</t>.
     */
    private final String name;
    private final long misc;
    /**
     * The address of the first byte of the section when loaded into memory, relative to the image base. For object files, this is the address of the
     * first byte before relocation is applied.
     */
    long virtualAddress;
    /**
     * The size of the initialized data on disk, in bytes. This value must be a multiple of the {@link OptionalHeader#fileAlignment}. If this value
     * is less than {@link Misc#virtualSize}, the remainder of the section is filled with zeroes. If the section contains only uninitialized data, the
     * member is zero.
     */
    long sizeOfRawData;
    long pointerToRawData;
    long pointerToRelocations;
    long pointerToLinenumbers;
    int numberOfRelocations;
    int numberOfLinenumbers;
    long characteristics;

    public SectionHeader(ImageInputStream in) throws IOException {
        name = readString(in, 8);
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

    public String getName() {
        return name;
    }

    public long getMisc() {
        return misc;
    }

    public long getVirtualAddress() {
        return virtualAddress;
    }

    public long getSizeOfRawData() {
        return sizeOfRawData;
    }

    public long getPointerToRawData() {
        return pointerToRawData;
    }

    public long getPointerToRelocations() {
        return pointerToRelocations;
    }

    public long getPointerToLinenumbers() {
        return pointerToLinenumbers;
    }

    public int getNumberOfRelocations() {
        return numberOfRelocations;
    }

    public int getNumberOfLinenumbers() {
        return numberOfLinenumbers;
    }

    public long getCharacteristics() {
        return characteristics;
    }

    // ========== Object ==========

    @Override
    public String toString() {
        return name;
    }

    // ========== static ==========

    public static String readString(ImageInputStream in, int length) throws IOException {
        byte ch;
        StringBuilder buf = new StringBuilder();

        for (int i = 0; i < length; i++)
            if ((ch = in.readByte()) != '\0')
                buf.append((char)ch);

        return buf.toString();
    }
}
