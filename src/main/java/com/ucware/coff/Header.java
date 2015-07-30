package com.ucware.coff;

import org.apache.commons.lang3.ArrayUtils;

import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Header implements A1 {
    protected Field[] fields;

    public Header() {
        this(new Field[0]);
    }

    public Header(Field... fields) {
        this.fields = fields;
    }

    public Field getField(String fieldName) throws FieldNotFoundException {
        if (fields != null)
            for (Field field : fields)
                if (field.name.equals(fieldName))
                    return field;
        throw new FieldNotFoundException("Field not found: " + fieldName);
    }

    public int A() {
        return G();
    }

    private int G() {
        int n = 0;
        for (int i = 0; i < fields.length; ++i) {
            Field field = fields[i];
            n += field.size;
        }
        return n;
    }

    public byte[] F() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (int i = 0; i < fields.length; ++i) {
            Field field = fields[i];
            byte[] arrby = field.getData();
            byteArrayOutputStream.write(arrby);
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static void A(Header e, Header e2, String string) {
        e2.getField(string).setData(e.getField(string).getData());
    }

    @Override
    public String toString() {
        if (ArrayUtils.isEmpty(fields))
            return "<empty>";

        StringBuilder buf = new StringBuilder();

        for (Field field : fields) {
            if (buf.length() > 0)
                buf.append(',');
            buf.append(field);
        }

        return buf.toString();
    }

    public static <T extends Header> T read(RandomAccessData in, T header) throws IOException {
        Field[] fields = header.fields;
        for (int i = 0; i < fields.length; ++i) {
            Field field = fields[i];
            byte[] buf = new byte[field.size];
            in.read(buf);
            field.setData(buf);
        }
        return header;
    }

    public static Header read(byte[] buf, Header header) throws IOException {
        return read(new ByteArrayInputStream(buf), header);
    }

    public static <T extends Header>T read(ByteArrayInputStream in, T header) throws IOException {
        for (Field field : header.fields) {
            byte[] buf = new byte[field.size];
            int n = in.read(buf);
            field.setData(buf);

            if (field.size == n)
                continue;

            throw new IOException("Not all bytes read");
        }

        return header;
    }

    public static <T extends Header>T read(ImageInputStream in, T header) throws IOException {
        for (Field field : header.fields) {
            byte[] buf = new byte[field.size];
            int n = in.read(buf);
            field.setData(buf);

            if (field.size == n)
                continue;

            throw new IOException("Not all bytes read");
        }

        return header;
    }

    public static String A(RandomAccessData in) throws IOException {
        StringBuilder buf = new StringBuilder();
        int n = in.read();
        while (n > 0) {
            buf.append((char)n);
            n = in.read();
        }
        return buf.toString();
    }

    public static long A(RandomAccessData randomAccessData, int n) throws IOException {
        byte[] buf = new byte[n];
        if (randomAccessData.read(buf) != n) {
            throw new IOException("Not all bytes read");
        }
        return Field.read(buf, n);
    }
}
