package com.cop.icoman.exceptions;

import com.cop.icoman.ImageKey;

/**
 * @author Oleg Cherednik
 * @since 05.09.2013
 */
public class ImageNotFoundException extends IconManagerException {
    private static final long serialVersionUID = -6470950436657824550L;

    public ImageNotFoundException(int pos, int total) {
        super("image pos = " + pos + ", total = " + total + " not found");
    }

    public ImageNotFoundException(ImageKey key) {
        super("image key = '" + key + "' not found");
    }

    public ImageNotFoundException(String id) {
        super("image key = '" + id + "' not found");
    }
}
