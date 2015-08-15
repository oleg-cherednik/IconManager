package cop.swing.icoman;

import cop.swing.icoman.exceptions.IconDuplicationException;
import cop.swing.icoman.exceptions.IconManagerException;
import cop.swing.icoman.exceptions.IconNotFoundException;
import cop.swing.icoman.icns.imageio.IcnsReaderSpi;
import cop.swing.icoman.ico.IcoFile;
import cop.swing.icoman.ico.imageio.IcoReaderSpi;
import cop.swing.icoman.imageio.bmp.IconBitmapReaderSpi;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.Icon;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Oleg Cherednik
 * @since 01.09.2013
 */
public final class IconManager {
    private static final IconManager INSTANCE = new IconManager();

    private final Map<String, IcoFile> icons = new LinkedHashMap<>();

    public static IconManager getInstance() {
        return INSTANCE;
    }

    static {
        register();
    }

    private IconManager() {
    }

    public Set<String> getIds() {
        return icons.isEmpty() ? Collections.<String>emptySet() : Collections.unmodifiableSet(icons.keySet());
    }

    @NotNull
    public IcoFile addIcon(String id, String filename) throws IconManagerException, IOException {
        IcoFile icon = read(filename);
        addIcon(id, icon);
        return icon;
    }

    @NotNull
    public IcoFile addIcon(String id, ImageInputStream in) throws IconManagerException, IOException {
        if (in == null)
            throw new IOException(String.format("Resource '%s' doesn't exists", id));
        return addIcon(id, read(in));
    }

    public IcoFile addIcon(String id, IcoFile icon) throws IconManagerException {
        if (StringUtils.isBlank(id) || icon == null)
            throw new IconManagerException("id/icon is not set");
        if (icons.put(id, icon) != null)
            throw new IconDuplicationException(id);
        return icon;
    }

    public void removeIcon(String id) {
        icons.remove(id);
    }

    @NotNull
    public IcoFile getIconFile(String id) throws IconNotFoundException {
        IcoFile icon = icons.get(id);

        if (icon == null)
            throw new IconNotFoundException(id);

        return icon;
    }

    @NotNull
    public Icon getIcon(String id, ImageKey key) throws IconManagerException, IOException {
        return getIconFile(id).getImage(key).getIcon();
    }

    // ========== static ==========

    private static void register() {
        IcoReaderSpi.register();
        IcnsReaderSpi.register();

//        IcoReaderSpi.register();
        IconBitmapReaderSpi.register();
    }

    private static IcoFile read(String filename) throws IOException {
        return read(ImageIO.createImageInputStream(IcoFile.class.getClassLoader().getResourceAsStream(filename)));
    }

    private static IcoFile read(ImageInputStream in) throws IOException {
        return (IcoFile)IconIO.read(in);
    }
}
