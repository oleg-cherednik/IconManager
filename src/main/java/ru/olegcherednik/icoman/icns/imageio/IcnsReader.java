package ru.olegcherednik.icoman.icns.imageio;

import ru.olegcherednik.icoman.icns.IcnsFile;
import ru.olegcherednik.icoman.imageio.IconReader;

import java.io.IOException;
import java.nio.ByteOrder;

/**
 * @author Oleg Cherednik
 * @since 14.08.2015
 */
public class IcnsReader extends IconReader {
    private IcnsFile icon;

    protected IcnsReader(IcnsReaderSpi provider) {
        super(provider);
    }

    // ========== IconFileReader ==========

    @Override
    public IcnsFile read() throws IOException {
        try {
            if (icon == null) {
                in.setByteOrder(ByteOrder.BIG_ENDIAN);
                icon = IcnsFile.read(in);
            }
            return icon;
        } catch(Exception e) {
            throw new IOException(e);
        }
    }
}
