package cop.icoman.icl.imageio;

import cop.icoman.icl.IclFile;
import cop.icoman.imageio.IconReader;

import java.io.IOException;
import java.nio.ByteOrder;

/**
 * @author Oleg Cherednik
 * @since 02.10.2016
 */
public class IclReader extends IconReader {
    private IclFile icon;

    protected IclReader(IclReaderSpi provider) {
        super(provider);
    }

    // ========== IconFileReader ==========

    @Override
    public IclFile read() throws IOException {
        try {
            if (icon == null) {
                in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
                icon = new IclFile(in);
            }
            return icon;
        } catch(Exception e) {
            throw new IOException(e);
        }
    }
}
