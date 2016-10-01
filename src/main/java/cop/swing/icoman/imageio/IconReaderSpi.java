package cop.swing.icoman.imageio;

import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author Oleg Cherednik
 * @since 15.08.2015
 */
public abstract class IconReaderSpi {

    public IconReader createReaderInstance() throws IOException {
        return createReaderInstance(null);
    }

    public abstract IconReader createReaderInstance(Object extension);

    public abstract boolean canDecodeInput(ImageInputStream in) throws IOException;

    // ========== static ==========

    static {
        // Add new category to the internal registry map
        try {
            IIORegistry registry = IIORegistry.getDefaultInstance();
            Field field = registry.getClass().getSuperclass().getDeclaredField("categoryMap");
            field.setAccessible(true);
            Map<Class<?>, Object> categoryMap = (Map<Class<?>, Object>)field.get(registry);
            Class<?> cls = ClassLoader.getSystemClassLoader().loadClass("javax.imageio.spi.SubRegistry");
            Constructor<?> constructor = cls.getConstructor(ServiceRegistry.class, Class.class);
            constructor.setAccessible(true);
            categoryMap.put(IconReaderSpi.class, constructor.newInstance(registry, IconReaderSpi.class));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
