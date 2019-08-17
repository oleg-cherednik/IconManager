package com.cop.icoman.imageio.bmp;

import lombok.Data;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/dd183376(v=vs.85).aspx">BITMAPINFOHEADER structure</a>
 * @since 01.09.2013
 */
@Data
@SuppressWarnings({ "InstanceVariableNamingConvention", "SpellCheckingInspection" })
final class BitmapInfoHeader {
    private final int biSize;
    private final int biWidth;
    private final int biHeight;
    private final int biPlanes;
    private final int biBitCount;
    private final int biCompression;
    private final int biSizeImage;
    private final int biXPelsPerMeter;
    private final int biYPelsPerMeter;
    private final int biColorsUsed;
    private final int biColorsImportant;

    public BitmapInfoHeader(ImageInputStream in) throws IOException {
        biSize = (int)in.readUnsignedInt();
        biWidth = in.readInt();
        biHeight = in.readInt() / 2;
        biPlanes = in.readUnsignedShort();
        biBitCount = in.readUnsignedShort();
        biCompression = (int)in.readUnsignedInt();
        biSizeImage = (int)in.readUnsignedInt();
        biXPelsPerMeter = in.readInt();
        biYPelsPerMeter = in.readInt();
        biColorsUsed = (int)in.readUnsignedInt();
        biColorsImportant = (int)in.readUnsignedInt();
    }
}
