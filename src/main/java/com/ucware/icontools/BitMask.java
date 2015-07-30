package com.ucware.icontools;

import com.ucware.coff.Field;
import com.ucware.coff.Header;

public class BitMask extends Header {
    public BitMask() {
        super(new Field("rgbBlue", 1),
                new Field("rgbGreen", 1),
                new Field("rgbRed", 1),
                new Field("rgbReserved", 1));
    }
}
