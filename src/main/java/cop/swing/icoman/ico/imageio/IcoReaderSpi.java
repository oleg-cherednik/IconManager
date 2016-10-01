package cop.swing.icoman.ico.imageio;

import cop.swing.icoman.imageio.IconReader;
import cop.swing.icoman.imageio.IconReaderSpi;
import cop.swing.icoman.imageio.bmp.IconBitmapReaderSpi;

import javax.imageio.spi.IIORegistry;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * This is spi for ico files as one file
 *
 * @author Oleg Cherednik
 * @since 15.08.2015
 */
public final class IcoReaderSpi extends IconReaderSpi {
    private IcoReaderSpi() {
    }

    // ========== IconFileReaderSpi ==========

    @Override
    public IconReader createReaderInstance(Object extension) {
        return new IcoReader(this);
    }


    @Override
    public boolean canDecodeInput(ImageInputStream in) throws IOException {
        return isHeaderValid(in.readInt());
    }

    // ========== static ==========

    public static synchronized void register() {
        IconBitmapReaderSpi.register();
        IIORegistry.getDefaultInstance().registerServiceProvider(new IcoReaderSpi());
    }

    public static boolean isHeaderValid(int marker) {
        return marker == 0x100;
    }
}
