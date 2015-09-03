package cop.swing.icoman.icns.foo;

import java.util.Arrays;

/**
 * @author Oleg Cherednik
 * @since 25.08.2015
 */
public final class rle24 {
    public static int[] decompress(int[] data, int total) {
        int[] buf = new int[total * 3];

        int i = 0;
        int offs = 0;

        while (i < data.length) {
            int count = data[i++];

            if (count == 0xc1)
                buf[offs++] = data[i++];
            else if (count <= 0xc0)
                buf[offs++] = count;
            else if (count > 0xc1) {
                int next = data[i++];

                for (int j = 0; j < (count - 0xc0); j++)
                    buf[offs++] = next;
            }
        }

        System.out.println(i);
        System.out.println(data.length);
        System.out.println(offs);

        return Arrays.copyOf(buf, offs);
    }

    public static int[] icns_decode_rle24_data1(int[] rawDataPtr, int expectedPixelCount, int dataSizeOut) {
        int runLength = 0;
        int dataOffset = 0;
        int[] destIconData;    // Decompressed Raw Icon Data

        // Calculate required data storage (pixels * 4 channels)
        int destIconDataSize = expectedPixelCount * 4;
        int[] dataPtrOut = new int[destIconDataSize];

        System.out.println(String.format("Compressed RLE data size is %d", rawDataPtr.length));
        System.out.println(String.format("Decompressed will be %d bytes (%d pixels)", destIconDataSize, expectedPixelCount));

        if (dataSizeOut != destIconDataSize) {
            // Allocate the block for the decoded memory and set to 0
            destIconData = new int[destIconDataSize];
        } else {
            destIconData = new int[dataSizeOut];
        }

        System.out.println("Decoding RLE data into RGB pixels...");

        // What's this??? In the 128x128 icons, we need to start 4 bytes
        // ahead. There is often a NULL padding here for some reason. If
        // we don't, the red channel will be off by 2 pixels, or worse
        if (rawDataPtr[0] == 0x0 && rawDataPtr[1] == 0x0 && rawDataPtr[2] == 0x0 && rawDataPtr[3] == 0x0) {
            System.out.println("4 byte null padding found in rle data!");
            dataOffset = 4;
        } else {
            dataOffset = 0;
        }

        // Data is stored in red run, green run,blue run
        // So we decompress to pixel format RGBA
        // RED:   byte[0], byte[4], byte[8]  ...
        // GREEN: byte[1], byte[5], byte[9]  ...
        // BLUE:  byte[2], byte[6], byte[10] ...
        // ALPHA: byte[3], byte[7], byte[11] do nothing with these bytes
        for (int colorOffset = 0; colorOffset < 3; colorOffset++) {
            int pixelOffset = 0;
            while ((pixelOffset < expectedPixelCount) && (dataOffset < rawDataPtr.length)) {
                if ((rawDataPtr[dataOffset] & 0x80) == 0) {
                    // Top bit is clear - run of various values to follow
                    runLength = (0xFF & rawDataPtr[dataOffset++]) + 1; // 1 <= len <= 128
                    for (int i = 0; (i < runLength) && (pixelOffset < expectedPixelCount) && (dataOffset < rawDataPtr.length); i++) {
                        destIconData[(pixelOffset * 4) + colorOffset] = rawDataPtr[dataOffset++];
                        pixelOffset++;
                    }
                } else {
                    // Top bit is set - run of one value to follow
                    runLength = (0xFF & rawDataPtr[dataOffset++]) - 125; // 3 <= len <= 130
                    // Set the value to the color shifted to the correct bit offset
                    int colorValue = rawDataPtr[dataOffset++];

                    for (int i = 0; (i < runLength) && (pixelOffset < expectedPixelCount); i++) {
                        destIconData[(pixelOffset * 4) + colorOffset] = colorValue;
                        pixelOffset++;
                    }
                }
            }
        }

        return destIconData;
    }

    public static int[] icns_decode_rle24_data(int rawDataSize, int[] data, int expectedPixelCount, int dataSizeOut) {
        int runLength = 0;
        int offs = 0;
        int[] destIconData;    // Decompressed Raw Icon Data

        // Calculate required data storage (pixels * 4 channels)
        int destIconDataSize = expectedPixelCount * 4;

        System.out.println(String.format("Compressed RLE data size is %d", rawDataSize));
        System.out.println(String.format("Decompressed will be %d bytes (%d pixels)", destIconDataSize, expectedPixelCount));

        if (dataSizeOut != destIconDataSize) {
            // Allocate the block for the decoded memory and set to 0
            destIconData = new int[destIconDataSize];
        } else {
            destIconData = new int[dataSizeOut];
        }

        System.out.println("Decoding RLE data into RGB pixels...");

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
        for (int colorOffset = 0; colorOffset < 3; colorOffset++) {
            int pixelOffset = 0;
            while ((pixelOffset < expectedPixelCount) && (offs < rawDataSize)) {
                if ((data[offs] & 0x80) == 0) {
                    // Top bit is clear - run of various values to follow
                    runLength = (0xFF & data[offs++]) + 1; // 1 <= len <= 128
                    for (int i = 0; (i < runLength) && (pixelOffset < expectedPixelCount) && (offs < rawDataSize); i++) {
                        destIconData[(pixelOffset * 4) + colorOffset] = data[offs++];
                        pixelOffset++;
                    }
                } else {
                    // Top bit is set - run of one value to follow
                    runLength = (0xFF & data[offs++]) - 125; // 3 <= len <= 130
                    // Set the value to the color shifted to the correct bit offset
                    int colorValue = data[offs++];

                    for (int i = 0; (i < runLength) && (pixelOffset < expectedPixelCount); i++) {
                        destIconData[(pixelOffset * 4) + colorOffset] = colorValue;
                        pixelOffset++;
                    }
                }
            }
        }

        return destIconData;
    }

    private rle24() {
    }
}
