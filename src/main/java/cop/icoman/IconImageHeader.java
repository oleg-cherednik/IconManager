package cop.icoman;

import java.util.Comparator;

/**
 * @author Oleg Cherednik
 * @since 27.10.2016
 */
public interface IconImageHeader {
    int getWidth();

    int getHeight();

    int getBitsPerPixel();

    Comparator<IconImageHeader> SORT_BY_BITS_SIZE_ASC = (header1, header2) -> {
        if (header1 == header2)
            return 0;

        int res;

        if ((res = Integer.compare(header1.getBitsPerPixel(), header2.getBitsPerPixel())) != 0)
            return res;
        if ((res = Integer.compare(header1.getWidth(), header2.getWidth())) != 0)
            return res;
        return Integer.compare(header1.getHeight(), header2.getHeight());
    };
}
