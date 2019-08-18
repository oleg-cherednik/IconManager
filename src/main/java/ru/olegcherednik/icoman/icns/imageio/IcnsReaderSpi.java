package ru.olegcherednik.icoman.icns.imageio;

import ru.olegcherednik.icoman.imageio.IconReader;
import ru.olegcherednik.icoman.imageio.IconReaderSpi;

import javax.imageio.spi.IIORegistry;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 14.08.2015
 */
public final class IcnsReaderSpi extends IconReaderSpi {
    private static final IcnsReaderSpi INSTANCE = new IcnsReaderSpi();

    private IcnsReaderSpi() {
    }

    // ========== IconFileReaderSpi ==========

    @Override
    public boolean canDecodeInput(ImageInputStream in) throws IOException {
        return canDecodeInput(in, () -> isHeaderValid(in.readInt()));
    }

    @Override
    public IconReader createReaderInstance(Object extension) {
        return new IcnsReader(this);
    }

    // ========== static ==========

    public static synchronized void register() {
        IIORegistry.getDefaultInstance().registerServiceProvider(INSTANCE);
    }

    public static boolean isHeaderValid(int marker) {
        return marker == 0x69636E73;    //icns
    }
}
