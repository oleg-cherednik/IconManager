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
    static {
        register();
    }

    private IcnsReaderSpi() {
    }

    // ========== IconFileReaderSpi ==========

    @Override
    public boolean canDecodeInput(ImageInputStream in) throws IOException {
        return isHeaderValid(readHeader(in));
    }

    @Override
    public IconReader createReaderInstance(Object extension) {
        return new IcnsReader(this);
    }

    // ========== static ==========

    public static synchronized void register() {
        IIORegistry.getDefaultInstance().registerServiceProvider(new IcnsReaderSpi());
    }

    public static boolean isHeaderValid(byte... buf) {
        return buf[0] == 'i' && buf[1] == 'c' && buf[2] == 'n' && buf[3] == 's';
    }

    public static byte[] readHeader(ImageInputStream in) throws IOException {
        byte[] buf = new byte[4];
        in.readFully(buf);
        return buf;
    }
}
