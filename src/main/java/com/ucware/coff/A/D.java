package com.ucware.coff.A;

import com.ucware.coff.Field;
import com.ucware.coff.Header;

public class D extends Header {
    public D() {
        super(new Field("bWidth", 1),
                new Field("bHeight", 1),
                new Field("bColorCount", 1),
                new Field("bReserved", 1),
                new Field("wPlanes", 2),
                new Field("wBitCount", 2),
                new Field("dwBytesInRes", 4),
                new Field("dwImageOffset", 4));
    }
}
