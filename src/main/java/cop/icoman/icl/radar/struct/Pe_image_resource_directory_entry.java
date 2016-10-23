package cop.icoman.icl.radar.struct;

import cop.icoman.icl.radar.Radar;

/**
 * @author Oleg Cherednik
 * @since 14.10.2016
 */
public class Pe_image_resource_directory_entry {
    public static final int SIZE = U1.SIZE + U2.SIZE;
    public final U1 u1 = new U1();
    public final U2 u2 = new U2();

    public static class U1 {
        public static final int SIZE = S.SIZE;

        public final S s = new S();
        public long /*ut32*/ Name;
        public int /*ut16*/ Id;

        public static class S {
            public static final int SIZE = 8;

            public long /*ut32*/ NameOffset;    // 31 bit;
            public boolean /*ut32*/ NameIsString;   // 1 bit;
        }
    }

    public static class U2 {
        public static final int SIZE = S.SIZE;

        public long /*ut32*/ OffsetToData;
        public final S s = new S();

        public static class S {
            public static final int SIZE = 8;

            public long /*ut32*/ OffsetToDirectory; // 31 bits;
            public boolean /*ut32*/ DataIsDirectory;    // 1 it;
        }
    }

    public void read(byte[] b, int offs) {
        long nameId = Radar.ut32(b, offs);
        long dataPtr = Radar.ut32(b, offs + 4);

        u1.s.NameOffset = nameId;
        u1.s.NameIsString = nameId < 0;
        u1.Name = nameId;
        u1.Id = (int)(nameId & 0xFF);

        u2.OffsetToData = dataPtr;
        u2.s.OffsetToDirectory = dataPtr;
        u2.s.DataIsDirectory = dataPtr < 0;
    }
}
