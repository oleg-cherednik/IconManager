package ru.olegcherednik.icoman.icl;

import lombok.Data;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @see <a href="https://msdn.microsoft.com/ru-ru/library/windows/desktop/ms680336(v=vs.85).aspx">IMAGE_NT_HEADER structure</a>
 * @since 03.10.2016
 */
@Data
final class NtHeader {
    private final FileHeader fileHeader;
    private final OptionalHeader optionalHeader;

    public NtHeader(ImageInputStream in) throws IOException {
        checkPortableExecutableSignature(in);
        fileHeader = new FileHeader(in);
        optionalHeader = new OptionalHeader(in);
    }

    // ========== static ==========

    private static void checkPortableExecutableSignature(ImageInputStream in) throws IOException {
        long signature = in.readUnsignedInt();
        // TODO check Signature 0x50450000 - IMAGE_NT_SIGNATURE - PE
    }

}
