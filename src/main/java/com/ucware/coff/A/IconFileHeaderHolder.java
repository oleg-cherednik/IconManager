package com.ucware.coff.A;

import java.util.ArrayList;
import java.util.List;

public class IconFileHeaderHolder {
    private final IconFileHeader B;
    private final List<E> A;

    public IconFileHeaderHolder(IconFileHeader iconFileHeader) {
        B = iconFileHeader;
        A = new ArrayList<>(iconFileHeader.getImageCount());
    }

    public IconFileHeader B() {
        return B;
    }

    public E get(int n) {
        return A.get(n);
    }

    public boolean add(E e) {
        return A.add(e);
    }

    public int size() {
        return A.size();
    }
}
