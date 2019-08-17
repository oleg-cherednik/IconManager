package com.cop.icoman;

import com.cop.icoman.exceptions.ImageNotFoundException;
import org.apache.commons.collections.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.awt.Image;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Oleg Cherednik
 * @since 23.10.2016
 */
public abstract class AbstractIconFile implements IconFile {
    private final Map<String, Image> imageById;

    protected AbstractIconFile(Map<String, Image> imageById) {
        this.imageById = CollectionUtils.size(imageById) == 0 ? Collections.emptyMap() : Collections.unmodifiableMap(imageById);
    }

    // ========== IconFile ==========

    @NotNull
    @Override
    public Set<String> getIds() {
        return imageById.isEmpty() ? Collections.emptySet() : imageById.keySet();
    }

    @NotNull
    @Override
    public Image getImage(String id) throws ImageNotFoundException {
        if (!imageById.containsKey(id))
            throw new ImageNotFoundException(id);
        return imageById.get(id);
    }

    @Override
    public int getTotalImages() {
        return imageById.size();
    }

    // ========== Iterable ==========

    @Override
    public Iterator<Image> iterator() {
        return imageById.values().iterator();
    }

}
