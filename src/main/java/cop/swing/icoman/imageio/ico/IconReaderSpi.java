package cop.swing.icoman.imageio.ico;

import cop.swing.icoman.VersionData;

import javax.imageio.ImageReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.util.Locale;

/**
 * @author Oleg Cherednik
 * @since 01.09.2013
 */
public final class IconReaderSpi extends ImageReaderSpi {
    private static final String[] FORMAT_NAMES = { "ico", "ICO" };
    private static final String[] EXT = { "ico" };
    private static final String[] MIME_TYPE = { "image/vnd.microsoft.icon", "image/x-ico" };

    static {
        register();
    }

    private IconReaderSpi() {
        super("cop", VersionData.getVersion(), FORMAT_NAMES, EXT, MIME_TYPE,
                IconReader.class.getName(), new Class<?>[] { ImageInputStream.class }, null, false, null, null, null,
                null, false, IconMetaDataFormat.NAME, IconMetaData.class.getName(), null, null
        );
    }

    /**
     * Returns <code>true</code> if the supplied source object appears to be of the format supported by this reader.
     *
     * @param source the object (typically an <code>ImageInputStream</code>) to be decoded.
     * @return <code>true</code> if it is likely that this stream can be decoded.
     * @throws IOException if an I/O error occurs while reading the stream.
     */
    @Override
    public boolean canDecodeInput(Object source) throws IOException {
        if (source instanceof ImageInputStream) {
            byte[] buff = new byte[4];
            ImageInputStream in = (ImageInputStream)source;
            in.mark();
            in.readFully(buff);
            in.reset();
            return isHeaderValid(buff);
        }
        return false;
    }

    /**
     * Returns an instance of the <code>ImageReader</code> implementation  associated with this service provider.
     *
     * @param extension a plug-in specific extension object, which may be {@code null}.
     * @return an <code>ImageReader</code> instance.
     */
    @Override
    public ImageReader createReaderInstance(Object extension) {
        return new IconReader(this);
    }

    /**
     * Returns a brief, human-readable description of this service provider and
     * its associated implementation.
     *
     * @param locale a <code>Locale</code> for which the return value should be
     *               localized.
     * @return a <code>String</code> containing a description of this service
     * provider.
     * @todo Implement this javax.imageio.spi.IIOServiceProvider method
     */
    @Override
    public String getDescription(Locale locale) {
        return "Microsoft IconFile Format (ICO) Reader version: " + VersionData.getVersion();
    }

    // ========== static ==========

    private static volatile boolean isRegistered;

    public static synchronized void register() {
        if (isRegistered)
            return;

        isRegistered = true;

        try {
            Object registeredReader = IIORegistry.getDefaultInstance().getServiceProviderByClass(IconReaderSpi.class);
            if (registeredReader == null) {
                Object reader = new IconReaderSpi();
                IIORegistry.getDefaultInstance().registerServiceProvider(reader);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isHeaderValid(byte... buff) {
        return buff[0] == 0x00 && buff[1] == 0x00 && buff[2] == 0x01 && buff[3] == 0x00;
    }
}
