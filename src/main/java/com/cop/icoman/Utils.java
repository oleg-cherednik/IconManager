package com.cop.icoman;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Oleg Cherednik
 * @since 27.10.2016
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Utils {
    @SuppressWarnings("StaticMethodNamingConvention")
    public static int zeroTo256(int size) {
        return size != 0 ? size : 256;
    }

    public static int bitsPerPixel(int bitsPerPixel, int colors) {
        return colors != 0 ? (int)Math.sqrt(colors) : bitsPerPixel;
    }
}
