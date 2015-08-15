package cop.swing.icoman.imageio.bmp;

import cop.swing.icoman.VersionData;
import cop.swing.icoman.ico.imageio.IcoMetaData;
import cop.swing.icoman.ico.imageio.IcoMetaDataFormat;
import cop.swing.icoman.ico.imageio.IcoReader;

import javax.imageio.ImageReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Locale;

/**
 * @author Oleg Cherednik
 * @since 01.09.2013
 */
public final class IconBitmapReaderSpi extends ImageReaderSpi {
    private static final String[] FORMAT_NAMES = { "bmp", "BMP" };
    private static final String[] EXT = { "bmp" };
    private static final String[] MIME_TYPE = { "image/bmp" };

    static {
        register();
    }

    private IconBitmapReaderSpi() {
        super("cop", VersionData.getVersion(), FORMAT_NAMES, EXT, MIME_TYPE,
                IcoReader.class.getName(), new Class<?>[] { ImageInputStream.class }, null, false, null, null, null,
                null, false, IcoMetaDataFormat.NAME, IcoMetaData.class.getName(), null, null
        );
    }

    // ========== ImageReaderSpi ==========

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
            ImageInputStream in = (ImageInputStream)source;
            ByteOrder byteOrder = in.getByteOrder();

            try {
                in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
                in.mark();
                BitmapInfoHeader header = new BitmapInfoHeader(in);
                in.reset();
                return header.getBiSize() == 40 && header.getBiPlanes() == 1;
            } finally {
                in.setByteOrder(byteOrder);
            }
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
        return new IconBitmapReader(this);
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
        if (isRegistered) return;

        isRegistered = true;

        try {
            Object registeredReader = IIORegistry.getDefaultInstance().getServiceProviderByClass(
                    IconBitmapReaderSpi.class);
            if (registeredReader == null) {
                Object reader = new IconBitmapReaderSpi();
                IIORegistry.getDefaultInstance().registerServiceProvider(reader);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
