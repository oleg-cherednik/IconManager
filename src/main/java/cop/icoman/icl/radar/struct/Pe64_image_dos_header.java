package cop.icoman.icl.radar.struct;

import cop.icoman.icl.radar.Radar;

/**
 * @author Oleg Cherednik
 * @since 14.10.2016
 */
public class Pe64_image_dos_header {
    public static final int SIZE = 64;

    public int /*ut16*/ e_magic;      /* 00: MZ Header signature */
    public int /*ut16*/ e_cblp;       /* 02: Bytes on last page of file */
    public int /*ut16*/ e_cp;         /* 04: Pages in file */
    public int /*ut16*/ e_crlc;       /* 06: Relocations */
    public int /*ut16*/ e_cparhdr;    /* 08: Size of header in paragraphs */
    public int /*ut16*/ e_minalloc;   /* 0a: Minimum extra paragraphs needed */
    public int /*ut16*/ e_maxalloc;   /* 0c: Maximum extra paragraphs needed */
    public int /*ut16*/ e_ss;         /* 0e: Initial (relative) SS value */
    public int /*ut16*/ e_sp;         /* 10: Initial SP value */
    public int /*ut16*/ e_csum;       /* 12: Checksum */
    public int /*ut16*/ e_ip;         /* 14: Initial IP value */
    public int /*ut16*/ e_cs;         /* 16: Initial (relative) CS value */
    public int /*ut16*/ e_lfarlc;     /* 18: File address of relocation table */
    public int /*ut16*/ e_ovno;       /* 1a: Overlay number */
    public int[] /*ut16*/ e_res/*[4]*/;     /* 1c: Reserved words */
    public int /*ut16*/ e_oemid;      /* 24: OEM identifier (for e_oeminfo) */
    public int /*ut16*/ e_oeminfo;    /* 26: OEM information; e_oemid specific */
    public int[] /*ut16*/ e_res2/*[10]*/;   /* 28: Reserved words */
    public long /*ut32*/ e_lfanew;     /* 3c: Offset to extended header */

    public void read(byte[] b, int offs) {
        e_magic = Radar.ut16(b, offs);
        e_cblp = Radar.ut16(b, offs + 2);
        e_cp = Radar.ut16(b, offs + 4);
        e_crlc = Radar.ut16(b, offs + 6);
        e_cparhdr = Radar.ut16(b, offs + 8);
        e_minalloc = Radar.ut16(b, offs + 10);
        e_maxalloc = Radar.ut16(b, offs + 12);
        e_ss = Radar.ut16(b, offs + 14);
        e_sp = Radar.ut16(b, offs + 16);
        e_csum = Radar.ut16(b, offs + 18);
        e_ip = Radar.ut16(b, offs + 20);
        e_cs = Radar.ut16(b, offs + 22);
        e_lfarlc = Radar.ut16(b, offs + 24);
        e_ovno = Radar.ut16(b, offs + 26);
        e_res = Radar.ut16(b, offs + 28, 4);
        e_oemid = Radar.ut16(b, offs + 36);
        e_oeminfo = Radar.ut16(b, offs + 38);
        e_res2 = Radar.ut16(b, offs + 40, 10);
        e_lfanew = Radar.ut32(b, offs + 60);
    }

}
