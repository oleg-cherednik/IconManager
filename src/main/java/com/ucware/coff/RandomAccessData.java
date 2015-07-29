package com.ucware.coff;

import java.io.IOException;

public interface RandomAccessData {
    void seek(long var1) throws IOException;

    long size() throws IOException;

    int read() throws IOException;

    int read(byte... buf) throws IOException;
}
