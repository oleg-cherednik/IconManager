package com.ucware.icontools;

public class Bitmap {
    private final BitmapInfoHeader header;
    private final A[] data;

    public Bitmap(BitmapInfoHeader header, A... data) {
        this.header = header;
        this.data = data;
    }

    public BitmapInfoHeader getHeader() {
        return header;
    }

    public A[] getData() {
        return data;
    }
}
