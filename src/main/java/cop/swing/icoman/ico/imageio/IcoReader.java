package cop.swing.icoman.ico.imageio;

import cop.swing.icoman.exceptions.IconManagerException;
import cop.swing.icoman.ico.IcoFile;
import cop.swing.icoman.imageio.IconReader;

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