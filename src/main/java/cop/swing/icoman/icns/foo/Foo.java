package cop.swing.icoman.icns.foo;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Oleg Cherednik
 * @since 24.08.2015
 */
public class Foo {
    public static icns_family_t icns_read_family_from_file(ImageInputStream dataFile) throws Exception {
        int dataSize = (int)dataFile.length();

        dataFile.mark();

        // Attempt 1 - try to import as an 'icns' file
        if (icns_icns_header_check(dataSize, dataFile)) {
            System.out.println("Trying to read from icns file...");
            dataFile.reset();
            return icns_parse_family_data(dataFile);
        }
        // Attempt 2 - try to import from an 'icns' resource in a big endian macintosh resource file
     /*   else if (icns_rsrc_header_check(dataSize, dataPtr, ICNS_BE_RSRC)) {
            #ifdef ICNS_DEBUG
            printf("Trying to find icns data in resource file...\n");
            #endif
            if ((error = icns_find_family_in_mac_resource(dataSize, dataPtr, ICNS_BE_RSRC, iconFamilyOut))) {
                icns_print_err("icns_read_family_from_file: Error reading macintosh resource file!\n");
                *iconFamilyOut = NULL;
            }
        }
        // Attempt 3 - try to import from an 'icns' resource in a little endian macintosh resource file
        else if (icns_rsrc_header_check(dataSize, dataPtr, ICNS_LE_RSRC)) {
            #ifdef ICNS_DEBUG
            printf("Trying to find icns data in resource file...\n");
            #endif
            if ((error = icns_find_family_in_mac_resource(dataSize, dataPtr, ICNS_LE_RSRC, iconFamilyOut))) {
                icns_print_err("icns_read_family_from_file: Error reading macintosh resource file!\n");
                *iconFamilyOut = NULL;
            }
        }
        // Attempt 4 - try to import from an 'icns' resource in a macbinary resource fork
        else if (icns_macbinary_header_check(dataSize, dataPtr)) {
            icns_size_t resourceSize;
            icns_byte_t * resourceData;
            #ifdef ICNS_DEBUG
            printf("Trying to find icns data in macbinary resource fork...\n");
            #endif
            if ((error = icns_read_macbinary_resource_fork(dataSize, dataPtr, NULL, NULL, & resourceSize,&resourceData)))
            {
                icns_print_err("icns_read_family_from_file: Error reading macbinary resource fork!\n");
                *iconFamilyOut = NULL;
            }

            if (error == 0) {
                if ((error = icns_find_family_in_mac_resource(resourceSize, resourceData, ICNS_BE_RSRC, iconFamilyOut))) {
                    icns_print_err("icns_read_family_from_file: Error reading icns data from macbinary resource fork!\n");
                    *iconFamilyOut = NULL;
                }
            }

            if (resourceData != NULL) {
                free(resourceData);
                resourceData = NULL;
            }
        }
        // Attempt 5 - try to import from an 'icns' resource in a apple encoded resource fork
        else if (icns_apple_encoded_header_check(dataSize, dataPtr)) {
            icns_size_t resourceSize;
            icns_byte_t * resourceData;
            #ifdef ICNS_DEBUG
            printf("Trying to find icns data in apple encoded resource fork...\n");
            #endif
            if ((error = icns_read_apple_encoded_resource_fork(dataSize, dataPtr, NULL, NULL, & resourceSize,&resourceData)))
            {
                icns_print_err("icns_read_family_from_file: Error reading macbinary resource fork!\n");
                *iconFamilyOut = NULL;
            }

            if (error == 0) {
                if ((error = icns_find_family_in_mac_resource(resourceSize, resourceData, ICNS_BE_RSRC, iconFamilyOut))) {
                    icns_print_err("icns_read_family_from_file: Error reading icns data from macbinary resource fork!\n");
                    *iconFamilyOut = NULL;
                }
            }

            if (resourceData != NULL) {
                free(resourceData);
                resourceData = NULL;
            }
        }
        // All attempts failed
        else {
            icns_print_err("icns_read_family_from_file: Error reading icns file - all parsing methods failed!\n");
            *iconFamilyOut = NULL;
            error = ICNS_STATUS_INVALID_DATA;
        }


        exception:

        if (dataPtr != NULL) {
            free(dataPtr);
            dataPtr = NULL;
        } */

        return null;
    }

    private static boolean icns_icns_header_check(int dataSize, ImageInputStream dataPtr) throws IOException {
        int resourceType = ICNS_NULL_TYPE;
        int resourceSize = 0;

        if (dataSize < 8)
            return false;

        if (dataPtr == null)
            return false;

        resourceType = dataPtr.readInt();
        resourceSize = dataPtr.readInt();

        if (resourceType != ICNS_FAMILY_TYPE)
            return false;

        if (dataSize != resourceSize)
            return false;

        return true;
    }

    private static icns_family_t icns_parse_family_data(ImageInputStream dataPtr) throws Exception {
        icns_family_t iconFamilyOut = new icns_family_t();

        iconFamilyOut.resourceType = dataPtr.readInt();
        iconFamilyOut.resourceSize = dataPtr.readInt();

        System.out.println("Reading icns family from data...");
        System.out.println(String.format(" resource type is '%s'", icns_type_str(iconFamilyOut.resourceType)));
        System.out.println(String.format(" resource size is %d", iconFamilyOut.resourceSize));

        // Skip past the icns header

        // Iterate through the icns resource, converting the 'type' and 'size' values to native endian
        icns_element_t element;

        while (dataPtr.getStreamPosition() < dataPtr.length()) {
            iconFamilyOut.elements.add(element = new icns_element_t());
            element.elementType = dataPtr.readInt();
            element.elementSize = dataPtr.readInt();
            element.elementData = new byte[element.elementSize - 8];

            System.out.println(String.format("  checking element type... type is '%s'", icns_type_str(element.elementType)));
            System.out.println(String.format("  checking element size... size is %d", element.elementSize));

            dataPtr.read(element.elementData);
        }

        return iconFamilyOut;
    }

    private static String icns_type_str(int v) {
        char strbuf[] = new char[5];
        strbuf[0] = (char)((v >> 24) & 0xFF);
        strbuf[1] = (char)((v >> 16) & 0xFF);
        strbuf[2] = (char)((v >> 8) & 0xFF);
        strbuf[3] = (char)(v & 0xFF);
        strbuf[4] = 0;
        return new String(strbuf);
    }

