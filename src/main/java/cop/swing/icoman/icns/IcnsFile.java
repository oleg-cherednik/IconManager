package cop.swing.icoman.icns;

import cop.swing.icoman.IconFile;
import cop.swing.icoman.ImageKey;
import cop.swing.icoman.exceptions.IconManagerException;
import cop.swing.icoman.exceptions.ImageKeyNotSupportedException;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.util.Map;

/**
 * @author Oleg Cherednik
 * @since 02.08.15
 */
public class IcnsFile extends IconFile {
    public static IcnsFile read(ImageInputStream in) throws IOException, IconManagerException {
        IcnsFileHeader header = IcnsFileHeader.read(in);
        Map<ImageKey, IcnsImage> images = readImages(in);
        return new IcnsFile();
    }

    private static Map<ImageKey, IcnsImage> readImages(ImageInputStream in)
            throws IOException, IconManagerException {
        int j = 1;
        while (in.getStreamPosition() < in.length()) {
            ImageKey key = readImageKey(in);
            int size = in.readInt();

            System.out.println(j + " - " + key + " - " + size);
            j++;

            for (int i = 0; i < size - 8; i++)
                in.read();
        }


        return null;
    }

    private static ImageKey readImageKey(ImageInputStream in) throws IOException, ImageKeyNotSupportedException {
        long val = in.readUnsignedInt();

        if (val == 0x69633130)   //ic10 - ICNS_1024x1024_32BIT_ARGB_DATA
            return ImageKey.createXpKey(1024);
        if (val == 0x69633039)   //ic09 - ICNS_512x512_32BIT_ARGB_DATA
            return ImageKey.createXpKey(512);
        if (val == 0x69633038)   //ic08 - ICNS_256x256_32BIT_ARGB_DATA
            return ImageKey.createXpKey(256);
        if (val == 0x69743332)   //it32 - ICNS_128X128_32BIT_DATA
            return ImageKey.createXpKey(128);
        if (val == 0x74386D6B)   //t8mk - ICNS_128X128_8BIT_MASK
            return ImageKey.createKey(128, 8);
        if (val == 0x69636823)   //ich# - ICNS_48x48_1BIT_DATA, ICNS_48x48_1BIT_MASK
            return ImageKey.createKey(48, 1);
        if (val == 0x69636834)   //ich4 - ICNS_48x48_4BIT_DATA
            return ImageKey.createKey(48, 4);
        if (val == 0x69636838)   //ich8 - ICNS_48x48_8BIT_DATA
            return ImageKey.createKey(48, 8);
        if (val == 0x69683332)   //ih32 - ICNS_48x48_32BIT_DATA
            return ImageKey.createXpKey(48);
        if (val == 0x68386D6B)   //h8mk - ICNS_48x48_8BIT_MASK
            return ImageKey.createKey(48, 8);
        if (val == 0x49434E23)   //ICN# - ICNS_32x32_1BIT_DATA, ICNS_32x32_1BIT_MASK
            return ImageKey.createKey(32, 1);
        if (val == 0x69636C34)   //icl4 - ICNS_32x32_4BIT_DATA
            return ImageKey.createKey(32, 4);
        if (val == 0x69636C38)   //icl8 - ICNS_32x32_8BIT_DATA
            return ImageKey.createKey(32, 8);
        if (val == 0x696C3332)   //il32 - ICNS_32x32_32BIT_DATA
            return ImageKey.createXpKey(32);
        if (val == 0x6C386D6B)   //l8mk - ICNS_32x32_8BIT_MASK
            return ImageKey.createKey(32, 8);
        if (val == 0x69637323)   //ics# - ICNS_16x16_1BIT_DATA, ICNS_16x16_1BIT_MASK
            return ImageKey.createKey(16, 1);
        if (val == 0x69637334)   //ics4 - ICNS_16x16_4BIT_DATA
            return ImageKey.createKey(16, 4);
        if (val == 0x69637338)   //ics8 - ICNS_16x16_8BIT_DATA
            return ImageKey.createKey(16, 8);
        if (val == 0x69733332)   //is32 - ICNS_16x16_32BIT_DATA
            return ImageKey.createXpKey(16);
        if (val == 0x73386D6B)   //s8mk - ICNS_16x16_8BIT_MASK
            return ImageKey.createKey(16, 8);
        if (val == 0x69636D23)   //icm# - ICNS_16x12_1BIT_DATA, ICNS_16x12_1BIT_MASK
            return ImageKey.createKey(16, 12, 1);
        if (val == 0x69636D34)   //icm4 - ICNS_16x12_4BIT_DATA
            return ImageKey.createKey(16, 12, 4);
        if (val == 0x69636D38)   //icm4 - ICNS_16x12_8BIT_DATA
            return ImageKey.createKey(16, 12, 8);

        StringBuilder buf = new StringBuilder();

        buf.append((char)(val >> 24 & 0xFF));
        buf.append((char)(val >> 16 & 0xFF));
        buf.append((char)(val >> 8 & 0xFF));
        buf.append((char)(val & 0xFF));

        throw new ImageKeyNotSupportedException(buf.toString());
    }
}
