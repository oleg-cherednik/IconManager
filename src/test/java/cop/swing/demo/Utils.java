package cop.swing.demo;

import java.util.Iterator;

/**
 * @author Oleg Cherednik
 * @since 03.11.2016
 */
final class Utils {
    public static <T> T getAt(Iterator<T> it, int pos) {
        T val = null;

        for (int i = 0; i <= pos; i++)
            val = it.next();

        return val;
    }

    private Utils() {
    }
}