    public static icns_image_t icns_get_image32_with_mask_from_family(icns_family_t iconFamily, int iconType) throws Exception {
        icns_element_t iconElement;
        icns_image_t iconImage;
        int dataCount = 0;
        int dataValue = 0;
        long pixelCount = 0;
        int pixelID = 0;
        int colorIndex = 0;
//

        // Preliminaries checked - carry on with the icon/mask merge

        // Load icon element then image
        iconElement = icns_get_element_from_family(iconFamily, iconType);

        iconImage = icns_get_image_from_element(iconElement);

        // We used the jp2 processor for these two, so we're done!
        /*if ((iconType == ICNS_256x256_32BIT_ARGB_DATA) || (iconType == ICNS_512x512_32BIT_ARGB_DATA) ||
                (iconType == ICNS_1024x1024_32BIT_ARGB_DATA)) {
            memcpy(imageOut, & iconImage, sizeof(icns_image_t));
            if (iconElement != NULL) {
                free(iconElement);
                iconElement = NULL;
            }
            return error;
        } */

        int maskType = icns_get_mask_type_for_icon_type(iconType);

        System.out.println(String.format("  using mask type '%s'", icns_type_str(maskType)));

        if (maskType == ICNS_NULL_DATA)
            throw new Exception(String.format("icns_get_image32_with_mask_from_family: Can't find mask for type '%s'", icns_type_str(iconType)));

        // Load mask element then image...
        icns_element_t maskElement = icns_get_element_from_family(iconFamily, maskType);

        // Note that we could arguably recover from not having a mask
        // by creating a dummy blank mask. However, the icns data type
        // should always have the corresponding mask present. This
        // function was designed to retreive a VALID image... There are
        // other API functions better used if the goal is editing, data
        // recovery, etc.
        if (maskElement == null)
            return null;

        icns_image_t maskImage = icns_get_mask_from_element(maskElement);

        if (iconImage.imageWidth != maskImage.imageWidth)
            throw new Exception(String.format("icns_get_image32_with_mask_from_family: icon and mask widths do not match! (%d != %d)",
                    iconImage.imageWidth,
                    maskImage.imageHeight));

        if (iconImage.imageHeight != maskImage.imageHeight)
            throw new Exception(String.format("icns_get_image32_with_mask_from_family: icon and mask heights do not match! (%d != %d)",
                    iconImage.imageHeight,
                    maskImage.imageHeight));

        // Unpack image pixels if depth is < 32
        if ((iconImage.imagePixelDepth * iconImage.imageChannels) < 32) {
            byte[] oldData;
            byte[] newData;
            int oldBitDepth = 0;
            int newBlockSize = 0;
            int newDataSize = 0;
            icns_colormap_rgb_t colorRGB;

            oldBitDepth = (iconImage.imagePixelDepth * iconImage.imageChannels);

            pixelCount = iconImage.imageWidth * iconImage.imageHeight;

            newBlockSize = iconImage.imageWidth * 32;
            newDataSize = newBlockSize * iconImage.imageHeight;

            oldData = iconImage.imageData;
            newData = new byte[newDataSize];

            dataCount = 0;

            // 8-Bit Icon Image Data Types
            if ((iconType == ICNS_48x48_8BIT_DATA) || iconType == ICNS_32x32_8BIT_DATA || iconType == ICNS_16x16_8BIT_DATA ||
                    iconType == ICNS_16x12_8BIT_DATA) {
                if (oldBitDepth != 8)
                    throw new Exception("icns_get_image32_with_mask_from_family: Invalid bit depth - type mismatch!");
                for (pixelID = 0; pixelID < pixelCount; pixelID++) {
                    colorIndex = oldData[dataCount++];
                    colorRGB = icns_colormap_8[colorIndex < 0 ? 0 : colorIndex];
                    newData[pixelID * 4 + 0] = (byte)colorRGB.r;
                    newData[pixelID * 4 + 1] = (byte)colorRGB.g;
                    newData[pixelID * 4 + 2] = (byte)colorRGB.b;
                    newData[pixelID * 4 + 3] = (byte)0xff;
                }
            }
            // 4-Bit Icon Image Data Types
            else if ((iconType == ICNS_48x48_4BIT_DATA) || iconType == ICNS_32x32_4BIT_DATA || iconType == ICNS_16x16_4BIT_DATA ||
                    iconType == ICNS_16x12_4BIT_DATA) {
                if (oldBitDepth != 4)
                    throw new Exception("icns_get_image32_with_mask_from_family: Invalid bit depth - type mismatch!");

                for (pixelID = 0; pixelID < pixelCount; pixelID++) {
                    if (pixelID % 2 == 0)
                        dataValue = oldData[dataCount++];
                    colorIndex = (dataValue &= 0xF0) >> 4;
                    dataValue = dataValue << 4;
                    colorRGB = icns_colormap_4[colorIndex];
                    newData[pixelID * 4 + 0] = (byte)colorRGB.r;
                    newData[pixelID * 4 + 1] = (byte)colorRGB.g;
                    newData[pixelID * 4 + 2] = (byte)colorRGB.b;
                    newData[pixelID * 4 + 3] = (byte)0xFF;
                }
            }
            // 1-Bit Icon Image Data Types
            else if (iconType == ICNS_48x48_1BIT_DATA || iconType == ICNS_32x32_1BIT_DATA || iconType == ICNS_16x16_1BIT_DATA ||
                    iconType == ICNS_16x12_1BIT_DATA) {
                if (oldBitDepth != 1)
                    throw new Exception("icns_get_image32_with_mask_from_family: Invalid bit depth - type mismatch!");

                for (pixelID = 0; pixelID < pixelCount; pixelID++) {
                    if (pixelID % 8 == 0)
                        dataValue = oldData[dataCount++];
                    colorIndex = (dataValue & 0x80) != 0 ? 0x00 : 0xFF;
                    dataValue = dataValue << 1;
                    newData[pixelID * 4 + 0] = (byte)colorIndex;
                    newData[pixelID * 4 + 1] = (byte)colorIndex;
                    newData[pixelID * 4 + 2] = (byte)colorIndex;
                    newData[pixelID * 4 + 3] = (byte)0xFF;
                }
            } else
                throw new Exception(String.format("icns_get_image32_with_mask_from_family: Unpack error - unknown icon type! ('%s')",
                        icns_type_str(iconType)));

            iconImage.imagePixelDepth = 8;
            iconImage.imageChannels = 4;
            iconImage.imageDataSize = newDataSize;
            iconImage.imageData = newData;
        }

        // 8-Bit Icon Mask Data Types
        if (maskType == ICNS_128X128_8BIT_MASK || maskType == ICNS_48x48_8BIT_MASK || maskType == ICNS_32x32_8BIT_MASK ||
                maskType == ICNS_16x16_8BIT_MASK) {
            pixelCount = maskImage.imageWidth * maskImage.imageHeight;
            dataCount = 0;
            if ((maskImage.imagePixelDepth * maskImage.imageChannels) != 8)
                throw new Exception("icns_get_image32_with_mask_from_family: Invalid bit depth - mismatch!");
            for (pixelID = 0; pixelID < pixelCount; pixelID++) {
                iconImage.imageData[pixelID * 4 + 3] = maskImage.imageData[dataCount++];
            }
        }
        // 1-Bit Icon Mask Data Types
        else if (maskType == ICNS_48x48_1BIT_MASK || maskType == ICNS_32x32_1BIT_MASK || maskType == ICNS_16x16_1BIT_MASK ||
                maskType == ICNS_16x12_1BIT_MASK) {
            pixelCount = maskImage.imageWidth * maskImage.imageHeight;
            dataCount = 0;
            if ((maskImage.imagePixelDepth * maskImage.imageChannels) != 1)
                throw new Exception("icns_get_image32_with_mask_from_family: Invalid bit depth - mismatch!");

            for (pixelID = 0; pixelID < pixelCount; pixelID++) {
                if (pixelID % 8 == 0)
                    dataValue = maskImage.imageData[dataCount++];
                colorIndex = (dataValue & 0x80) != 0 ? 0xFF : 0x00;
                dataValue = dataValue << 1;
                iconImage.imageData[pixelID * 4 + 3] = (byte)colorIndex;
            }
        } else
            throw new Exception(String.format("icns_get_image32_with_mask_from_family: Unpack error - unknown mask type! ('%s')", icns_type_str(
                    maskType)));

        icns_image_t imageOut = iconImage;
        System.out.println("Finished 32-bit image...");
        System.out.println(String.format("  height: %d", imageOut.imageHeight));
        System.out.println(String.format("  width: %d", imageOut.imageHeight));
        System.out.println(String.format("  channels: %d", imageOut.imageChannels));
        System.out.println(String.format("  pixel depth: %d", imageOut.imagePixelDepth));
        System.out.println(String.format("  data size: %d", imageOut.imageDataSize));

        return imageOut;
    }

