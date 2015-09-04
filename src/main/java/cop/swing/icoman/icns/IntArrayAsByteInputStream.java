package cop.swing.icoman.icns;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Oleg Cherednik
 * @since 04.09.2015
 */
final class IntArrayAsByteInputStream extends InputStream {
    private int[] buf;
    private int pos;
    private int mark;
    private int count;

    public IntArrayAsByteInputStream(int[] buf) {
        this.buf = buf;
        count = buf.length;
    }

    public IntArrayAsByteInputStream(int[] buf, int offset, int length) {
        this.buf = buf;
        pos = offset;
        count = Math.min(offset + length, buf.length);
        mark = offset;
    }

    public synchronized int read() {
        return (pos < count) ? (buf[pos++] & 0xff) : -1;
    }

    public synchronized int read(byte b[], int off, int len) {
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }

        if (pos >= count) {
            return -1;
        }

        int avail = count - pos;
        if (len > avail) {
            len = avail;
        }
        if (len <= 0) {
            return 0;
        }

        for(int i = pos, j = off; i < pos + len; i++, j++)
            buf[i] = b[j];
        pos += len;
        return len;
    }

    public synchronized long skip(long n) {
        long k = count - pos;
        if (n < k) {
            k = n < 0 ? 0 : n;
        }

        pos += k;
        return k;
    }

    public synchronized int available() {
        return count - pos;
    }

    public boolean markSupported() {
        return true;
    }

    public void mark(int readAheadLimit) {
        mark = pos;
    }

    public synchronized void reset() {
        pos = mark;
    }

    public void close() throws IOException {
    }
}
