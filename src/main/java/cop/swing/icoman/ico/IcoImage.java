package cop.swing.icoman.ico;

import cop.swing.icoman.IconImageHeader;
import cop.swing.icoman.exceptions.IconManagerException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 03.07.2013
 */
public final class IcoImage {
    private final IconImageHeader header;
    private final ImageIcon icon;

    public static IcoImage createImage(IconImageHeader header, byte... data) throws IconManagerException, IOException {
        check(header, data);
        return new IcoImage(header, data);
    }

    private IcoImage(IconImageHeader header, byte... data) throws IOException {
        this.header = header;
        icon = new ImageIcon(ImageIO.read(new ByteArrayInputStream(data)));
    }

    public IconImageHeader getHeader() {
        return header;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    // ========== Object ==========

    @Override
    public String toString() {
        return header.toString();
    }

    // ========== static ==========

    private static void check(IconImageHeader header, byte... data) throws IconManagerException {
        if (header == null)
            throw new IconManagerException("header is not set");
        if (data == null || data.length == 0)
            throw new IconManagerException("data is not set");
        if (header.getSize() != data.length)
            throw new IconManagerException("data size is not equals to 'header.size'");
    }
}
