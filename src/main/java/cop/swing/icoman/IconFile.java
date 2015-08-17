package cop.swing.icoman;

import cop.swing.icoman.exceptions.ImageNotFoundException;

import javax.swing.ImageIcon;
import java.io.IOException;
import java.util.Set;

/**
 * @author Oleg Cherednik
 * @since 15.08.2015
 */
public abstract class IconFile {
    public abstract Set<ImageKey> getKeys();

    public abstract IconImage getImage(ImageKey key) throws ImageNotFoundException;

    public abstract ImageIcon getIcon(ImageKey key) throws ImageNotFoundException, IOException;

    public abstract int getImagesAmount();

    protected IconFile() {
    }
}
