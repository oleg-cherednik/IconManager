package cop.swing.icoman.imageio.bmp;

import java.io.IOException;

public class DecodeRLE8 {
    private static final byte ESCAPE = (byte)0;
    private static final byte EOL = (byte)0; // end of line
    private static final byte EOB = (byte)1; // end of bitmap
    private static final byte DELTA = (byte)2; // delta

    public static int[] uncompress(int w, int h, int[] in) throws IOException {
        int[] cmd = new int[2];
        int[] data = new int[w * h];
        int offIn = 0;
        int x = 0, y = 0;
        int offs = 0;
        while ((x + y * w) < w * h) {
            cmd[0] = in[offs++];
            cmd[1] = in[offs++];

            if (cmd[0] == ESCAPE) {
                switch (cmd[1]) {
                    case EOB: // end of bitmap
                        return data;
                    case EOL: // end of line
                        x = 0;
                        y++;
                        break;
                    case DELTA: // delta
                        cmd[0] = in[offs++];
                        cmd[1] = in[offs++];
                        int dx = cmd[0] & (0xFF);
                        int dy = cmd[1] & (0xFF);
                        x += dx;
                        y += dy;
                        break;

                    default:
                        // decode a literal run
                        int length = cmd[1] & (0xFF);
                        int copylength = length;

                        // absolute mode must be word-aligned
                        length += (length & 1);

                        byte[] run = new byte[length];

                        for(int i = 0; i < run.length; i++)
                            run[i] = (byte)in[offs++];

                        System.arraycopy(run, 0, data, (x + w * (h - y - 1)),
                                copylength);
                        x += copylength;
                        break;
                }
            } else {
                // decode a byte run
                int length = cmd[0] & (0xFF);
                for (int i = 0; i < length; i++)
                    data[(h - y - 1) * w + x++] = cmd[1];
            }
        }
        return data;
    }
}
