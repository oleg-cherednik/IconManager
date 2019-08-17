package com.cop.icoman.icl;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 21.10.2016
 */
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ResourceDataEntry {
    private static final ResourceDataEntry INSTANCE = new ResourceDataEntry();

    private long rva;
    private int size;
    private long codePage;
    private long reserved;

    public static ResourceDataEntry read(ImageInputStream in) throws IOException {
        INSTANCE.rva = in.readUnsignedInt();
        INSTANCE.size = (int)in.readUnsignedInt();
        INSTANCE.codePage = in.readUnsignedInt();
        INSTANCE.reserved = in.readUnsignedInt();
        return INSTANCE;
    }
}
