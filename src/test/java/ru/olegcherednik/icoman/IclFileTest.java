package ru.olegcherednik.icoman;

import ru.olegcherednik.icoman.exceptions.IconManagerException;
import ru.olegcherednik.icoman.icl.IclFile;
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
 * @since 03.11.2016
 */
public class IclFileTest {

    @BeforeClass
    public static void init() {
        IconManager.getInstance();
    }

    @Test
    public void testIclFile() throws IOException, IconManagerException {
        IconFile iconFile = IconIO.read(ImageIO.createImageInputStream(IcnsFileTest.class.getResourceAsStream("/test.icl")));
        assertThat(iconFile).isNotNull();
        assertThat(iconFile).isInstanceOf(IclFile.class);
        assertThat(iconFile.getTotalImages()).isEqualTo(52);

        Set<String> ids = iconFile.getIds();
        assertThat(ids).hasSize(52);

        String id = TestUtils.getAt(ids.iterator(), 3);
        assertThat(id).isEqualTo("doom_48x48_8");

        Image expectedImage = iconFile.getImage(id);
        assertThat(expectedImage).isInstanceOf(BufferedImage.class);
        assertThat(((RenderedImage)expectedImage).getWidth()).isEqualTo(48);
        assertThat(((RenderedImage)expectedImage).getHeight()).isEqualTo(48);
        assertThat(TestUtils.getAt(iconFile.iterator(), 3)).isSameAs(expectedImage);
        assertThat(iconFile.getImage("doom_48x48_8")).isSameAs(expectedImage);

        IclFile iclFile = (IclFile)iconFile;
        Set<String> names = iclFile.getNames();
        Iterator<String> it = names.iterator();
        assertThat(it.next()).isEqualTo("Doom");
        assertThat(it.next()).isEqualTo("Hitman");
        assertThat(it.next()).isEqualTo("NFS");
        assertThat(it.next()).isEqualTo("Starcraft");

        assertThat(iclFile.getImages("Doom")).hasSize(13);
        assertThat(iclFile.getImages("Doom").get("doom_48x48_8")).isSameAs(expectedImage);
    }
}