    private static icns_image_t icns_get_mask_from_element(icns_element_t maskElement) throws Exception {
        int elementType = maskElement.elementType;
        int elementSize = maskElement.elementSize;

        System.out.println("Retreiving image from mask element...");
        System.out.println(String.format("  type is: '%s'", icns_type_str(elementType)));
        System.out.println(String.format("  size is: %d", elementSize));

        int maskType = elementType;
        int rawDataSize = elementSize - 8;

        System.out.println(String.format("  data size is: %d", rawDataSize));

        switch (maskType) {
            case ICNS_128X128_8BIT_MASK:
            case ICNS_48x48_8BIT_MASK:
            case ICNS_32x32_8BIT_MASK:
            case ICNS_16x16_8BIT_MASK: {
                icns_image_t imageOut = icns_init_image_for_type(maskType);
                int maskBitDepth = imageOut.imagePixelDepth * imageOut.imageChannels;

                if (maskBitDepth != 8)
                    throw new Exception("icns_get_mask_from_element: Unknown bit depth!");

                imageOut.imageData = new byte[maskElement.elementData.length];

                for (int i = 0; i < maskElement.elementData.length; i++)
                    imageOut.imageData[i] = maskElement.elementData[i];
                return imageOut;
            }
            case ICNS_48x48_1BIT_MASK:
            case ICNS_32x32_1BIT_MASK:
            case ICNS_16x16_1BIT_MASK:
            case ICNS_16x12_1BIT_MASK: {
                icns_image_t imageOut = icns_init_image_for_type(maskType);
                int maskBitDepth = imageOut.imagePixelDepth * imageOut.imageChannels;

                if (maskBitDepth != 1)
                    throw new Exception("icns_get_mask_from_element: Unknown bit depth!");

                int maskDataSize = imageOut.imageDataSize;
                int maskDataRowSize = imageOut.imageWidth * maskBitDepth / ICNS_BYTE_BITS;

                System.out.println(String.format("  raw mask data size is: %d", rawDataSize));
                System.out.println(String.format("  image mask data size is: %d", maskDataSize));

                imageOut.imageData = new byte[imageOut.imageDataSize];

                if (rawDataSize == (maskDataSize * 2)) {
                    System.out.println("  mask data in second memory block");
                    // Mask data found - Copy the second block of memory
                    for (int dataCount = 0; dataCount < imageOut.imageHeight; dataCount++) {
                        memcpy(imageOut.imageData, maskElement.elementData, dataCount * maskDataRowSize, dataCount * maskDataRowSize, maskDataSize);
                        break;
                    }
                } else {
                    System.out.println("  using icon data from first memory block");
                    // Hmm, no mask - copy the first block of memory
                    for (int dataCount = 0; dataCount < imageOut.imageHeight; dataCount++)
                        memcpy(imageOut.imageData, maskElement.elementData, dataCount * maskDataRowSize, dataCount * maskDataRowSize,
                                maskDataRowSize);
                }

                return imageOut;
            }
//            default: {
//                char typeStr[ 5];
//                icns_print_err("icns_get_mask_from_element: Unknown mask type! ('%s')\n", icns_type_str(maskType, typeStr));
//                icns_free_image(imageOut);
//            }
//            return ICNS_STATUS_INVALID_DATA;
        }

        return null;
    }

    public static void memcpy(byte[] dest, byte[] src, int offsDest, int offsSrc, int size) {
        for (int i = 0; i < size; i++)
            dest[offsDest + i] = src[offsSrc + i];
    }

    private static int icns_get_mask_type_for_icon_type(int iconType) {
        switch (iconType) {
            // Obviously the TOC type has no mask
            case ICNS_TABLE_OF_CONTENTS:
                return ICNS_NULL_MASK;

            // Obviously the version type has no mask
            case ICNS_ICON_VERSION:
                return ICNS_NULL_MASK;

            // 32-bit image types > 256x256 - no mask (mask is already in image)
            case ICNS_1024x1024_32BIT_ARGB_DATA:
                return ICNS_NULL_MASK;
            case ICNS_512x512_32BIT_ARGB_DATA:
                return ICNS_NULL_MASK;
            case ICNS_256x256_32BIT_ARGB_DATA:
                return ICNS_NULL_MASK;

            // 32-bit image types - 8-bit mask type
            case ICNS_128X128_32BIT_DATA:
                return ICNS_128X128_8BIT_MASK;
            case ICNS_48x48_32BIT_DATA:
                return ICNS_48x48_8BIT_MASK;
            case ICNS_32x32_32BIT_DATA:
                return ICNS_32x32_8BIT_MASK;
            case ICNS_16x16_32BIT_DATA:
                return ICNS_16x16_8BIT_MASK;

            // 8-bit image types - 1-bit mask types
            case ICNS_48x48_8BIT_DATA:
                return ICNS_48x48_1BIT_MASK;
            case ICNS_32x32_8BIT_DATA:
                return ICNS_32x32_1BIT_MASK;
            case ICNS_16x16_8BIT_DATA:
                return ICNS_16x16_1BIT_MASK;
            case ICNS_16x12_8BIT_DATA:
                return ICNS_16x12_1BIT_MASK;

            // 4 bit image types - 1-bit mask types
            case ICNS_48x48_4BIT_DATA:
                return ICNS_48x48_1BIT_MASK;
            case ICNS_32x32_4BIT_DATA:
                return ICNS_32x32_1BIT_MASK;
            case ICNS_16x16_4BIT_DATA:
                return ICNS_16x16_1BIT_MASK;
            case ICNS_16x12_4BIT_DATA:
                return ICNS_16x12_1BIT_MASK;

            // 1 bit image types - 1-bit mask types
            case ICNS_48x48_1BIT_DATA:
                return ICNS_48x48_1BIT_MASK;
            case ICNS_32x32_1BIT_DATA:
                return ICNS_32x32_1BIT_MASK;
            case ICNS_16x16_1BIT_DATA:
                return ICNS_16x16_1BIT_MASK;
            case ICNS_16x12_1BIT_DATA:
                return ICNS_16x12_1BIT_MASK;
            default:
                return ICNS_NULL_MASK;
        }
    }


