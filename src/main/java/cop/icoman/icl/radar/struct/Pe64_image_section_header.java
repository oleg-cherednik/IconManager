package cop.icoman.icl.radar.struct;

import cop.icoman.icl.radar.Constant;
import cop.icoman.icl.radar.Radar;

/**
 * @author Oleg Cherednik
 * @since 14.10.2016
 */
public class Pe64_image_section_header {
    public static final int SIZE = 32;

    public final char[] /*ut16*/ Name = new char[Constant.PE_IMAGE_SIZEOF_SHORT_NAME];
    public final Misc Misc = new Misc();
    public long /*ut32*/ VirtualAddress;
    public long /*ut32*/ SizeOfRawData;
    public long /*ut32*/ PointerToRawData;
    public long /*ut32*/ PointerToRelocations;
    public long /*ut32*/ PointerToLinenumbers;
    public int /*ut16*/ NumberOfRelocations;
    public int /*ut16*/ NumberOfLinenumbers;
    public long /*ut32*/ Characteristics;

    public static class Misc {
        public long /*ut32*/ PhysicalAddress;
        public long /*ut32*/ VirtualSize;
    }

    public void read(byte[] b, int offs) {
        for (int i = 0; i < Name.length; i++)
            Name[i] = (char)b[offs + i];

        Misc.PhysicalAddress = Radar.ut32(b, offs + 8);
        Misc.VirtualSize = Misc.PhysicalAddress;

        VirtualAddress = Radar.ut32(b, offs + 12);
        SizeOfRawData = Radar.ut32(b, offs + 16);
        PointerToRawData = Radar.ut32(b, offs + 20);
        PointerToRelocations = Radar.ut32(b, offs + 24);
        PointerToLinenumbers = Radar.ut32(b, offs + 28);
        NumberOfRelocations = Radar.ut16(b, offs + 32);
        NumberOfLinenumbers = Radar.ut16(b, offs + 34);
        Characteristics = Radar.ut32(b, offs + 36);
    }
}
