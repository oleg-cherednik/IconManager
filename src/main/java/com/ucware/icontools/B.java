package com.ucware.icontools;

import com.ucware.coff.A.E;
import com.ucware.coff.Header;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class B {
    private Bitmap D;
    private Bitmap B;
    private byte[] A;
    private byte[] C;

    public B(E e) {
        this(e.A());
    }

    public B(byte[] buf) {
        try (ByteArrayInputStream in = new ByteArrayInputStream(buf)) {
            BitmapInfoHeader header = Header.read(in, new BitmapInfoHeader());
            int bitCount = header.getField("biBitCount").readInt();
            int size = bitCount <= 8 ? (int)Math.pow(2.0, bitCount) : 0;
            A[] arra = new A[size];
            for (int i = 0; i < arra.length; ++i) {
                arra[i] = Header.read(in, new A());
            }
            D = new Bitmap(header, arra);
            int n3 = (header.getWidth() * header.getField("biBitCount").readInt() + 31) / 32 * 4 * header.getHeight();
            int n4 = (header.getWidth() + 31) / 32 * 4 * header.getHeight();
            A = new byte[n3];
            C = new byte[n4];
            in.read(A);
            in.read(C);
            header = new BitmapInfoHeader();
            Header.A(D.getHeader(), header, "biSize");
            Header.A(D.getHeader(), header, "biWidth");
            Header.A(D.getHeader(), header, "biHeight");
            Header.A(D.getHeader(), header, "biBitCount");
            header.getField("biPlanes").A(1);
            header.getField("biSizeImage").A((header.getWidth() + 31) / 32 * 4 * header.getHeight());
            arra = new A[] { new A(), new A() };
            arra[1].getField("rgbBlue").A(255);
            arra[1].getField("rgbGreen").A(255);
            arra[1].getField("rgbRed").A(255);
            B = new Bitmap(header, arra);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap D() {
        return D;
    }

    public Bitmap A() {
        return B;
    }

    public byte[] B() {
        return A;
    }

    public byte[] C() {
        return C;
    }
}
