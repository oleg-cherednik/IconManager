package cop.icoman.icl;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 03.10.2016
 */
final class PeHeader {
    private final FileHeader fileHeader;
    private final OptionalHeader optionalHeader;

    public PeHeader(FileHeader fileHeader, OptionalHeader optionalHeader) {
        this.fileHeader = fileHeader;
        this.optionalHeader = optionalHeader;
    }

    public FileHeader getFileHeader() {
        return fileHeader;
    }

    public OptionalHeader getOptionalHeader() {
        return optionalHeader;
    }

    // ========== static ==========

    public static PeHeader read(ImageInputStream in) throws IOException {
        checkPortableExecutableSignature(in);
        return new PeHeader(new FileHeader(in), new OptionalHeader(in));
    }

    private static void checkPortableExecutableSignature(ImageInputStream in) throws IOException {
        long signature = in.readUnsignedInt();
        // TODO check Signature 0x50450000 - IMAGE_NT_SIGNATURE - PE
    }

}
