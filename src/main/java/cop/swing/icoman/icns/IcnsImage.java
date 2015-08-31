package cop.swing.icoman.icns;

import cop.swing.icoman.IconImage;
import cop.swing.icoman.ImageKey;

import javax.swing.ImageIcon;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 16.08.2015
 */
public final class IcnsImage implements IconImage {
    private final ImageKey key;
    private final Type type;
    private int[] data;
    private int[] mask;
    public ImageIcon icon;

    public IcnsImage(ImageKey key, Type type) {
        this.key = key;
        this.type = type;
    }

    public void setData(int[] data) {
        this.data = data;
    }

    public void setMask(int[] mask) {
        this.mask = mask;
    }

    public void createIcon() throws IOException {
        try {
            icon = new ImageIcon(type.createImage(key, data, mask));
//            if (data != null) {
//                BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));
//                if (image != null)
//                    icon = new ImageIcon(image);
//            } else if (mask != null) {
//                BufferedImage image = ImageIO.read(new ByteArrayInputStream(mask));
//                if (image != null)
//                    icon = new ImageIcon(image);
//            }
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
