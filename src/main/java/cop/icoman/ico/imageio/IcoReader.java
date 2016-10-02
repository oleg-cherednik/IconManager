package cop.icoman.ico.imageio;

import cop.icoman.exceptions.IconManagerException;
import cop.icoman.ico.IcoFile;
import cop.icoman.imageio.IconReader;

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
                icon = IcoFile.read(in);
            }
            return icon;
        } catch(IconManagerException e) {
            throw new IOException(e);
        }
    }
}
