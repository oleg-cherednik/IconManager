package cop.swing.icoman.icns;

/**
 * @author Oleg Cherednik
 * @since 25.08.2015
 */
final class rle24 {
    public static int[] decompress(int width, int height, int[] data, int[] mask) {
        int[] buf = new int[width * height * 4];
        final int totalPix = width * height;

        // In the 128x128 icons, we need to start 4 bytes ahead. There is often a עדהה padding here for some reason. If we don't, the red channel
        // will be off by 2 pixels, or worse
        int offs = data[0] == 0x0 && data[1] == 0x0 && data[2] == 0x0 && data[3] == 0x0 ? 4 : 0;

        // red:   [0], [4], [8]...
        // green: [1], [5], [9]...
        // blue:  [2], [6], [10]...
        // alpha: [3], [7], [11]... (do nothing with these bytes)
        for (int offsColor = 2, offsPix = 0; offsColor >= 0; offsColor--, offsPix = 0) {
            while (offsPix < totalPix && offs < data.length) {
                if ((data[offs] & 0x80) == 0) {
                    // Top bit is clear - run of various values to follow
                    int total = (0xFF & data[offs++]) + 1; // 1 <= len <= 128

                    for (int i = 0; i < total && offsPix < totalPix && offs < data.length; i++) {
                        buf[(offsPix * 4) + offsColor] = data[offs++];
                        offsPix++;
                    }
                } else {
                    // Top bit is set - run of one value to follow
                    int total = data[offs++] - 125; // 3 <= len <= 130
                    int color = data[offs++];

                    for (int i = 0; i < total && offsPix < totalPix; i++, offsPix++)
                        buf[offsPix * 4 + offsColor] = color;
                }
            }
        }

        if (mask != null)
            for (int i = 0, offs1 = 3; i < mask.length; i++, offs1 += 4)
                buf[offs1] = mask[i];

        return buf;
    }

    private rle24() {
    }
}
