package cop.swing.icoman.icns.imageio;

import cop.swing.icoman.exceptions.IconManagerException;
import cop.swing.icoman.icns.IcnsFile;
import cop.swing.icoman.imageio.IconReader;

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
            if (icon != null)
                return icon;

            in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
            in.mark();
            return icon = IcnsFile.read(in);
        } catch(IconManagerException e) {
            throw new IOException(e);
        }
    }
}
