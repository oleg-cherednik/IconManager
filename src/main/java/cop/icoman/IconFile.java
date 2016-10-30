package cop.icoman;

import cop.icoman.exceptions.ImageNotFoundException;

import javax.validation.constraints.NotNull;
import java.awt.Image;
import java.util.Set;

/**
 * @author Oleg Cherednik
 * @since 15.08.2015
 */
public interface IconFile extends Iterable<Image> {
    @NotNull
    Set<String> getIds();

    @NotNull
    Image getImage(String id) throws ImageNotFoundException;

    int getTotalImages();

}