    private static icns_image_t icns_get_image_from_element(icns_element_t iconElement) throws Exception {
        icns_image_t imageOut = null;
//        int error = ICNS_STATUS_OK;
//        unsigned long dataCount = 0;
        int elementType = ICNS_NULL_TYPE;
        int elementSize = 0;
        int iconType = ICNS_NULL_TYPE;
        int rawDataSize = 0;
        int rawDataPtr = 0;
        int iconBitDepth = 0;
        long iconDataRowSize = 0;

        elementType = iconElement.elementType;
        elementSize = iconElement.elementSize;

        System.out.println("Retreiving image from icon element...");
        System.out.println(String.format("  type is: '%s'", icns_type_str(elementType)));
        System.out.println(String.format("  size is: %dn", elementSize));

        iconType = elementType;
        rawDataSize = elementSize - 8;
//        rawDataPtr = (icns_byte_t *) & (iconElement -> elementData[0]);

        System.out.println(String.format("  data size is: %d", rawDataSize));

        switch (iconType) {
            // 32-Bit Icon Image Data Types ( > 256px )
            case ICNS_256x256_32BIT_ARGB_DATA:
            case ICNS_512x512_32BIT_ARGB_DATA:
            case ICNS_1024x1024_32BIT_ARGB_DATA: {
                int magicPNG[] = { 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A };
                int magicByt[] = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

                int a = 0;
                a++;

//                ICNS_READ_UNALIGNED(magicByt[0], rawDataPtr, 8);
//
//                // 256x256+ sizes may or may not be PNG dta as of 10.7 Lion, so check
//                if (memcmp( & magicByt[0],&magicPNG[0], 8)==0){
//                    // We know to use the PNG processor
//                    error = icns_png_to_image((int)rawDataSize, (icns_byte_t *)rawDataPtr, imageOut);
//                }else{
//                    // We assume use of the jp2 processor
//                    error = icns_jp2_to_image((int)rawDataSize, (icns_byte_t *)rawDataPtr, imageOut);
//                }
//                return error;
            }
            case ICNS_128X128_32BIT_DATA:
            case ICNS_48x48_32BIT_DATA:
            case ICNS_32x32_32BIT_DATA:
            case ICNS_16x16_32BIT_DATA: {
                imageOut = icns_init_image_for_type(iconType);
                iconBitDepth = imageOut.imagePixelDepth * imageOut.imageChannels;
                iconDataRowSize = imageOut.imageWidth * iconBitDepth / ICNS_BYTE_BITS;

                if (rawDataSize < imageOut.imageDataSize) {
                    int pixelCount = 0;
                    int decodedDataSize = imageOut.imageDataSize;

                    pixelCount = imageOut.imageWidth * imageOut.imageHeight;
//                    imageOut.imageData = rle24.decompress(iconElement.elementData.length, iconElement.elementData, pixelCount, decodedDataSize);
                    imageOut.imageDataSize = imageOut.imageData.length;
                } else {
                    int a = 0;
                    a++;
//                    int pixelCount = 0;
//                    icns_byte_t * swapPtr = NULL;
//                    icns_argb_t * pixelPtr = NULL;
//
//                    for (int dataCount = 0; dataCount < imageOut.imageHeight; dataCount++)
//                        memcpy( & (((char*)(imageOut -> imageData))[dataCount * iconDataRowSize]),&(((char*)(rawDataPtr))[
//                    dataCount * iconDataRowSize]),iconDataRowSize);
//
//                    pixelCount = imageOut -> imageWidth * imageOut -> imageHeight;
//
//                    System.out.println(String.format("Converting %d pixels from argb to rgba", pixelCount));
//                            swapPtr = imageOut -> imageData;
//                    for (dataCount = 0; dataCount < pixelCount; dataCount++) {
//                        pixelPtr = (icns_argb_t *) (swapPtr + (dataCount * 4));
//                        *((icns_rgba_t *) pixelPtr)=ICNS_ARGB_TO_RGBA( * ((icns_argb_t *) pixelPtr));
//                    }
                }
                break;
            }
            case ICNS_48x48_8BIT_DATA:
            case ICNS_32x32_8BIT_DATA:
            case ICNS_16x16_8BIT_DATA:
            case ICNS_16x12_8BIT_DATA:
            case ICNS_48x48_4BIT_DATA:
            case ICNS_32x32_4BIT_DATA:
            case ICNS_16x16_4BIT_DATA:
            case ICNS_16x12_4BIT_DATA:
            case ICNS_48x48_1BIT_DATA:
            case ICNS_32x32_1BIT_DATA:
            case ICNS_16x16_1BIT_DATA:
            case ICNS_16x12_1BIT_DATA: {
                imageOut = icns_init_image_for_type(iconType);

                iconBitDepth = imageOut.imagePixelDepth * imageOut.imageChannels;
                iconDataRowSize = imageOut.imageWidth * iconBitDepth / ICNS_BYTE_BITS;
                imageOut.imageData = new byte[iconElement.elementData.length];

                for (int i = 0; i < iconElement.elementData.length; i++)
                    imageOut.imageData[i] = iconElement.elementData[i];

                break;
            }
            default: {
//                char typeStr[ 5];
//                icns_print_err("icns_get_image_from_element: Unknown icon type! ('%s')\n", icns_type_str(iconType, typeStr));
//                icns_free_image(imageOut);
            }
//            return ICNS_STATUS_INVALID_DATA;
        }

        return imageOut;
    }

    private static icns_image_t icns_init_image_for_type(int iconType) throws Exception {
        icns_icon_info_t iconInfo;

        // Determine what the height and width ought to be, to check the incoming image

        iconInfo = icns_get_image_info_for_type(iconType);

        if (iconType != iconInfo.iconType)
            throw new Exception(String.format("icns_init_image_for_type: Couldn't determine information for type! ('%s')", icns_type_str(iconType)));

        return icns_init_image(iconInfo.iconWidth, iconInfo.iconHeight, iconInfo.iconChannels, iconInfo.iconPixelDepth);
    }

    private static icns_image_t icns_init_image(int iconWidth, int iconHeight, int iconChannels, int iconPixelDepth) {
        icns_image_t imageOut = new icns_image_t();

        int iconBitDepth = iconPixelDepth * iconChannels;
        long iconDataRowSize = iconWidth * iconBitDepth / ICNS_BYTE_BITS;
        int iconDataSize = (int)(iconHeight * iconDataRowSize);

        System.out.println("Initializing new image...");
        System.out.println(String.format("  width is: %d", iconWidth));
        System.out.println(String.format("  height is: %d", iconHeight));
        System.out.println(String.format("  channels are: %d", iconChannels));
        System.out.println(String.format("  bit depth is: %d", iconBitDepth));
        System.out.println(String.format("  data size is: %d", iconDataSize));

        imageOut.imageWidth = iconWidth;
        imageOut.imageHeight = iconHeight;
        imageOut.imageChannels = iconChannels;
        imageOut.imagePixelDepth = (iconBitDepth / iconChannels);
        imageOut.imageDataSize = iconDataSize;

        return imageOut;
    }

