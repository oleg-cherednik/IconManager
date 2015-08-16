package cop.swing.icoman.icns;

import javax.swing.ImageIcon;

/**
 * @author Oleg Cherednik
 * @since 16.08.2015
 */
public final class IcnsImage {
    private byte[] data;
    private byte[] mask;
    private ImageIcon icon;

    public void setData(byte... data) {
        this.data = data;
    }

    public void setMask(byte... mask) {
        this.mask = mask;
    }

}
