package com.cop.icoman.icl;

import lombok.Data;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @see <a href="https://msdn.microsoft.com/ru-ru/library/windows/desktop/ms680313(v=vs.85).aspx">IMAGE_FILE_HEADER structure</a>
 * @since 03.10.2016
 */
@Data
final class FileHeader {
    private final int machine;
    private final int numberOfSection;
    private final long timestamp;
    private final long pointerToSymbolTable;
    private final long numberOfSymbols;
    private final int sizeOfOptionalHeader;
    private final int characteristics;

    public FileHeader(ImageInputStream in) throws IOException {
        machine = in.readUnsignedShort();
        numberOfSection = in.readUnsignedShort();
        timestamp = in.readUnsignedInt();
        pointerToSymbolTable = in.readUnsignedInt();
        numberOfSymbols = in.readUnsignedInt();
        sizeOfOptionalHeader = in.readUnsignedShort();
        characteristics = in.readUnsignedShort();
    }
}
