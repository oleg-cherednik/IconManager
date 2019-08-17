package com.cop.icoman.icl;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 08.10.2016
 */
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ResourceDirectory {
    private static final ResourceDirectory INSTANCE = new ResourceDirectory();

    private long characteristics;
    private long timeDateStamp;
    private int majorVersion;
    private int minorVersion;
    private int numberOfNamedEntries;
    private int numberOfIdEntries;

    public static ResourceDirectory read(ImageInputStream in) throws IOException {
        INSTANCE.characteristics = in.readUnsignedInt();
        INSTANCE.timeDateStamp = in.readUnsignedInt();
        INSTANCE.majorVersion = in.readUnsignedShort();
        INSTANCE.minorVersion = in.readUnsignedShort();
        INSTANCE.numberOfNamedEntries = in.readUnsignedShort();
        INSTANCE.numberOfIdEntries = in.readUnsignedShort();
        return INSTANCE;
    }
}
