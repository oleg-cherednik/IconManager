
package cop.icoman;

import cop.icoman.imageio.IconReader;
import cop.icoman.imageio.IconReaderSpi;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageInputStreamImpl;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;

/**
 * @author Oleg Cherednik
 * @since 15.08.2015
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IconIO {
    private static final IIORegistry REGISTRY = IIORegistry.getDefaultInstance();

    @SuppressWarnings("MethodCanBeVariableArityMethod")
    public static BufferedImage readImage(byte[] buf) throws IOException {
        return ImageIO.read(new ByteArrayInputStream(buf));
    }

    public static BufferedImage readImage(ImageInputStream in, int size) throws IOException {
        return ImageIO.read(new SubImageInputStream(in, size));
    }

    public static int[] readUnsignedBytes(int total, ImageInputStream in) throws IOException {
        int[] buf = new int[total];

        for (int i = 0; i < total; i++)
            buf[i] = in.readUnsignedByte();

        return buf;
    }

    public static IconFile read(ImageInputStream in) throws IOException {
        if (in == null)
            throw new IllegalArgumentException("in == null");

        Iterator<IconReader> it = getIconReaders(in);

        if (!it.hasNext())
            return null;

        IconReader reader = it.next();
        reader.setInput(in);
        return reader.read();
    }

    private static Iterator<IconReader> getIconReaders(ImageInputStream in) {
        try {
            return new IconReaderIterator(REGISTRY.getServiceProviders(IconReaderSpi.class, new CanDecodeInputFilter(in), true));
        } catch(IllegalArgumentException ignored) {
            return Collections.emptyIterator();
        }
    }

    public static String readString(ImageInputStream in, int length) throws IOException {
        byte ch;
        StringBuilder buf = new StringBuilder();

        for (int i = 0; i < length; i++)
            if ((ch = in.readByte()) != '\0')
                buf.append((char)ch);

        return buf.toString();
    }

    // ========= class ==========

    private static final class CanDecodeInputFilter implements ServiceRegistry.Filter {
        private final ImageInputStream in;

        public CanDecodeInputFilter(ImageInputStream in) {
            this.in = in;
        }

        // ========== ServiceRegistry ==========

        @Override
        public boolean filter(Object elt) {
            try {
                IconReaderSpi spi = (IconReaderSpi)elt;

                boolean canDecode;

                if (in != null)
                    in.mark();

                canDecode = spi.canDecodeInput(in);

                if (in != null)
                    in.reset();

                return canDecode;
            } catch(IOException ignored) {
                return false;
            }
        }
    }

    private static final class IconReaderIterator implements Iterator<IconReader> {
        private final Iterator<IconReaderSpi> it;

        public IconReaderIterator(Iterator<IconReaderSpi> it) {
            this.it = it;
        }

        // ========== Iterator ==========

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public IconReader next() {
            IconReaderSpi spi = null;

            try {
                return (spi = it.next()).createReaderInstance();
            } catch(IOException ignored) {
                REGISTRY.deregisterServiceProvider(spi, IconReaderSpi.class);
                return null;
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static final class SubImageInputStream extends ImageInputStreamImpl {
        private final ImageInputStream in;
        private final long offsBase;
        private final int size;

        public SubImageInputStream(ImageInputStream in, int size) throws IOException {
            this.in = in;
            offsBase = in.getStreamPosition();
            this.size = size;
        }

        // ========== ImageInputStream ==========

        @Override
        public int read() throws IOException {
            if (streamPos == size)
                return -1;
            else {
                streamPos++;
                return in.read();
            }
        }

        @Override
        public int read(byte[] buf, int offs, int len) throws IOException {
            int bytes = -1;

            if (streamPos < size)
                streamPos += bytes = in.read(buf, offs, (int)Math.min(len, size - streamPos));

            return bytes;
        }

        @Override
        public long length() {
            return size;
        }

        @Override
        public void seek(long pos) throws IOException {
            in.seek(offsBase + pos);
            streamPos = pos;
        }

        @Override
        public void close() throws IOException {
            checkClosed();
            in.seek(offsBase + size);
            super.close();
        }

        // ========== Object ==========

        @Override
        protected void finalize() throws Throwable {
            // Empty finalizer (for improved performance; no need to call
            // super.finalize() in this case)
        }
    }
}
