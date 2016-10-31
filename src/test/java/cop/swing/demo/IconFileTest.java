package cop.swing.demo;

import cop.icoman.IconFile;
import cop.icoman.IconIO;
import cop.icoman.IconManager;
import cop.icoman.exceptions.IconManagerException;
import cop.icoman.ico.IcoFile;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Oleg Cherednik
 * @since 30.10.2016
 */
public class IconFileTest {
    @BeforeClass
    public static void init() {
        IconManager.getInstance();
    }

    @Test
    public void testIconFile() throws IOException, IconManagerException {
        IconFile iconFile = IconIO.read(ImageIO.createImageInputStream(IconFileTest.class.getResourceAsStream("/test.ico")));
        assertThat(iconFile).isNotNull();
        assertThat(iconFile).isInstanceOf(IcoFile.class);
        assertThat(iconFile.getTotalImages()).isEqualTo(16);

        Set<String> ids = iconFile.getIds();
        assertThat(ids).hasSize(16);

        String id = getAt(ids.iterator(), 3);    // No.3 - '16x16 256'
        Image image1 = iconFile.getImage(id);
        assertThat(image1).isInstanceOf(BufferedImage.class);
        assertThat(((RenderedImage)image1).getWidth()).isEqualTo(16);
        assertThat(((RenderedImage)image1).getHeight()).isEqualTo(16);

        Image image2 = getAt(iconFile.iterator(), 3);
        assertThat(image2).isSameAs(image1);
    }

    // ========== static ==========

    private static <T> T getAt(Iterator<T> it, int pos) {
        T val = null;

        for (int i = 0; i <= pos; i++)
            val = it.next();

        return val;
    }
}
