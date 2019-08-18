package ru.olegcherednik.icoman.ico.imageio;

import ru.olegcherednik.icoman.exceptions.IconManagerException;
import ru.olegcherednik.icoman.ico.IcoFile;
import ru.olegcherednik.icoman.imageio.IconReader;

import java.io.IOException;
import java.nio.ByteOrder;

/**
 * @author Oleg Cherednik
 * @since 15.08.2015
 */
public class IcoReader extends IconReader {
    private IcoFile icon;

    protected IcoReader(IcoReaderSpi provider) {
        super(provider);
    }

    // ========== IconFileReader ==========

    @Override
    public IcoFile read() throws IOException {
        try {
            if (icon == null) {
                in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
                icon = new IcoFile(in);
            }
            return icon;
        } catch(IconManagerException e) {
            throw new IOException(e);
        }
    }
}
