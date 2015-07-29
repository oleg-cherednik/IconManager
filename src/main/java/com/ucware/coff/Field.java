package com.ucware.coff;

public class Field implements A1 {
    public static final int F = 0;
    public static final int A = 1;
    public static final int D = 2;
    public static final int G = 3;
    public final String name;
    public final int size;
    public final int B;
    private byte[] buf;

    public Field(String name, int size) {
        this(name, size, 0);
    }

    public Field(String name, int size, int n2) {
        this.name = name;
        this.size = size;
        B = n2;
        buf = new byte[size];
    }

    public byte[] getData() {
        return buf;
    }

    public void setData(byte... buf) {
        if (buf.length != size) {
            throw new IllegalArgumentException("rawData.length must be equal to sizeInBytes");
        }
        this.buf = buf;
    }

    public long readLong() {
        return read(buf, size);
    }

    public int readInt() {
        return (int)readLong();
    }

    public static long read(byte[] buf, int n) {
        long l = 0;
        for (int i = 0; i < n; ++i) {
            l |= ((long)buf[i] & 255) << i * 8;
        }
        return l;
    }

    public void A(int n) {
        setData(A(n, size));
    }

    public void A(long l) {
        setData(A(l, size));
    }

    public static byte[] A(long l, int n) {
        byte[] arrby = new byte[n];
        for (int i = 0; i < n; ++i) {
            arrby[i] = (byte)(l >>> i * 8 & 255);
        }
        return arrby;
    }

    public int A() {
        return size;
    }

    @Override
    public String toString() {
        return String.format("%s:%d", name, readLong());
    }
}
