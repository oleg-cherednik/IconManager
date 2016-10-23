package cop.icoman.icl.radar.struct;

/**
 * @author Oleg Cherednik
 * @since 14.10.2016
 */
public class r_bin_pe_section_t {
    public int[] name; // ut8 (Constant.PE_IMAGE_SIZEOF_SHORT_NAME * 2)
    public long size;  // ut64
    public long vsize; // ut64
    public long vaddr; // ut64
    public long paddr; // ut64
    public long flags; // ut64
    public int last;
}
