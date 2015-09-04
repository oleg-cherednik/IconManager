
package cop.swing.icoman;

import cop.swing.icoman.imageio.IconReader;
import cop.swing.icoman.imageio.IconReaderSpi;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;

/**
 * @author Oleg Cherednik
 * @since 15.08.2015
 */
public final class IconIO {
    private static final IIORegistry REGISTRY = IIORegistry.getDefaultInstance();

    public static byte[] readBytes(int total, ImageInputStream in) throws IOException {
        byte[] buf = new byte[total];
        in.read(buf);
        return buf;
    }

    public static int[] readUnsignedBytes(int total, ImageInputStream in) throws IOException {
        int[] buf = new int[total];

        for (int i = 0; i < total; i++)
            buf[i] = in.readUnsignedByte();

        return buf;
    }

    /** @see ImageIO#scanForPlugins() */
    public static void scanForPlugins() {
        ImageIO.scanForPlugins();
    }

    public static IconFile read(ImageInputStream in) throws IOException {
        if (in == null)
            throw new IllegalArgumentException("in == null!");

        Iterator<IconReader> it = getIconReaders(in);

        if (!it.hasNext())
            return null;

        IconReader reader = it.next();

        try {
            reader.setInput(in);
            return reader.read();
        } finally {
            reader.dispose();
            in.close();
        }
    }

    private static Iterator<IconReader> getIconReaders(ImageInputStream in) {
        try {
            return new IconReaderIterator(REGISTRY.getServiceProviders(IconReaderSpi.class, new CanDecodeInputFilter(in), true));
        } catch(IllegalArgumentException ignored) {
            return Collections.emptyIterator();
        }
    }

    // ========= class ==========

    static class CanDecodeInputFilter implements ServiceRegistry.Filter {
        ImageInputStream in;

        public CanDecodeInputFilter(ImageInputStream in) {
            this.in = in;
        }

        public boolean filter(Object elt) {
            try {
                IconReaderSpi spi = (IconReaderSpi)elt;

                // Perform mark/reset as a defensive measure
                // even though plug-ins are supposed to take
                // care of it.
                boolean canDecode = false;
                if (in != null) {
                    in.mark();
                }
                canDecode = spi.canDecodeInput(in);
                if (in != null) {
                    in.reset();
                }

                return canDecode;
            } catch(IOException e) {
                return false;
            }
        }
    }

    static class IconReaderIterator implements Iterator<IconReader> {
        // Contains ImageReaderSpis
        public Iterator iter;

        public IconReaderIterator(Iterator iter) {
            this.iter = iter;
        }

        public boolean hasNext() {
            return iter.hasNext();
        }

        public IconReader next() {
            IconReaderSpi spi = null;
            try {
                spi = (IconReaderSpi)iter.next();
                return spi.createReaderInstance();
            } catch(IOException e) {
                // Deregister the spi in this case, but only as
                // an ImageReaderSpi
                REGISTRY.deregisterServiceProvider(spi, IconReaderSpi.class);
            }
            return null;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private IconIO() {
    }
}