    private static icns_icon_info_t icns_get_image_info_for_type(int iconType) {
        icns_icon_info_t iconInfo = new icns_icon_info_t();

        iconInfo.iconType = iconType;

        switch (iconType) {
            // TOC type
            case ICNS_TABLE_OF_CONTENTS:
                iconInfo.isImage = false;
                iconInfo.isMask = false;
                iconInfo.iconWidth = 0;
                iconInfo.iconHeight = 0;
                iconInfo.iconChannels = 0;
                iconInfo.iconPixelDepth = 0;
                iconInfo.iconBitDepth = 0;
                break;
            // Version type
            case ICNS_ICON_VERSION:
                iconInfo.isImage = false;
                iconInfo.isMask = false;
                iconInfo.iconWidth = 0;
                iconInfo.iconHeight = 0;
                iconInfo.iconChannels = 0;
                iconInfo.iconPixelDepth = 0;
                iconInfo.iconBitDepth = 0;
                break;
            // 32-bit image types
            case ICNS_1024x1024_32BIT_ARGB_DATA:
                iconInfo.isImage = true;
                iconInfo.isMask = false;
                iconInfo.iconWidth = 1024;
                iconInfo.iconHeight = 1024;
                iconInfo.iconChannels = 4;
                iconInfo.iconPixelDepth = 8;
                iconInfo.iconBitDepth = 32;
                break;
            case ICNS_512x512_32BIT_ARGB_DATA:
                iconInfo.isImage = true;
                iconInfo.isMask = false;
                iconInfo.iconWidth = 512;
                iconInfo.iconHeight = 512;
                iconInfo.iconChannels = 4;
                iconInfo.iconPixelDepth = 8;
                iconInfo.iconBitDepth = 32;
                break;
            case ICNS_256x256_32BIT_ARGB_DATA:
                iconInfo.isImage = true;
                iconInfo.isMask = false;
                iconInfo.iconWidth = 256;
                iconInfo.iconHeight = 256;
                iconInfo.iconChannels = 4;
                iconInfo.iconPixelDepth = 8;
                iconInfo.iconBitDepth = 32;
                break;
            case ICNS_128X128_32BIT_DATA:
                iconInfo.isImage = true;
                iconInfo.isMask = false;
                iconInfo.iconWidth = 128;
                iconInfo.iconHeight = 128;
                iconInfo.iconChannels = 4;
                iconInfo.iconPixelDepth = 8;
                iconInfo.iconBitDepth = 32;
                break;
            case ICNS_48x48_32BIT_DATA:
                iconInfo.isImage = true;
                iconInfo.isMask = false;
                iconInfo.iconWidth = 48;
                iconInfo.iconHeight = 48;
                iconInfo.iconChannels = 4;
                iconInfo.iconPixelDepth = 8;
                iconInfo.iconBitDepth = 32;
                break;
            case ICNS_32x32_32BIT_DATA:
                iconInfo.isImage = true;
                iconInfo.isMask = false;
                iconInfo.iconWidth = 32;
                iconInfo.iconHeight = 32;
                iconInfo.iconChannels = 4;
                iconInfo.iconPixelDepth = 8;
                iconInfo.iconBitDepth = 32;
                break;
            case ICNS_16x16_32BIT_DATA:
                iconInfo.isImage = true;
                iconInfo.isMask = false;
                iconInfo.iconWidth = 16;
                iconInfo.iconHeight = 16;
                iconInfo.iconChannels = 4;
                iconInfo.iconPixelDepth = 8;
                iconInfo.iconBitDepth = 32;
                break;

            // 8-bit mask types
            case ICNS_128X128_8BIT_MASK:
                iconInfo.isImage = false;
                iconInfo.isMask = true;
                iconInfo.iconWidth = 128;
                iconInfo.iconHeight = 128;
                iconInfo.iconChannels = 1;
                iconInfo.iconPixelDepth = 8;
                iconInfo.iconBitDepth = 8;
                break;
            case ICNS_48x48_8BIT_MASK:
                iconInfo.isImage = false;
                iconInfo.isMask = true;
                iconInfo.iconWidth = 48;
                iconInfo.iconHeight = 48;
                iconInfo.iconChannels = 1;
                iconInfo.iconPixelDepth = 8;
                iconInfo.iconBitDepth = 8;
                break;
            case ICNS_32x32_8BIT_MASK:
                iconInfo.isImage = false;
                iconInfo.isMask = true;
                iconInfo.iconWidth = 32;
                iconInfo.iconHeight = 32;
                iconInfo.iconChannels = 1;
                iconInfo.iconPixelDepth = 8;
                iconInfo.iconBitDepth = 8;
                break;
            case ICNS_16x16_8BIT_MASK:
                iconInfo.isImage = false;
                iconInfo.isMask = true;
                iconInfo.iconWidth = 16;
                iconInfo.iconHeight = 16;
                iconInfo.iconChannels = 1;
                iconInfo.iconPixelDepth = 8;
                iconInfo.iconBitDepth = 8;
                break;

            // 8-bit image types
            case ICNS_48x48_8BIT_DATA:
                iconInfo.isImage = true;
                iconInfo.isMask = false;
                iconInfo.iconWidth = 48;
                iconInfo.iconHeight = 48;
                iconInfo.iconChannels = 1;
                iconInfo.iconPixelDepth = 8;
                iconInfo.iconBitDepth = 8;
                break;
            case ICNS_32x32_8BIT_DATA:
                iconInfo.isImage = true;
                iconInfo.isMask = false;
                iconInfo.iconWidth = 32;
                iconInfo.iconHeight = 32;
                iconInfo.iconChannels = 1;
                iconInfo.iconPixelDepth = 8;
                iconInfo.iconBitDepth = 8;
                break;
            case ICNS_16x16_8BIT_DATA:
                iconInfo.isImage = true;
                iconInfo.isMask = false;
                iconInfo.iconWidth = 16;
                iconInfo.iconHeight = 16;
                iconInfo.iconChannels = 1;
                iconInfo.iconPixelDepth = 8;
                iconInfo.iconBitDepth = 8;
                break;
            case ICNS_16x12_8BIT_DATA:
                iconInfo.isImage = true;
                iconInfo.isMask = false;
                iconInfo.iconWidth = 16;
                iconInfo.iconHeight = 12;
                iconInfo.iconChannels = 1;
                iconInfo.iconPixelDepth = 8;
                iconInfo.iconBitDepth = 8;
                break;

            // 4 bit image types
            case ICNS_48x48_4BIT_DATA:
                iconInfo.isImage = true;
                iconInfo.isMask = false;
                iconInfo.iconWidth = 48;
                iconInfo.iconHeight = 48;
                iconInfo.iconChannels = 1;
                iconInfo.iconPixelDepth = 4;
                iconInfo.iconBitDepth = 4;
                break;
            case ICNS_32x32_4BIT_DATA:
                iconInfo.isImage = true;
                iconInfo.isMask = false;
                iconInfo.iconWidth = 32;
                iconInfo.iconHeight = 32;
                iconInfo.iconChannels = 1;
                iconInfo.iconPixelDepth = 4;
                iconInfo.iconBitDepth = 4;
                break;
            case ICNS_16x16_4BIT_DATA:
                iconInfo.isImage = true;
                iconInfo.isMask = false;
                iconInfo.iconWidth = 16;
                iconInfo.iconHeight = 16;
                iconInfo.iconChannels = 1;
                iconInfo.iconPixelDepth = 4;
                iconInfo.iconBitDepth = 4;
                break;
            case ICNS_16x12_4BIT_DATA:
                iconInfo.isImage = true;
                iconInfo.isMask = false;
                iconInfo.iconWidth = 16;
                iconInfo.iconHeight = 12;
                iconInfo.iconChannels = 1;
                iconInfo.iconPixelDepth = 4;
                iconInfo.iconBitDepth = 4;
                break;

            // 1 bit image types - same as mask typess
            case ICNS_48x48_1BIT_DATA:
                iconInfo.isImage = true;
                iconInfo.isMask = true;
                iconInfo.iconWidth = 48;
                iconInfo.iconHeight = 48;
                iconInfo.iconChannels = 1;
                iconInfo.iconPixelDepth = 1;
                iconInfo.iconBitDepth = 1;
                break;
            case ICNS_32x32_1BIT_DATA:
                iconInfo.isImage = true;
                iconInfo.isMask = true;
                iconInfo.iconWidth = 32;
                iconInfo.iconHeight = 32;
                iconInfo.iconChannels = 1;
                iconInfo.iconPixelDepth = 1;
                iconInfo.iconBitDepth = 1;
                break;
            case ICNS_16x16_1BIT_DATA:
                iconInfo.isImage = true;
                iconInfo.isMask = true;
                iconInfo.iconWidth = 16;
                iconInfo.iconHeight = 16;
                iconInfo.iconChannels = 1;
                iconInfo.iconPixelDepth = 1;
                iconInfo.iconBitDepth = 1;
                break;
            case ICNS_16x12_1BIT_DATA:
                iconInfo.isImage = true;
                iconInfo.isMask = true;
                iconInfo.iconWidth = 16;
                iconInfo.iconHeight = 12;
                iconInfo.iconChannels = 1;
                iconInfo.iconPixelDepth = 1;
                iconInfo.iconBitDepth = 1;
                break;
        }

        iconInfo.iconRawDataSize = iconInfo.iconHeight * iconInfo.iconWidth * iconInfo.iconBitDepth / ICNS_BYTE_BITS;

	/*
    #ifdef ICNS_DEBUG
	{
		char typeStr[5];
		printf("  type is: '%s'\n",icns_type_str(iconInfo.iconType));
		printf("  width is: %d\n",iconInfo.iconWidth);
		printf("  height is: %d\n",iconInfo.iconHeight);
		printf("  channels are: %d\n",iconInfo.iconChannels);
		printf("  pixel depth is: %d\n",iconInfo.iconPixelDepth);
		printf("  bit depth is: %d\n",iconInfo.iconBitDepth);
		printf("  data size is: %d\n",(int)iconInfo.iconRawDataSize);
	}
	#endif
	*/

        return iconInfo;
    }

    private static icns_element_t icns_get_element_from_family(icns_family_t iconFamily, int iconType) {
        int iconFamilyType = iconFamily.resourceType;
        int iconFamilySize = iconFamily.resourceSize;

        System.out.println(String.format("Looking for icon element of type: '%s'", icns_type_str(iconType)));
        System.out.println(String.format("  icon family type check: '%s'\n", icns_type_str(iconFamilyType)));
        System.out.println(String.format("  icon family size check: %d\n", iconFamilySize));

        for (icns_element_t element : iconFamily.elements)
            if (element.elementType == iconType)
                return element;

        return null;
    }

    public static final int ICNS_TABLE_OF_CONTENTS = 0x544F4320;  // "TOC "
    public static final int ICNS_ICON_VERSION = 0x69636E56;  // "icnV"

    public static final int ICNS_NULL_TYPE = 0x0;
    public static final int ICNS_NULL_MASK = 0x0;
    public static final int ICNS_NULL_DATA = 0x0;
    public static final int ICNS_FAMILY_TYPE = 0x69636E73; // "icns"

    public static final int ICNS_1024x1024_32BIT_ARGB_DATA = 0x69633130; // "ic10"

    public static final int ICNS_512x512_32BIT_ARGB_DATA = 0x69633039; // "ic09"
    public static final int ICNS_256x256_32BIT_ARGB_DATA = 0x69633038; // "ic08"

