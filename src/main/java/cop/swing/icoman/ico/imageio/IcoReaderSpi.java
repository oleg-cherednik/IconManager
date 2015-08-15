package cop.swing.icoman.ico.imageio;

import cop.swing.icoman.imageio.IconReader;
import cop.swing.icoman.imageio.IconReaderSpi;

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
    // ========== IconFileReaderSpi ==========

    @Override
    public IconReader createReaderInstance(Object extension) {
        return new IcoReader(this);
    }


    @Override
    public boolean canDecodeInput(ImageInputStream in) throws IOException {
        byte[] buf = new byte[4];
        in.readFully(buf);
        return isHeaderValid(buf);
    }
    // ========== static ==========

    public static synchronized void register() {
        IIORegistry.getDefaultInstance().registerServiceProvider(new IcoReaderSpi());
    }

    public static boolean isHeaderValid(byte... buf) {
        return buf[0] == 0x00 && buf[1] == 0x00 && buf[2] == 0x01 && buf[3] == 0x00;
    }
}
