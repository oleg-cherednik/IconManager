package com.ucware.coff.A;

import com.ucware.coff.Field;
import com.ucware.coff.Header;

public class IconFileHeader extends Header {
    public IconFileHeader() {
        super(new Field("idReserved", 2),
                new Field("idType", 2),
                new Field("idCount", 2));
    }

    public int getImageCount() {
        return getField("idCount").readInt();
    }
}
