package cop.swing.icoman.icns.foo;

/**
 * @author Oleg Cherednik
 * @since 25.08.2015
 */
public class icns_icon_info_t {
    public int iconType;         // uint32, type of icon (or mask)
    boolean isImage;          // uint8, is this type an image
    boolean isMask;           //uint8,  is this type a mask
    int iconWidth;        // uint32, width of icon in pixels
    int iconHeight;       // uint32, height of icon in pixels
    int iconChannels;     // uint8, number of channels in data
    int iconPixelDepth;   // uint16, number of bits-per-pixel
    int iconBitDepth;     // uint16, overall bit depth = iconPixelDepth * iconChannels
    int iconRawDataSize;  // uint64, uncompressed bytes = width * height * depth / bits-per-pixel
}
