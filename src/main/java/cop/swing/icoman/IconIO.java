
package cop.swing.icoman;

import cop.swing.icoman.imageio.IconReader;
import cop.swing.icoman.imageio.IconReaderSpi;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
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

    /** @see ImageIO#read(File) */
    public static IconFile read(File file) throws IOException {
        if (file == null)
            throw new IllegalArgumentException("file == null!");
        if (!file.canRead())
            throw new IIOException("Can't read file file!");

        ImageInputStream stream = createImageInputStream(file);

        if (stream == null)
            throw new IIOException("Can't create an ImageInputStream!");

        IconFile bi = read(stream);

        if (bi == null)
            stream.close();

        return bi;
    }

    public static IconFile read(ImageInputStream in) throws IOException {
        if (in == null)
            throw new IllegalArgumentException("in == null!");

        Iterator<IconReader> it = getIconReaders(in);

        if (!it.hasNext())
            return null;

        IconReader reader = it.next();
        reader.setInput(in);

        try {
            return reader.read();
        } finally {
            reader.dispose();
            in.close();
        }
    }

    public static Iterator<IconReader> getIconReaders(ImageInputStream in) {
        if (in == null) {
            throw new IllegalArgumentException("in == null!");
        }
        Iterator iter;
        // Ensure category is present
        try {
            iter = REGISTRY.getServiceProviders(IconReaderSpi.class, new CanDecodeInputFilter(in), true);
        } catch(IllegalArgumentException e) {
            return Collections.emptyIterator();
        }

        return new IconReaderIterator(iter);
    }

    /** @see ImageIO#createImageInputStream(Object) */
    public static ImageInputStream createImageInputStream(Object input) throws IOException {
        if (input == null)
            throw new IllegalArgumentException("in == null!");

        Iterator iter;
        // Ensure category is present
        try {
            iter = REGISTRY.getServiceProviders(ImageInputStreamSpi.class, true);
        } catch(IllegalArgumentException e) {
            return null;
        }

//        boolean usecache = getUseCache() && hasCachePermission();

        while (iter.hasNext()) {
            ImageInputStreamSpi spi = (ImageInputStreamSpi)iter.next();
            if (spi.getInputClass().isInstance(input)) {
                try {
                    return spi.createInputStreamInstance(input);//, usecache, getCacheDirectory());
                } catch(IOException e) {
                    throw new IIOException("Can't create cache file!", e);
                }
            }
        }

        return null;
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
