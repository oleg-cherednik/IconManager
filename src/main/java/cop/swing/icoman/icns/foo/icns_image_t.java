package cop.swing.icoman.icns.foo;

/**
 * @author Oleg Cherednik
 * @since 25.08.2015
 */
public class icns_image_t {
    public int imageWidth;     // uint32, width of image in pixels
    public int imageHeight;    // uint32, height of image in pixels
    public int imageChannels;  // uint8, number of channels in data
    public int imagePixelDepth;// uint16, number of bits-per-pixel
    public int imageDataSize;  // uint64, bytes = width * height * depth / bits-per-pixel
    public byte[] imageData;     // pointer to base address of uncompressed raw image data
}
