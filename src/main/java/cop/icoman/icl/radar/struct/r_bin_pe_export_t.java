package cop.icoman.icl.radar.struct;

/**
 * @author Oleg Cherednik
 * @since 14.10.2016
 */
public class r_bin_pe_export_t {
    public int[]  name;  // ut8 (Constant.PE_NAME_LENGTH + 1)
    public int[]  forwarder; // ut8 (Constant.PE_NAME_LENGTH + 1)
    public long vaddr;   // ut64
    public long paddr;   // ut64
    public long ordinal;   // ut64
    public int last;
}
