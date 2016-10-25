package cop.icoman.imageio.bmp;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/dd183376(v=vs.85).aspx">BITMAPINFOHEADER structure</a>
 * @since 01.09.2013
 */
@SuppressWarnings({ "InstanceVariableNamingConvention", "SpellCheckingInspection" })
public final class BitmapInfoHeader {

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

    public int getBiSize() {
        return biSize;
    }

    public int getBiWidth() {
        return biWidth;
    }

    public int getBiHeight() {
        return biHeight;
    }

    public int getBiPlanes() {
        return biPlanes;
    }

    public int getBiBitCount() {
        return biBitCount;
    }

    public int getBiCompression() {
        return biCompression;
    }

    public int getBiSizeImage() {
        return biSizeImage;
    }

    public int getBiXPelsPerMeter() {
        return biXPelsPerMeter;
    }

    public int getBiYPelsPerMeter() {
        return biYPelsPerMeter;
    }

    public int getBiColorsUsed() {
        return biColorsUsed;
    }

    public int getBiColorsImportant() {
        return biColorsImportant;
    }
}
