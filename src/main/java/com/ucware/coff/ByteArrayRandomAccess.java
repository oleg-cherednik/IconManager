package com.ucware.coff;

import java.io.IOException;

public class ByteArrayRandomAccess implements RandomAccessData {
    private final byte[] buf;
    private int pos;

    public ByteArrayRandomAccess(byte... buf) {
        this.buf = buf;
        pos = 0;
    }

    public void seek(long l) throws IOException {
        if (l < 0 || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Illegal seek position: " + l);
        }
        pos = (int)l;
    }

    public long size() throws IOException {
        return buf.length;
    }

    public int read() throws IOException {
        if (pos < buf.length) {
            return buf[pos++] & 255;
        }
        return -1;
    }

    public int read(byte... buf) throws IOException {
        if (pos >= this.buf.length) {
            return -1;
        }
        if (buf != null) {
            int n = 0;
            for (int i = 0; i < buf.length && pos < this.buf.length; ++i) {
                buf[i] = this.buf[pos++];
                ++n;
            }
            return n;
        }
        throw new IllegalArgumentException("b:" + buf);
    }
}
