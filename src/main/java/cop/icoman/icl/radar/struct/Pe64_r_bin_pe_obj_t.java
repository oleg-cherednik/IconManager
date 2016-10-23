package cop.icoman.icl.radar.struct;

/**
 * @author Oleg Cherednik
 * @since 14.10.2016
 */
public class Pe64_r_bin_pe_obj_t {
    // these pointers contain a copy of the headers and sections!
    public final Pe64_image_dos_header dos_header = new Pe64_image_dos_header();
    public final Pe64_image_nt_headers nt_headers = new Pe64_image_nt_headers();
    public Pe64_image_section_header[] section_header;
    public Pe64_image_export_directory export_directory;
    public Pe64_image_import_directory import_directory;
    public Pe64_image_tls_directory tls_directory;
    public final Pe_image_resource_directory resource_directory = new Pe_image_resource_directory();
    public Pe64_image_delay_import_directory delay_import_directory;
    // these values define the real offset into the untouched binary
    public long /*ut64*/ nt_header_offset;
    public long /*ut64*/ import_directory_offset;
    public long /*ut64*/ export_directory_offset;
    public long /*ut64*/ resource_directory_offset;
    public long /*ut64*/ delay_import_directory_offset;

    public int import_directory_size;
    public int num_sections;
    public int endian;
//    RList *relocs;
    public byte[] b;
//    public Sdb *kv;

}
