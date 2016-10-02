package cop.icoman;

import cop.icoman.exceptions.ImageNotFoundException;

import java.awt.Image;
import java.util.Set;

/**
 * @author Oleg Cherednik
 * @since 15.08.2015
 */
public interface IconFile extends Iterable<Image> {

    Set<ImageKey> getKeys();

    Image getImage(ImageKey key) throws ImageNotFoundException;

    int getImagesAmount();
}
