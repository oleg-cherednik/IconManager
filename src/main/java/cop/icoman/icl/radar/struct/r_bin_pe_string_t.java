package cop.icoman.icl.radar.struct;

import cop.icoman.icl.radar.Constant;

/**
 * @author Oleg Cherednik
 * @since 14.10.2016
 */
public class r_bin_pe_string_t {
    public final char[] string = new char[Constant.PE_STRING_LENGTH];
    public long /*ut64*/ vaddr;
    public long /*ut64*/ paddr;
    public long /*ut64*/ size;
    public char type;
    public int last;
}
