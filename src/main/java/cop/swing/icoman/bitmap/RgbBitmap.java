package cop.swing.icoman.bitmap;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Oleg Cherednik
 * @since 01.09.2013
 */
public final class RgbBitmap extends Bitmap {
    private final int readBytes;

    public RgbBitmap(BitmapInfoHeader header, int readBytes, InputStream in) throws IOException {
        this(header, readBytes, ImageIO.createImageInputStream(in));
    }

    public RgbBitmap(BitmapInfoHeader header, int readBytes, ImageInputStream in) throws IOException {
        super(header);
        this.readBytes = readBytes;
        readImage(in);
    }

    // ========== Bitmap ==========

    @Override
    protected BufferedImage createImage(ImageInputStream in) throws IOException {
        final int w = height;
        final int h = width;

        //boolean debuginf = readBytes > 3 && w == 32;
        int[] pixeldata = new int[h * w];
        for (int rijNr = 0; rijNr < w; rijNr++) {
            byte[] rij = new byte[h * readBytes];
            in.read(rij);
            int rByte = 0;
            int oPos = (w - rijNr - 1) * h;
            for (int colNr = 0; colNr < h; colNr++) {
                // BGR -> RGB
                int pos = oPos++;
/*        if (false && readBytes > 2) {
          int r = rij[rByte + 2] & 0xFF;
          int g = rij[rByte + 1] & 0xFF;
          int b = rij[rByte] & 0xFF;

          java.awt.Color c;
          if (readBytes > 3) {
            int a = rij[rByte + 3] & 0xFF;
            c = new java.awt.Color(r, g, b, a);
          } else {
            c = new java.awt.Color(r, g, b, 255);
          }
          pixeldata[pos] = c.getRGB();
          rByte += readBytes;
        } else {
 */
                //added &0xFF to every byte read... this seems to solve all the trouble
                //I had with the 32bit icons
                pixeldata[pos] =
                        (rij[rByte++] & 0xFF); //8bit pixel data (blue)
                if (readBytes > 1) //16bit pixel data
                    pixeldata[pos] += ((rij[rByte++] & 0xFF) << 8); //greeen
                else
                    pixeldata[pos] += (0 << 8);
                if (readBytes > 2) //24bit pixel data
                    pixeldata[pos] += ((rij[rByte++] & 0xFF) << 16); //red
                else
                    pixeldata[pos] += (0 << 16);

                if (readBytes > 3) //32bit alpha channel
                    pixeldata[pos] += ((rij[rByte++] & 0xFF) << 24);
                    // else if (pixeldata[pos] == 0)
                    //   pixeldata[pos] += (0 << 24); //<!-- bugfix if the alphamask is not present, it should be set to transparent, not to fully visible. note: this is not the way to do it, now all black pixels become transparent
                else
                    pixeldata[pos] += ((255) << 24);
            }
            //}
        }
        BufferedImage bIm;
        // if (readBytes>3){
        bIm = new BufferedImage(h, w, BufferedImage.TYPE_INT_ARGB);
        // }  else{
        //  bIm = new BufferedImage(entry.getWidth(), entry.getHeight(), BufferedImage.TYPE_INT_RGB);
        //}

        bIm.setRGB(0, 0, h, w, pixeldata, 0, h);
        return bIm;
    }
}
