package com.cop.icoman;

import com.cop.icoman.exceptions.IconManagerException;
import com.cop.icoman.icns.IcnsFile;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Oleg Cherednik
 * @since 03.11.2016
 */
public class IcnsFileTest {

    @BeforeClass
    public static void init() {
        IconManager.getInstance();
    }

    @Test
    public void testIcnsFile() throws IOException, IconManagerException {
        IconFile iconFile = IconIO.read(ImageIO.createImageInputStream(IcnsFileTest.class.getResourceAsStream("/test.icns")));
        assertThat(iconFile).isNotNull();
        assertThat(iconFile).isInstanceOf(IcnsFile.class);
        assertThat(iconFile.getTotalImages()).isEqualTo(7);

        Set<String> ids = iconFile.getIds();
        assertThat(iconFile.getIds()).hasSize(7);

        String id = TestUtils.getAt(ids.iterator(), 3);
        assertThat(id).isEqualTo("16x16_32");

        Image expectedImage = iconFile.getImage(id);
        assertThat(expectedImage).isInstanceOf(BufferedImage.class);
        assertThat(((RenderedImage)expectedImage).getWidth()).isEqualTo(16);
        assertThat(((RenderedImage)expectedImage).getHeight()).isEqualTo(16);
        assertThat(TestUtils.getAt(iconFile.iterator(), 3)).isSameAs(expectedImage);
        assertThat(iconFile.getImage("16x16_32")).isSameAs(expectedImage);
    }
}
