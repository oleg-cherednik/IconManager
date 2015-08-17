package cop.swing.icoman.icns;

import cop.swing.icoman.IconImage;
import cop.swing.icoman.ImageKey;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 16.08.2015
 */
public final class IcnsImage implements IconImage {
    private final ImageKey key;
    private final Type type;
    private byte[] data;
    private byte[] mask;
    private ImageIcon icon;

    public IcnsImage(ImageKey key, Type type) {
        this.key = key;
        this.type = type;
    }

    public void setData(byte... data) {
        this.data = data;
    }

    public void setMask(byte... mask) {
        this.mask = mask;
    }

    public void createIcon() throws IOException {
        try {
            if (data != null) {
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));
                if (image != null)
                    icon = new ImageIcon(image);
            } else if (mask != null) {
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(mask));
                if (image != null)
                    icon = new ImageIcon(image);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // ========== IconImage ==========

    @Override
    public ImageIcon getIcon() {
        return icon;
    }
}
