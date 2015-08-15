package cop.swing.icoman.imageio;

import cop.swing.icoman.IconFile;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 15.08.2015
 */
public abstract class IconReader {
    protected ImageInputStream in;
    protected final IconReaderSpi provider;

    protected IconReader(IconReaderSpi provider) {
        this.provider = provider;
    }

    public void setInput(ImageInputStream in) {
        this.in = in;
    }

    public void dispose() {
    }

    public abstract IconFile read() throws IOException;
}