    public static final int ICNS_128X128_32BIT_DATA = 0x69743332; // "it32"
    public static final int ICNS_128X128_8BIT_MASK = 0x74386D6B; // "t8mk"

    public static final int ICNS_48x48_1BIT_DATA = 0x69636823; // "ich#"
    public static final int ICNS_48x48_4BIT_DATA = 0x69636834; // "ich4"
    public static final int ICNS_48x48_8BIT_DATA = 0x69636838; // "ich8"
    public static final int ICNS_48x48_32BIT_DATA = 0x69683332; // "ih32"
    public static final int ICNS_48x48_1BIT_MASK = 0x69636823; // "ich#"
    public static final int ICNS_48x48_8BIT_MASK = 0x68386D6B; // "h8mk"

    public static final int ICNS_32x32_1BIT_DATA = 0x49434E23; // "ICN#"
    public static final int ICNS_32x32_4BIT_DATA = 0x69636C34; // "icl4"
    public static final int ICNS_32x32_8BIT_DATA = 0x69636C38; // "icl8"
    public static final int ICNS_32x32_32BIT_DATA = 0x696C3332; // "il32"
    public static final int ICNS_32x32_1BIT_MASK = 0x49434E23; // "ICN#"
    public static final int ICNS_32x32_8BIT_MASK = 0x6C386D6B; // "l8mk"

    public static final int ICNS_16x16_1BIT_DATA = 0x69637323; // "ics#"
    public static final int ICNS_16x16_4BIT_DATA = 0x69637334; // "ics4"
    public static final int ICNS_16x16_8BIT_DATA = 0x69637338; // "ics8"
    public static final int ICNS_16x16_32BIT_DATA = 0x69733332; // "is32"
    public static final int ICNS_16x16_1BIT_MASK = 0x69637323; // "ics#"
    public static final int ICNS_16x16_8BIT_MASK = 0x73386D6B; // "s8mk"

    public static final int ICNS_16x12_1BIT_DATA = 0x69636D23; // "icm#"
    public static final int ICNS_16x12_4BIT_DATA = 0x69636D34; // "icm4"
    public static final int ICNS_16x12_1BIT_MASK = 0x69636D23; // "icm#"
    public static final int ICNS_16x12_8BIT_DATA = 0x69636D38; // "icm8"

    public static final int ICNS_BYTE_BITS = 8;

    public static final icns_colormap_rgb_t icns_colormap_4[] =
            {
                    new icns_colormap_rgb_t(0xFF, 0xFF, 0xFF),
                    new icns_colormap_rgb_t(0xFC, 0xF3, 0x05),
                    new icns_colormap_rgb_t(0xFF, 0x64, 0x02),
                    new icns_colormap_rgb_t(0xDD, 0x08, 0x06),
                    new icns_colormap_rgb_t(0xF2, 0x08, 0x84),
                    new icns_colormap_rgb_t(0x46, 0x00, 0xA5),
                    new icns_colormap_rgb_t(0x00, 0x00, 0xD4),
                    new icns_colormap_rgb_t(0x02, 0xAB, 0xEA),
                    new icns_colormap_rgb_t(0x1F, 0xB7, 0x14),
                    new icns_colormap_rgb_t(0x00, 0x64, 0x11),
                    new icns_colormap_rgb_t(0x56, 0x2C, 0x05),
                    new icns_colormap_rgb_t(0x90, 0x71, 0x3A),
                    new icns_colormap_rgb_t(0xC0, 0xC0, 0xC0),
                    new icns_colormap_rgb_t(0x80, 0x80, 0x80),
                    new icns_colormap_rgb_t(0x40, 0x40, 0x40),
                    new icns_colormap_rgb_t(0x00, 0x00, 0x00)
            };


