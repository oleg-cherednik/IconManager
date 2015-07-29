package com.ucware.icontools;

import com.ucware.coff.Field;
import com.ucware.coff.Header;

public class A extends Header {
    public A() {
        super(new Field("rgbBlue", 1),
                new Field("rgbGreen", 1),
                new Field("rgbRed", 1),
                new Field("rgbReserved", 1));
    }
}
