package cop.swing.icoman;

import cop.swing.icoman.exceptions.ImageNotFoundException;

import javax.swing.ImageIcon;
import java.util.Set;

/**
 * @author Oleg Cherednik
 * @since 15.08.2015
 */
public interface IconFile extends Iterable<ImageIcon> {
    Set<ImageKey> getKeys();

    ImageIcon getImage(ImageKey key) throws ImageNotFoundException;

    int getImagesAmount();
}
