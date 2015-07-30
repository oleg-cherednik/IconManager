package com.ucware.coff.A;

public class IconFileImage {
    private final ImageKey key;
    private final byte[] data;

    public IconFileImage(ImageKey key, byte... data) {
        this.key = key;
        this.data = data;
    }

    public ImageKey getKey() {
        return key;
    }

    public byte[] getData() {
        return data;
    }
}
