package cop.icoman.icl.radar.struct;

import cop.icoman.icl.radar.Radar;

/**
 * @author Oleg Cherednik
 * @since 14.10.2016
 */
public class Pe_image_resource_directory {
    public static final int SIZE = 16;

    public long /*ut32*/ Characteristics;
    public long /*ut32*/ TimeDateStamp;
    public int /*ut16*/ MajorVersion;
    public int /*ut16*/ MinorVersion;
    public int /*ut16*/ NumberOfNamedEntries;
    public int /*ut16*/ NumberOfIdEntries;

    public void read(byte[] b, int offs) {
        Characteristics = Radar.ut32(b, offs);
        TimeDateStamp = Radar.ut32(b, offs + 4);
        MajorVersion = Radar.ut16(b, offs + 8);
        MinorVersion = Radar.ut16(b, offs + 10);
        NumberOfNamedEntries = Radar.ut16(b, offs + 12);
        NumberOfIdEntries = Radar.ut16(b, offs + 14);
    }
}
