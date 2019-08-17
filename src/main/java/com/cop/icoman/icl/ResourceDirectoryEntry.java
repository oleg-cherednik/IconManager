package com.cop.icoman.icl;

import lombok.Data;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 08.10.2016
 */
@Data
final class ResourceDirectoryEntry {
    public static final int SIZE = 8;

    private final boolean leaf;
    private final int id;
    private final long offsName;
    private final long offsData;

    public ResourceDirectoryEntry(ImageInputStream in, boolean idDec) throws IOException {
        int nameId = in.readInt();
        int dataPtr = in.readInt();

        leaf = dataPtr >= 0;
        id = nameId > 0 ? ((nameId & 0xFF) - (idDec ? 1 : 0)) : -1;
        offsName = nameId >= 0 ? -1 : (nameId & 0x7FFFFFFF);
        offsData = leaf ? dataPtr : (dataPtr & 0x7FFFFFFF);
    }
}
