package cop.swing.icoman.ico;

import cop.swing.icoman.IconImage;
import cop.swing.icoman.IconImageHeader;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 03.07.2013
 */
public final class IcoImage implements IconImage {
    private final IconImageHeader header;
    private final ImageIcon icon;

    public static IcoImage read(IconImageHeader header, ImageInputStream in) throws IOException {
        byte[] data = new byte[header.getSize()];
        in.readFully(data);
        return new IcoImage(header, new ImageIcon(ImageIO.read(new ByteArrayInputStream(data))));
    }

    private IcoImage(IconImageHeader header, ImageIcon icon) {
        this.header = header;
        this.icon = icon;
    }

    public IconImageHeader getHeader() {
        return header;
    }

    // ========== IconImage ==========

    @Override
    public ImageIcon getIcon() {
        return icon;
    }

    // ========== Object ==========

    @Override
    public String toString() {
        return header.toString();
    }
}
