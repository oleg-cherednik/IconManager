package cop.swing.icoman.icns;

import cop.swing.icoman.IconFile;
import cop.swing.icoman.exceptions.IconManagerException;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 02.08.15
 */
public class IcnsFile extends IconFile {
    public static IcnsFile read(ImageInputStream in) throws IOException, IconManagerException {
        int offs = in.getBitOffset();
        int a = in.readInt();
        offs = in.getBitOffset();
//        IconFileHeader header = IconFileHeader.readHeader(in);
//        Map<ImageKey, IconImage> images = readImages(header, in);
//        return null;//new IconFile(header, images);
        return new IcnsFile();
    }
}
