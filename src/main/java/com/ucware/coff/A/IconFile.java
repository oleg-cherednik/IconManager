package com.ucware.coff.A;

import java.util.ArrayList;
import java.util.List;

public class IconFile {
    private final IconFileHeader header;
    private final List<IconFileImage> images;

    public IconFile(IconFileHeader header) {
        this.header = header;
        images = new ArrayList<>(header.getImageCount());
    }

    public IconFileImage getImage(int pos) {
        return images.get(pos);
    }

    public boolean addImage(IconFileImage image) {
        return images.add(image);
    }

    public int size() {
        return images.size();
    }
}
