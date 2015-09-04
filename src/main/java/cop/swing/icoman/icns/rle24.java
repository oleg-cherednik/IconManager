package cop.swing.icoman.icns;

/**
 * @author Oleg Cherednik
 * @since 25.08.2015
 */
final class rle24 {
    public static int[] decompress(int width, int height, int[] data, int[] mask) {
        int runLength = 0;
        int offs = 0;
        int[] buf = new int[width * height * 4];
        int expectedPixelCount = width * height;

        // What's this??? In the 128x128 icons, we need to start 4 bytes
        // ahead. There is often a NULL padding here for some reason. If
        // we don't, the red channel will be off by 2 pixels, or worse
        if (data[0] == 0x0 && data[1] == 0x0 && data[2] == 0x0 && data[3] == 0x0) {
            System.out.println("4 byte null padding found in rle data!");
            offs = 4;
        } else {
            offs = 0;
        }

        // Data is stored in red run, green run,blue run
        // So we decompress to pixel format RGBA
        // RED:   byte[0], byte[4], byte[8]  ...
        // GREEN: byte[1], byte[5], byte[9]  ...
        // BLUE:  byte[2], byte[6], byte[10] ...
        // ALPHA: byte[3], byte[7], byte[11] do nothing with these bytes
        for (int colorOffset = 2; colorOffset >= 0; colorOffset--) {
            int pixelOffset = 0;
            while ((pixelOffset < expectedPixelCount) && (offs < data.length)) {
                if ((data[offs] & 0x80) == 0) {
                    // Top bit is clear - run of various values to follow
                    runLength = (0xFF & data[offs++]) + 1; // 1 <= len <= 128
                    for (int i = 0; (i < runLength) && (pixelOffset < expectedPixelCount) && (offs < data.length); i++) {
                        buf[(pixelOffset * 4) + colorOffset] = data[offs++];
                        pixelOffset++;
                    }
                } else {
                    // Top bit is set - run of one value to follow
                    runLength = data[offs++] - 125; // 3 <= len <= 130
                    int color = data[offs++];

                    for (int i = 0; (i < runLength) && (pixelOffset < expectedPixelCount); i++, pixelOffset++)
                        buf[pixelOffset * 4 + colorOffset] = color;
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
