package com.ucware.icontools;

import com.ucware.coff.A.IcoFileUtils;
import com.ucware.coff.A.E;
import com.ucware.coff.A.IconFileHeaderHolder;
import com.ucware.coff.ByteArrayRandomAccess;
import com.ucware.coff.RandomAccessData;

import javax.swing.Icon;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class IconTools {
    protected static Icon getIcon(File file, int n) throws IOException {
        Icon[] arricon = readIcons(file);
        if (arricon.length == 0) {
            return null;
        }
        if (n < 0) {
            n = 0;
        } else if (n > arricon.length - 1) {
            n = arricon.length - 1;
        }
        return arricon[n];
    }

    protected static Icon getIcon(E e) {
        return new IconImage(e);
    }

    public static Icon[] readIcons(File file) throws IOException {
        return getIcons(IcoFileUtils.readHeader(file));
    }

    public static Icon[] readIcons(RandomAccessData randomAccessData) throws IOException {
        return getIcons(IcoFileUtils.readHeader(randomAccessData));
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

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static Icon[] readIcons(URL uRL) throws IOException {
        Icon[] arricon;
        InputStream inputStream = null;
        try {
            inputStream = uRL.openStream();
            arricon = readIcons(inputStream);
            Object var4_3 = null;
            if (inputStream == null) return arricon;
        } catch(Throwable var3_5) {
            Object var4_4 = null;
            if (inputStream == null) throw var3_5;
            inputStream.close();
            throw var3_5;
        }
        inputStream.close();
        return arricon;
    }

    protected static Icon[] getIcons(IconFileHeaderHolder g) {
        Icon[] icons = new Icon[g.size()];

        for (int i = 0; i < g.size(); ++i)
            icons[i] = new IconImage(g.get(i));

        return icons;
    }
}
