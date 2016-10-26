package cop.icoman.icl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 08.10.2016
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ResourceDirectory {
    private static long characteristics;
    private static long timeDateStamp;
    private static int majorVersion;
    private static int minorVersion;
    @Getter
    private static int numberOfNamedEntries;
    @Getter
    private static int numberOfIdEntries;

    public static void read(ImageInputStream in) throws IOException {
        characteristics = in.readUnsignedInt();
        timeDateStamp = in.readUnsignedInt();
        majorVersion = in.readUnsignedShort();
        minorVersion = in.readUnsignedShort();
        numberOfNamedEntries = in.readUnsignedShort();
        numberOfIdEntries = in.readUnsignedShort();
    }
}
