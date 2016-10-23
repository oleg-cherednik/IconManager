package cop.icoman.icl.radar.struct;

import cop.icoman.icl.radar.Radar;

/**
 * @author Oleg Cherednik
 * @since 14.10.2016
 */
public class Pe64_image_data_directory {
    public static final int SIZE = 8;

    public long /*ut32*/ VirtualAddress;
    public long /*ut32*/ Size;

    public void read(byte[] b, int offs) {
        VirtualAddress = Radar.ut32(b, offs);
        Size = Radar.ut32(b, offs + 4);
    }
}
