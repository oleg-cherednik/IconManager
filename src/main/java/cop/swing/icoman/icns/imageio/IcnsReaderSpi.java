package cop.swing.icoman.icns.imageio;

import cop.swing.icoman.imageio.IconReader;
import cop.swing.icoman.imageio.IconReaderSpi;

import javax.imageio.spi.IIORegistry;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 14.08.2015
 */
public final class IcnsReaderSpi extends IconReaderSpi {
    private IcnsReaderSpi() {
    }

    // ========== IconFileReaderSpi ==========

    @Override
    public boolean canDecodeInput(ImageInputStream in) throws IOException {
        try {
            in.mark();
            return isHeaderValid(in.readInt());
        } finally {
            in.reset();
        }
    }

    @Override
    public IconReader createReaderInstance(Object extension) {
        return new IcnsReader(this);
    }

    // ========== static ==========

    public static synchronized void register() {
        IIORegistry.getDefaultInstance().registerServiceProvider(new IcnsReaderSpi());
    }

    public static boolean isHeaderValid(int marker) {
        return marker == 0x69636E73;    //icns
    }
}