    public static final icns_colormap_rgb_t icns_colormap_8[] =
            {
                    new icns_colormap_rgb_t(0xFF, 0xFF, 0xFF),
                    new icns_colormap_rgb_t(0xFF, 0xFF, 0xCC),
                    new icns_colormap_rgb_t(0xFF, 0xFF, 0x99),
                    new icns_colormap_rgb_t(0xFF, 0xFF, 0x66),
                    new icns_colormap_rgb_t(0xFF, 0xFF, 0x33),
                    new icns_colormap_rgb_t(0xFF, 0xFF, 0x00),
                    new icns_colormap_rgb_t(0xFF, 0xCC, 0xFF),
                    new icns_colormap_rgb_t(0xFF, 0xCC, 0xCC),
                    new icns_colormap_rgb_t(0xFF, 0xCC, 0x99),
                    new icns_colormap_rgb_t(0xFF, 0xCC, 0x66),
                    new icns_colormap_rgb_t(0xFF, 0xCC, 0x33),
                    new icns_colormap_rgb_t(0xFF, 0xCC, 0x00),
                    new icns_colormap_rgb_t(0xFF, 0x99, 0xFF),
                    new icns_colormap_rgb_t(0xFF, 0x99, 0xCC),
                    new icns_colormap_rgb_t(0xFF, 0x99, 0x99),
                    new icns_colormap_rgb_t(0xFF, 0x99, 0x66),
                    new icns_colormap_rgb_t(0xFF, 0x99, 0x33),
                    new icns_colormap_rgb_t(0xFF, 0x99, 0x00),
                    new icns_colormap_rgb_t(0xFF, 0x66, 0xFF),
                    new icns_colormap_rgb_t(0xFF, 0x66, 0xCC),
                    new icns_colormap_rgb_t(0xFF, 0x66, 0x99),
                    new icns_colormap_rgb_t(0xFF, 0x66, 0x66),
                    new icns_colormap_rgb_t(0xFF, 0x66, 0x33),
                    new icns_colormap_rgb_t(0xFF, 0x66, 0x00),
                    new icns_colormap_rgb_t(0xFF, 0x33, 0xFF),
                    new icns_colormap_rgb_t(0xFF, 0x33, 0xCC),
                    new icns_colormap_rgb_t(0xFF, 0x33, 0x99),
                    new icns_colormap_rgb_t(0xFF, 0x33, 0x66),
                    new icns_colormap_rgb_t(0xFF, 0x33, 0x33),
                    new icns_colormap_rgb_t(0xFF, 0x33, 0x00),
                    new icns_colormap_rgb_t(0xFF, 0x00, 0xFF),
                    new icns_colormap_rgb_t(0xFF, 0x00, 0xCC),
                    new icns_colormap_rgb_t(0xFF, 0x00, 0x99),
                    new icns_colormap_rgb_t(0xFF, 0x00, 0x66),
                    new icns_colormap_rgb_t(0xFF, 0x00, 0x33),
                    new icns_colormap_rgb_t(0xFF, 0x00, 0x00),
                    new icns_colormap_rgb_t(0xCC, 0xFF, 0xFF),
                    new icns_colormap_rgb_t(0xCC, 0xFF, 0xCC),
                    new icns_colormap_rgb_t(0xCC, 0xFF, 0x99),
                    new icns_colormap_rgb_t(0xCC, 0xFF, 0x66),
                    new icns_colormap_rgb_t(0xCC, 0xFF, 0x33),
                    new icns_colormap_rgb_t(0xCC, 0xFF, 0x00),
                    new icns_colormap_rgb_t(0xCC, 0xCC, 0xFF),
                    new icns_colormap_rgb_t(0xCC, 0xCC, 0xCC),
                    new icns_colormap_rgb_t(0xCC, 0xCC, 0x99),
                    new icns_colormap_rgb_t(0xCC, 0xCC, 0x66),
                    new icns_colormap_rgb_t(0xCC, 0xCC, 0x33),
                    new icns_colormap_rgb_t(0xCC, 0xCC, 0x00),
                    new icns_colormap_rgb_t(0xCC, 0x99, 0xFF),
                    new icns_colormap_rgb_t(0xCC, 0x99, 0xCC),
                    new icns_colormap_rgb_t(0xCC, 0x99, 0x99),
                    new icns_colormap_rgb_t(0xCC, 0x99, 0x66),
                    new icns_colormap_rgb_t(0xCC, 0x99, 0x33),
                    new icns_colormap_rgb_t(0xCC, 0x99, 0x00),
                    new icns_colormap_rgb_t(0xCC, 0x66, 0xFF),
                    new icns_colormap_rgb_t(0xCC, 0x66, 0xCC),
                    new icns_colormap_rgb_t(0xCC, 0x66, 0x99),
                    new icns_colormap_rgb_t(0xCC, 0x66, 0x66),
                    new icns_colormap_rgb_t(0xCC, 0x66, 0x33),
                    new icns_colormap_rgb_t(0xCC, 0x66, 0x00),
                    new icns_colormap_rgb_t(0xCC, 0x33, 0xFF),
                    new icns_colormap_rgb_t(0xCC, 0x33, 0xCC),
                    new icns_colormap_rgb_t(0xCC, 0x33, 0x99),
                    new icns_colormap_rgb_t(0xCC, 0x33, 0x66),
                    new icns_colormap_rgb_t(0xCC, 0x33, 0x33),
                    new icns_colormap_rgb_t(0xCC, 0x33, 0x00),
                    new icns_colormap_rgb_t(0xCC, 0x00, 0xFF),
                    new icns_colormap_rgb_t(0xCC, 0x00, 0xCC),
                    new icns_colormap_rgb_t(0xCC, 0x00, 0x99),
                    new icns_colormap_rgb_t(0xCC, 0x00, 0x66),
                    new icns_colormap_rgb_t(0xCC, 0x00, 0x33),
                    new icns_colormap_rgb_t(0xCC, 0x00, 0x00),
                    new icns_colormap_rgb_t(0x99, 0xFF, 0xFF),
                    new icns_colormap_rgb_t(0x99, 0xFF, 0xCC),
                    new icns_colormap_rgb_t(0x99, 0xFF, 0x99),
                    new icns_colormap_rgb_t(0x99, 0xFF, 0x66),
                    new icns_colormap_rgb_t(0x99, 0xFF, 0x33),
                    new icns_colormap_rgb_t(0x99, 0xFF, 0x00),
                    new icns_colormap_rgb_t(0x99, 0xCC, 0xFF),
                    new icns_colormap_rgb_t(0x99, 0xCC, 0xCC),
                    new icns_colormap_rgb_t(0x99, 0xCC, 0x99),
                    new icns_colormap_rgb_t(0x99, 0xCC, 0x66),
                    new icns_colormap_rgb_t(0x99, 0xCC, 0x33),
                    new icns_colormap_rgb_t(0x99, 0xCC, 0x00),
                    new icns_colormap_rgb_t(0x99, 0x99, 0xFF),
                    new icns_colormap_rgb_t(0x99, 0x99, 0xCC),
                    new icns_colormap_rgb_t(0x99, 0x99, 0x99),
                    new icns_colormap_rgb_t(0x99, 0x99, 0x66),
                    new icns_colormap_rgb_t(0x99, 0x99, 0x33),
                    new icns_colormap_rgb_t(0x99, 0x99, 0x00),
                    new icns_colormap_rgb_t(0x99, 0x66, 0xFF),
                    new icns_colormap_rgb_t(0x99, 0x66, 0xCC),
                    new icns_colormap_rgb_t(0x99, 0x66, 0x99),
                    new icns_colormap_rgb_t(0x99, 0x66, 0x66),
                    new icns_colormap_rgb_t(0x99, 0x66, 0x33),
                    new icns_colormap_rgb_t(0x99, 0x66, 0x00),
                    new icns_colormap_rgb_t(0x99, 0x33, 0xFF),
                    new icns_colormap_rgb_t(0x99, 0x33, 0xCC),
                    new icns_colormap_rgb_t(0x99, 0x33, 0x99),
                    new icns_colormap_rgb_t(0x99, 0x33, 0x66),
                    new icns_colormap_rgb_t(0x99, 0x33, 0x33),
                    new icns_colormap_rgb_t(0x99, 0x33, 0x00),
                    new icns_colormap_rgb_t(0x99, 0x00, 0xFF),
                    new icns_colormap_rgb_t(0x99, 0x00, 0xCC),
                    new icns_colormap_rgb_t(0x99, 0x00, 0x99),
                    new icns_colormap_rgb_t(0x99, 0x00, 0x66),
                    new icns_colormap_rgb_t(0x99, 0x00, 0x33),
                    new icns_colormap_rgb_t(0x99, 0x00, 0x00),
                    new icns_colormap_rgb_t(0x66, 0xFF, 0xFF),
                    new icns_colormap_rgb_t(0x66, 0xFF, 0xCC),
                    new icns_colormap_rgb_t(0x66, 0xFF, 0x99),
                    new icns_colormap_rgb_t(0x66, 0xFF, 0x66),
                    new icns_colormap_rgb_t(0x66, 0xFF, 0x33),
                    new icns_colormap_rgb_t(0x66, 0xFF, 0x00),
                    new icns_colormap_rgb_t(0x66, 0xCC, 0xFF),
                    new icns_colormap_rgb_t(0x66, 0xCC, 0xCC),
                    new icns_colormap_rgb_t(0x66, 0xCC, 0x99),
                    new icns_colormap_rgb_t(0x66, 0xCC, 0x66),
                    new icns_colormap_rgb_t(0x66, 0xCC, 0x33),
                    new icns_colormap_rgb_t(0x66, 0xCC, 0x00),
                    new icns_colormap_rgb_t(0x66, 0x99, 0xFF),
                    new icns_colormap_rgb_t(0x66, 0x99, 0xCC),
                    new icns_colormap_rgb_t(0x66, 0x99, 0x99),
                    new icns_colormap_rgb_t(0x66, 0x99, 0x66),
                    new icns_colormap_rgb_t(0x66, 0x99, 0x33),
                    new icns_colormap_rgb_t(0x66, 0x99, 0x00),
                    new icns_colormap_rgb_t(0x66, 0x66, 0xFF),
                    new icns_colormap_rgb_t(0x66, 0x66, 0xCC),
                    new icns_colormap_rgb_t(0x66, 0x66, 0x99),
                    new icns_colormap_rgb_t(0x66, 0x66, 0x66),
                    new icns_colormap_rgb_t(0x66, 0x66, 0x33),
                    new icns_colormap_rgb_t(0x66, 0x66, 0x00),
                    new icns_colormap_rgb_t(0x66, 0x33, 0xFF),
                    new icns_colormap_rgb_t(0x66, 0x33, 0xCC),
                    new icns_colormap_rgb_t(0x66, 0x33, 0x99),
                    new icns_colormap_rgb_t(0x66, 0x33, 0x66),
                    new icns_colormap_rgb_t(0x66, 0x33, 0x33),
                    new icns_colormap_rgb_t(0x66, 0x33, 0x00),
                    new icns_colormap_rgb_t(0x66, 0x00, 0xFF),
                    new icns_colormap_rgb_t(0x66, 0x00, 0xCC),
                    new icns_colormap_rgb_t(0x66, 0x00, 0x99),
                    new icns_colormap_rgb_t(0x66, 0x00, 0x66),
                    new icns_colormap_rgb_t(0x66, 0x00, 0x33),
                    new icns_colormap_rgb_t(0x66, 0x00, 0x00),
                    new icns_colormap_rgb_t(0x33, 0xFF, 0xFF),
                    new icns_colormap_rgb_t(0x33, 0xFF, 0xCC),
                    new icns_colormap_rgb_t(0x33, 0xFF, 0x99),
                    new icns_colormap_rgb_t(0x33, 0xFF, 0x66),
                    new icns_colormap_rgb_t(0x33, 0xFF, 0x33),
                    new icns_colormap_rgb_t(0x33, 0xFF, 0x00),
                    new icns_colormap_rgb_t(0x33, 0xCC, 0xFF),
                    new icns_colormap_rgb_t(0x33, 0xCC, 0xCC),
                    new icns_colormap_rgb_t(0x33, 0xCC, 0x99),
                    new icns_colormap_rgb_t(0x33, 0xCC, 0x66),
                    new icns_colormap_rgb_t(0x33, 0xCC, 0x33),
                    new icns_colormap_rgb_t(0x33, 0xCC, 0x00),
                    new icns_colormap_rgb_t(0x33, 0x99, 0xFF),
                    new icns_colormap_rgb_t(0x33, 0x99, 0xCC),
                    new icns_colormap_rgb_t(0x33, 0x99, 0x99),
                    new icns_colormap_rgb_t(0x33, 0x99, 0x66),
                    new icns_colormap_rgb_t(0x33, 0x99, 0x33),
                    new icns_colormap_rgb_t(0x33, 0x99, 0x00),
                    new icns_colormap_rgb_t(0x33, 0x66, 0xFF),
                    new icns_colormap_rgb_t(0x33, 0x66, 0xCC),
                    new icns_colormap_rgb_t(0x33, 0x66, 0x99),
                    new icns_colormap_rgb_t(0x33, 0x66, 0x66),
                    new icns_colormap_rgb_t(0x33, 0x66, 0x33),
                    new icns_colormap_rgb_t(0x33, 0x66, 0x00),
                    new icns_colormap_rgb_t(0x33, 0x33, 0xFF),
                    new icns_colormap_rgb_t(0x33, 0x33, 0xCC),
                    new icns_colormap_rgb_t(0x33, 0x33, 0x99),
                    new icns_colormap_rgb_t(0x33, 0x33, 0x66),
                    new icns_colormap_rgb_t(0x33, 0x33, 0x33),
                    new icns_colormap_rgb_t(0x33, 0x33, 0x00),
                    new icns_colormap_rgb_t(0x33, 0x00, 0xFF),
                    new icns_colormap_rgb_t(0x33, 0x00, 0xCC),
                    new icns_colormap_rgb_t(0x33, 0x00, 0x99),
                    new icns_colormap_rgb_t(0x33, 0x00, 0x66),
                    new icns_colormap_rgb_t(0x33, 0x00, 0x33),
                    new icns_colormap_rgb_t(0x33, 0x00, 0x00),
                    new icns_colormap_rgb_t(0x00, 0xFF, 0xFF),
                    new icns_colormap_rgb_t(0x00, 0xFF, 0xCC),
                    new icns_colormap_rgb_t(0x00, 0xFF, 0x99),
                    new icns_colormap_rgb_t(0x00, 0xFF, 0x66),
                    new icns_colormap_rgb_t(0x00, 0xFF, 0x33),
                    new icns_colormap_rgb_t(0x00, 0xFF, 0x00),
                    new icns_colormap_rgb_t(0x00, 0xCC, 0xFF),
                    new icns_colormap_rgb_t(0x00, 0xCC, 0xCC),
                    new icns_colormap_rgb_t(0x00, 0xCC, 0x99),
                    new icns_colormap_rgb_t(0x00, 0xCC, 0x66),
                    new icns_colormap_rgb_t(0x00, 0xCC, 0x33),
                    new icns_colormap_rgb_t(0x00, 0xCC, 0x00),
                    new icns_colormap_rgb_t(0x00, 0x99, 0xFF),
                    new icns_colormap_rgb_t(0x00, 0x99, 0xCC),
                    new icns_colormap_rgb_t(0x00, 0x99, 0x99),
                    new icns_colormap_rgb_t(0x00, 0x99, 0x66),
                    new icns_colormap_rgb_t(0x00, 0x99, 0x33),
                    new icns_colormap_rgb_t(0x00, 0x99, 0x00),
                    new icns_colormap_rgb_t(0x00, 0x66, 0xFF),
                    new icns_colormap_rgb_t(0x00, 0x66, 0xCC),
                    new icns_colormap_rgb_t(0x00, 0x66, 0x99),
                    new icns_colormap_rgb_t(0x00, 0x66, 0x66),
                    new icns_colormap_rgb_t(0x00, 0x66, 0x33),
                    new icns_colormap_rgb_t(0x00, 0x66, 0x00),
                    new icns_colormap_rgb_t(0x00, 0x33, 0xFF),
                    new icns_colormap_rgb_t(0x00, 0x33, 0xCC),
                    new icns_colormap_rgb_t(0x00, 0x33, 0x99),
                    new icns_colormap_rgb_t(0x00, 0x33, 0x66),
                    new icns_colormap_rgb_t(0x00, 0x33, 0x33),
                    new icns_colormap_rgb_t(0x00, 0x33, 0x00),
                    new icns_colormap_rgb_t(0x00, 0x00, 0xFF),
                    new icns_colormap_rgb_t(0x00, 0x00, 0xCC),
                    new icns_colormap_rgb_t(0x00, 0x00, 0x99),
                    new icns_colormap_rgb_t(0x00, 0x00, 0x66),
                    new icns_colormap_rgb_t(0x00, 0x00, 0x33),
                    new icns_colormap_rgb_t(0xEE, 0x00, 0x00),
                    new icns_colormap_rgb_t(0xDD, 0x00, 0x00),
                    new icns_colormap_rgb_t(0xBB, 0x00, 0x00),
                    new icns_colormap_rgb_t(0xAA, 0x00, 0x00),
                    new icns_colormap_rgb_t(0x88, 0x00, 0x00),
                    new icns_colormap_rgb_t(0x77, 0x00, 0x00),
                    new icns_colormap_rgb_t(0x55, 0x00, 0x00),
                    new icns_colormap_rgb_t(0x44, 0x00, 0x00),
                    new icns_colormap_rgb_t(0x22, 0x00, 0x00),
                    new icns_colormap_rgb_t(0x11, 0x00, 0x00),
                    new icns_colormap_rgb_t(0x00, 0xEE, 0x00),
                    new icns_colormap_rgb_t(0x00, 0xDD, 0x00),
                    new icns_colormap_rgb_t(0x00, 0xBB, 0x00),
                    new icns_colormap_rgb_t(0x00, 0xAA, 0x00),
                    new icns_colormap_rgb_t(0x00, 0x88, 0x00),
                    new icns_colormap_rgb_t(0x00, 0x77, 0x00),
                    new icns_colormap_rgb_t(0x00, 0x55, 0x00),
                    new icns_colormap_rgb_t(0x00, 0x44, 0x00),
                    new icns_colormap_rgb_t(0x00, 0x22, 0x00),
                    new icns_colormap_rgb_t(0x00, 0x11, 0x00),
                    new icns_colormap_rgb_t(0x00, 0x00, 0xEE),
                    new icns_colormap_rgb_t(0x00, 0x00, 0xDD),
                    new icns_colormap_rgb_t(0x00, 0x00, 0xBB),
                    new icns_colormap_rgb_t(0x00, 0x00, 0xAA),
                    new icns_colormap_rgb_t(0x00, 0x00, 0x88),
                    new icns_colormap_rgb_t(0x00, 0x00, 0x77),
                    new icns_colormap_rgb_t(0x00, 0x00, 0x55),
                    new icns_colormap_rgb_t(0x00, 0x00, 0x44),
                    new icns_colormap_rgb_t(0x00, 0x00, 0x22),
                    new icns_colormap_rgb_t(0x00, 0x00, 0x11),
                    new icns_colormap_rgb_t(0xEE, 0xEE, 0xEE),
                    new icns_colormap_rgb_t(0xDD, 0xDD, 0xDD),
                    new icns_colormap_rgb_t(0xBB, 0xBB, 0xBB),
                    new icns_colormap_rgb_t(0xAA, 0xAA, 0xAA),
                    new icns_colormap_rgb_t(0x88, 0x88, 0x88),
                    new icns_colormap_rgb_t(0x77, 0x77, 0x77),
                    new icns_colormap_rgb_t(0x55, 0x55, 0x55),
                    new icns_colormap_rgb_t(0x44, 0x44, 0x44),
                    new icns_colormap_rgb_t(0x22, 0x22, 0x22),
                    new icns_colormap_rgb_t(0x11, 0x11, 0x11),
                    new icns_colormap_rgb_t(0x00, 0x00, 0x00)
            };
}
