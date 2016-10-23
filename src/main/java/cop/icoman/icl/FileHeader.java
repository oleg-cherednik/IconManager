package cop.icoman.icl;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 03.10.2016
 */
final class FileHeader {
    private final int machine;    // size: 2, rva: 0x4
    /**
     * The number of sections. This indicates the size of the section table, which immediately follows the headers. Note that the Windows loader
     * limits the number of sections to 96.
     */
    private final int numberOfSection;    // size: 2, rva: 0x6
    private final long timestamp;     // size: 4, rva 0x8
    private final long pointerToSymbolTable;  // size: 4, rva: 0xC
    private final long numberOfSymbols;   // size: 4, rva: 0x10
    private final int sizeOfOptionalHeader;   // size: 2, rva: 0x14
    private final int characteristics;    // size: 2, rva: 0x16

    public FileHeader(ImageInputStream in) throws IOException {
        // total size: 20

        machine = in.readUnsignedShort();
        numberOfSection = in.readUnsignedShort();
        timestamp = in.readUnsignedInt();  // 1 января, 1970, по Greenwich (Англия)
        pointerToSymbolTable = in.readUnsignedInt();
        numberOfSymbols = in.readUnsignedInt();
        sizeOfOptionalHeader = in.readUnsignedShort();
        characteristics = in.readUnsignedShort();
    }

    public int getMachine() {
        return machine;
    }

    public int getNumberOfSection() {
        return numberOfSection;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getPointerToSymbolTable() {
        return pointerToSymbolTable;
    }

    public long getNumberOfSymbols() {
        return numberOfSymbols;
    }

    public int getSizeOfOptionalHeader() {
        return sizeOfOptionalHeader;
    }

    public int getCharacteristics() {
        return characteristics;
    }
}
