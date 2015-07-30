package com.ucware.icontools;

import com.ucware.coff.A.IcoFileUtils;
import com.ucware.coff.A.IconFile;
import com.ucware.coff.ByteArrayRandomAccess;
import com.ucware.coff.RandomAccessData;

import javax.swing.Icon;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class IconTools {
    public static Icon[] readIcons(File file) throws IOException {
        return getIcons(IcoFileUtils.read(file));
    }

    public static Icon[] readIcons(RandomAccessData randomAccessData) throws IOException {
        return getIcons(IcoFileUtils.read(randomAccessData));
    }

    public static Icon[] readIcons(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] arrby = new byte[1024];
        int n = inputStream.read(arrby);
        while (n != -1) {
            byteArrayOutputStream.write(arrby, 0, n);
            n = inputStream.read(arrby);
        }
        ByteArrayRandomAccess byteArrayRandomAccess = new ByteArrayRandomAccess(byteArrayOutputStream.toByteArray());
        return readIcons(byteArrayRandomAccess);
    }

    protected static Icon[] getIcons(IconFile iconFile) {
        Icon[] icons = new Icon[iconFile.size()];

        for (int i = 0; i < iconFile.size(); ++i)
            icons[i] = new IconImage(iconFile.getImage(i));

        return icons;
    }
}
