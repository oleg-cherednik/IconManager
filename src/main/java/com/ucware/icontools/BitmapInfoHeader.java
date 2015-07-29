package com.ucware.icontools;

import com.ucware.coff.Header;
import com.ucware.coff.Field;

public class BitmapInfoHeader extends Header {
    public BitmapInfoHeader() {
        super(new Field("biSize", 4),
                new Field("biWidth", 4),
                new Field("biHeight", 4),
                new Field("biPlanes", 2),
                new Field("biBitCount", 2),
                new Field("biCompression", 4),
                new Field("biSizeImage", 4),
                new Field("biXPelsPerMeter", 4),
                new Field("biYPelsPerMeter", 4),
                new Field("biClrUsed", 4),
                new Field("biClrImportant", 4));
    }

    public int getWidth() {
        return getField("biWidth").readInt();
    }

    public int getHeight() {
        return getField("biHeight").readInt() / 2;
    }
}
