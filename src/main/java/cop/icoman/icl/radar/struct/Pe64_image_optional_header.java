package cop.icoman.icl.radar.struct;

import cop.icoman.icl.radar.Constant;
import cop.icoman.icl.radar.Radar;

/**
 * @author Oleg Cherednik
 * @since 14.10.2016
 */
public class Pe64_image_optional_header {
    public static final int SIZE = 96 + Pe64_image_data_directory.SIZE * Constant.PE_IMAGE_DIRECTORY_ENTRIES;
    /* Standard fields */
    public int /*ut16*/ Magic;
    public int /*ut8*/  MajorLinkerVersion;
    public int /*ut8*/  MinorLinkerVersion;
    public long /*ut32*/ SizeOfCode;
    public long /*ut32*/ SizeOfInitializedData;
    public long /*ut32*/ SizeOfUninitializedData;
    public long /*ut32*/ AddressOfEntryPoint;
    public long /*ut32*/ BaseOfCode;
    /* NT additional fields */
    public long /*ut64*/ ImageBase;
    public long /*ut32*/ SectionAlignment;
    public long /*ut32*/ FileAlignment;
    public int /*ut16*/ MajorOperatingSystemVersion;
    public int /*ut16*/ MinorOperatingSystemVersion;
    public int /*ut16*/ MajorImageVersion;
    public int /*ut16*/ MinorImageVersion;
    public int /*ut16*/ MajorSubsystemVersion;
    public int /*ut16*/ MinorSubsystemVersion;
    public long /*ut32*/ Win32VersionValue;
    public long /*ut32*/ SizeOfImage;
    public long /*ut32*/ SizeOfHeaders;
    public long /*ut32*/ CheckSum;
    public int /*ut16*/ Subsystem;
    public int /*ut16*/ DllCharacteristics;
    public long /*ut64*/ SizeOfStackReserve;
    public long /*ut64*/ SizeOfStackCommit;
    public long /*ut64*/ SizeOfHeapReserve;
    public long /*ut64*/ SizeOfHeapCommit;
    public long /*ut32*/ LoaderFlags;
    public long /*ut32*/ NumberOfRvaAndSizes;
    public final Pe64_image_data_directory[] DataDirectory = new Pe64_image_data_directory[Constant.PE_IMAGE_DIRECTORY_ENTRIES];

    public void read(byte[] b, int offs) {
        Magic = Radar.ut16(b, offs);
        MajorLinkerVersion = Radar.ut8(b, offs + 2);
        MinorLinkerVersion = Radar.ut8(b, offs + 3);
        SizeOfCode = Radar.ut32(b, offs + 4);
        SizeOfInitializedData = Radar.ut32(b, offs + 8);
        SizeOfUninitializedData = Radar.ut32(b, offs + 12);
        AddressOfEntryPoint = Radar.ut32(b, offs + 16);
        BaseOfCode = Radar.ut32(b, offs + 20);

        ImageBase = Radar.ut64(b, offs + 28);
        SectionAlignment = Radar.ut32(b, offs + 32);
        FileAlignment = Radar.ut32(b, offs + 36);
        MajorOperatingSystemVersion = Radar.ut16(b, offs + 38);
        MinorOperatingSystemVersion = Radar.ut16(b, offs + 40);
        MajorImageVersion = Radar.ut16(b, offs + 42);
        MinorImageVersion = Radar.ut16(b, offs + 44);
        MajorSubsystemVersion = Radar.ut16(b, offs + 46);
        MinorSubsystemVersion = Radar.ut16(b, offs + 48);
        Win32VersionValue = Radar.ut32(b, offs + 52);
        SizeOfImage = Radar.ut32(b, offs + 56);
        SizeOfHeaders = Radar.ut32(b, offs + 60);
        CheckSum = Radar.ut32(b, offs + 64);
        Subsystem = Radar.ut16(b, offs + 68);
        DllCharacteristics = Radar.ut16(b, offs + 70);
        SizeOfStackReserve = Radar.ut64(b, offs + 72);
        SizeOfStackCommit = Radar.ut64(b, offs + 76);
        SizeOfHeapReserve = Radar.ut64(b, offs + 80);
        SizeOfHeapCommit = Radar.ut64(b, offs + 84);
        LoaderFlags = Radar.ut32(b, offs + 88);
        NumberOfRvaAndSizes = Radar.ut64(b, offs + 92);

        offs += 96;

        for (int i = 0; i < DataDirectory.length; i++, offs += Pe64_image_data_directory.SIZE) {
            DataDirectory[i] = new Pe64_image_data_directory();
            DataDirectory[i].read(b, offs);
        }

    }
}
