package cop.icoman.icl.radar.struct;

import cop.icoman.icl.radar.Radar;

/**
 * @author Oleg Cherednik
 * @since 14.10.2016
 */
public class Pe64_image_file_header {
    public static final int SIZE = 20;

    public int /*ut16*/ Machine;
    public int /*ut16*/ NumberOfSections;
    public long /*ut32*/ TimeDateStamp;
    public long /*ut32*/ PointerToSymbolTable;
    public long /*ut32*/ NumberOfSymbols;
    public int /*ut16*/ SizeOfOptionalHeader;
    public int /*ut16*/ Characteristics;

    public void read(byte[] b, int offs) {
        Machine = Radar.ut16(b, offs);
        NumberOfSections = Radar.ut16(b, offs + 2);
        TimeDateStamp = Radar.ut32(b, offs + 4);
        PointerToSymbolTable = Radar.ut32(b, offs + 8);
        NumberOfSymbols = Radar.ut32(b, offs + 12);
        SizeOfOptionalHeader = Radar.ut16(b, offs + 16);
        Characteristics = Radar.ut16(b, offs + 18);
    }
}
