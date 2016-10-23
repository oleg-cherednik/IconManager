package cop.icoman.icl.radar.struct;

import cop.icoman.icl.radar.Radar;

/**
 * @author Oleg Cherednik
 * @since 14.10.2016
 */
public class Pe64_image_nt_headers {
    public static final int SIZE = 4 + Pe64_image_file_header.SIZE + Pe64_image_optional_header.SIZE;
    public long /*ut32*/ Signature;
    public final Pe64_image_file_header file_header = new Pe64_image_file_header();
    public final Pe64_image_optional_header optional_header = new Pe64_image_optional_header();

    public void read(byte[] b, int offs) {
        Signature = Radar.ut32(b, offs);
        file_header.read(b, offs + 4);
        optional_header.read(b, offs + 4 + Pe64_image_file_header.SIZE);
    }

}
