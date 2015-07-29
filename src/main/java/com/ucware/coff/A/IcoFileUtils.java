package com.ucware.coff.A;

import com.ucware.coff.FileRandomAccess;
import com.ucware.coff.Header;
import com.ucware.coff.RandomAccessData;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class IcoFileUtils {
    public static E A(IconImageHeader iconImageHeader, byte[] buf) {
        D d = new D();
        Header.A(iconImageHeader, d, "bWidth");
        Header.A(iconImageHeader, d, "bHeight");
        Header.A(iconImageHeader, d, "bColorCount");
        Header.A(iconImageHeader, d, "bReserved");
        Header.A(iconImageHeader, d, "wPlanes");
        Header.A(iconImageHeader, d, "wBitCount");
        Header.A(iconImageHeader, d, "dwBytesInRes");
        return new E(d, buf);
    }

    public static IconFileHeaderHolder readHeader(File file) throws IOException {
        IconFileHeaderHolder header;
        FileRandomAccess in = new FileRandomAccess(new RandomAccessFile(file, "r"));

        try {
            header = readHeader(in);
        } catch(Throwable e) {
            in.delegate.close();
            throw e;
        }
        in.delegate.close();
        return header;
    }

    public static IconFileHeaderHolder readHeader(RandomAccessData in) throws IOException {
        IconFileHeader iconFileHeader = Header.read(in, new IconFileHeader());
        if (iconFileHeader.getField("idReserved").readLong() != 0) {
            throw new IOException("Not an icon");
        }
        if (iconFileHeader.getField("idType").readLong() != 1) {
            throw new IllegalArgumentException("Not an icon");
        }
        IconFileHeaderHolder g = new IconFileHeaderHolder(iconFileHeader);
        for (int i = 0; i < iconFileHeader.getImageCount(); ++i) {
            D d = Header.read(in, new D());
            g.add(new E(d, null));
        }
        for (int j = 0; j < iconFileHeader.getImageCount(); ++j) {
            E e = g.get(j);
            in.seek(e.B().getField("dwImageOffset").readLong());
            byte[] arrby = new byte[e.B().getField("dwBytesInRes").readInt()];
            if (in.read(arrby) != arrby.length) {
                throw new IOException("Not all bytes read");
            }
            g.get(j).A(arrby);
        }
        return g;
    }

    public static void main(String... args) {
        try {
            readHeader(new File(args[0]));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
