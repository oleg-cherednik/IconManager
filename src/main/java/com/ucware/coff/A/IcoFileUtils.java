package com.ucware.coff.A;

import com.ucware.coff.FileRandomAccess;
import com.ucware.coff.Header;
import com.ucware.coff.RandomAccessData;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public final class IcoFileUtils {
    public static IconFile read(File file) throws IOException {
        IconFile iconFile;
        FileRandomAccess in = null;

        try {
            in = new FileRandomAccess(new RandomAccessFile(file, "r"));
            iconFile = read(in);
        } catch(Throwable e) {
            throw e;
        } finally {
            if (in != null)
                in.delegate.close();
        }

        return iconFile;
    }

    public static IconFile read(RandomAccessData in) throws IOException {
        IconFileHeader header = Header.read(in, new IconFileHeader());

        if (header.getField("idReserved").readLong() != 0)
            throw new IOException("Not an icon");
        if (header.getField("idType").readLong() != 1)
            throw new IllegalArgumentException("Not an icon");

        IconFile iconFile = new IconFile(header);

        for (int i = 0; i < header.getImageCount(); ++i) {
            ImageKey key = Header.read(in, new ImageKey());
            int size = key.getField("dwBytesInRes").readInt();
            iconFile.addImage(new IconFileImage(key, new byte[size]));
        }
        for (int j = 0; j < header.getImageCount(); ++j) {
            IconFileImage iconFileImage = iconFile.getImage(j);
            ImageKey key = iconFileImage.getKey();
            long offs = key.getField("dwImageOffset").readLong();

            in.seek(offs);
            byte[] data = iconFileImage.getData();

            if (in.read(data) != data.length)
                throw new IOException("Not all bytes read");
        }
        return iconFile;
    }

    public static void main(String... args) {
        try {
            read(new File(args[0]));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private IcoFileUtils() {
    }
}
