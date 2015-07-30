package com.ucware.icontools;

public class Bitmap {
    private final BitmapInfoHeader bitmapInfoHeader;
    private final BitMask[] data;

    public Bitmap(BitmapInfoHeader bitmapInfoHeader, BitMask... data) {
        this.bitmapInfoHeader = bitmapInfoHeader;
        this.data = data;
    }

    public BitmapInfoHeader getBitmapInfoHeader() {
        return bitmapInfoHeader;
    }

    public BitMask[] getData() {
        return data;
    }
}
