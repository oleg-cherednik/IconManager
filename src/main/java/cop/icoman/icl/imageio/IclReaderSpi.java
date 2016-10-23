package cop.icoman.icl.imageio;

import cop.icoman.imageio.IconReader;
import cop.icoman.imageio.IconReaderSpi;

import javax.imageio.spi.IIORegistry;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.nio.ByteOrder;

/**
 * @author Oleg Cherednik
 * @since 02.10.2016
 */
public final class IclReaderSpi extends IconReaderSpi {
    private static final IclReaderSpi INSTANCE = new IclReaderSpi();

    private IclReaderSpi() {
    }

    // ========== IconFileReaderSpi ==========

    @Override
    public boolean canDecodeInput(ImageInputStream in) throws IOException {
        return canDecodeInput(in, () -> {
            in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
            return isHeaderValid(in.readUnsignedShort());
        });
    }

    @Override
    public IconReader createReaderInstance(Object extension) {
        return new IclReader(this);
    }

    // ========== static ==========

    public static synchronized void register() {
        IIORegistry.getDefaultInstance().registerServiceProvider(INSTANCE);
    }

    public static boolean isHeaderValid(int marker) {
        return marker == 0x5A4D;    // MZ
    }
}
