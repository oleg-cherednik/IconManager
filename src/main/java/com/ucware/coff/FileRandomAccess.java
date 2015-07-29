package com.ucware.coff;

import java.io.IOException;
import java.io.RandomAccessFile;

public class FileRandomAccess implements RandomAccessData {
    public RandomAccessFile delegate;

    public FileRandomAccess(RandomAccessFile delegate) {
        this.delegate = delegate;
    }

    @Override
    public int read() throws IOException {
        return delegate.read();
    }

    @Override
    public int read(byte... buf) throws IOException {
        return delegate.read(buf);
    }

    @Override
    public void seek(long l) throws IOException {
        delegate.seek(l);
    }

    @Override
    public long size() throws IOException {
        return delegate.length();
    }
}
