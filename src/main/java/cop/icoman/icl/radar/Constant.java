package cop.icoman.icl.radar;

/**
 * @author Oleg Cherednik
 * @since 14.10.2016
 */
public final class Constant {
    public static final int PE_NAME_LENGTH = 256;
    public static final int PE_STRING_LENGTH = 256;

    public static final int PE_IMAGE_SIZEOF_SHORT_NAME = 8;

    public static final int PE_IMAGE_SCN_MEM_SHARED = 0x10000000;
    public static final int PE_IMAGE_SCN_MEM_EXECUTE = 0x20000000;
    public static final int PE_IMAGE_SCN_MEM_READ = 0x40000000;
    public static final int PE_IMAGE_SCN_MEM_WRITE = 0x80000000;

    public static final int PE_IMAGE_DIRECTORY_ENTRIES = 16;
    public static final int PE_IMAGE_DIRECTORY_ENTRY_EXPORT = 0;
    public static final int PE_IMAGE_DIRECTORY_ENTRY_IMPORT = 1;
    public static final int PE_IMAGE_DIRECTORY_ENTRY_RESOURCE = 2;
    public static final int PE_IMAGE_DIRECTORY_ENTRY_EXCEPTION = 3;
    public static final int PE_IMAGE_DIRECTORY_ENTRY_SECURITY = 4;
    public static final int PE_IMAGE_DIRECTORY_ENTRY_BASERELOC = 5;
    public static final int PE_IMAGE_DIRECTORY_ENTRY_DEBUG = 6;
    public static final int PE_IMAGE_DIRECTORY_ENTRY_COPYRIGHT = 7;
    public static final int PE_IMAGE_DIRECTORY_ENTRY_ARCHITECTURE = 7;
    public static final int PE_IMAGE_DIRECTORY_ENTRY_GLOBALPTR = 8;
    public static final int PE_IMAGE_DIRECTORY_ENTRY_TLS = 9;
    public static final int PE_IMAGE_DIRECTORY_ENTRY_LOAD_CONFIG = 10;
    public static final int PE_IMAGE_DIRECTORY_ENTRY_BOUND_IMPORT = 11;
    public static final int PE_IMAGE_DIRECTORY_ENTRY_IAT = 12;
    public static final int PE_IMAGE_DIRECTORY_ENTRY_DELAY_IMPORT = 13;
    public static final int PE_IMAGE_DIRECTORY_ENTRY_COM_DESCRIPTOR = 14;

    public static final int PE_IMAGE_FILE_MACHINE_UNKNOWN = 0x0000;
    public static final int PE_IMAGE_FILE_MACHINE_ALPHA = 0x0184;
    public static final int PE_IMAGE_FILE_MACHINE_ALPHA64 = 0x0284;
    public static final int PE_IMAGE_FILE_MACHINE_AM33 = 0x01d3;
    public static final int PE_IMAGE_FILE_MACHINE_AMD64 = 0x8664;
    public static final int PE_IMAGE_FILE_MACHINE_ARM = 0x01c0;
    public static final int PE_IMAGE_FILE_MACHINE_AXP64 = PE_IMAGE_FILE_MACHINE_ALPHA64;
    public static final int PE_IMAGE_FILE_MACHINE_CEE = 0xc0ee;
    public static final int PE_IMAGE_FILE_MACHINE_CEF = 0x0cef;
    public static final int PE_IMAGE_FILE_MACHINE_EBC = 0x0ebc;
    public static final int PE_IMAGE_FILE_MACHINE_I386 = 0x014c;
    public static final int PE_IMAGE_FILE_MACHINE_IA64 = 0x0200;
    public static final int PE_IMAGE_FILE_MACHINE_M32R = 0x9041;
    public static final int PE_IMAGE_FILE_MACHINE_M68K = 0x0268;
    public static final int PE_IMAGE_FILE_MACHINE_MIPS16 = 0x0266;
    public static final int PE_IMAGE_FILE_MACHINE_MIPSFPU = 0x0366;
    public static final int PE_IMAGE_FILE_MACHINE_MIPSFPU16 = 0x0466;
    public static final int PE_IMAGE_FILE_MACHINE_POWERPC = 0x01f0;
    public static final int PE_IMAGE_FILE_MACHINE_POWERPCFP = 0x01f1;
    public static final int PE_IMAGE_FILE_MACHINE_R10000 = 0x0168;
    public static final int PE_IMAGE_FILE_MACHINE_R3000 = 0x0162;
    public static final int PE_IMAGE_FILE_MACHINE_R4000 = 0x0166;
    public static final int PE_IMAGE_FILE_MACHINE_SH3 = 0x01a2;
    public static final int PE_IMAGE_FILE_MACHINE_SH3DSP = 0x01a3;
    public static final int PE_IMAGE_FILE_MACHINE_SH3E = 0x01a4;
    public static final int PE_IMAGE_FILE_MACHINE_SH4 = 0x01a6;
    public static final int PE_IMAGE_FILE_MACHINE_SH5 = 0x01a8;
    public static final int PE_IMAGE_FILE_MACHINE_THUMB = 0x01c2;
    public static final int PE_IMAGE_FILE_MACHINE_TRICORE = 0x0520;
    public static final int PE_IMAGE_FILE_MACHINE_WCEMIPSV2 = 0x0169;

    public static final int PE_RESOURCE_ENTRY_CURSOR = 1;
    public static final int PE_RESOURCE_ENTRY_BITMAP = 2;
    public static final int PE_RESOURCE_ENTRY_ICON = 3;
    public static final int PE_RESOURCE_ENTRY_MENU = 4;
    public static final int PE_RESOURCE_ENTRY_DIALOG = 5;
    public static final int PE_RESOURCE_ENTRY_STRING = 6;
    public static final int PE_RESOURCE_ENTRY_FONTDIR = 7;
    public static final int PE_RESOURCE_ENTRY_FONT = 8;
    public static final int PE_RESOURCE_ENTRY_ACCELERATOR = 9;
    public static final int PE_RESOURCE_ENTRY_RCDATA = 10;
    public static final int PE_RESOURCE_ENTRY_MESSAGETABLE = 11;
    public static final int PE_RESOURCE_ENTRY_GROUP_CURSOR = 12;
    public static final int PE_RESOURCE_ENTRY_GROUP_ICON = 14;
    public static final int PE_RESOURCE_ENTRY_VERSION = 16;
    public static final int PE_RESOURCE_ENTRY_DLGINCLUDE = 17;
    public static final int PE_RESOURCE_ENTRY_PLUGPLAY = 19;
    public static final int PE_RESOURCE_ENTRY_VXD = 20;
    public static final int PE_RESOURCE_ENTRY_ANICURSOR = 21;
    public static final int PE_RESOURCE_ENTRY_ANIICON = 22;
    public static final int PE_RESOURCE_ENTRY_HTML = 23;
    public static final int PE_RESOURCE_ENTRY_MANIFEST = 24;

    public static final int PE_IMAGE_FILE_MACHINE_RPI2 = 452;

    private Constant() {
    }
}
