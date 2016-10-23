package cop.icoman.icl.radar;

import cop.icoman.icl.radar.struct.Pe64_image_data_directory;
import cop.icoman.icl.radar.struct.Pe64_image_nt_headers;
import cop.icoman.icl.radar.struct.Pe64_image_section_header;
import cop.icoman.icl.radar.struct.Pe64_r_bin_pe_obj_t;
import cop.icoman.icl.radar.struct.Pe_image_resource_directory;
import cop.icoman.icl.radar.struct.Pe_image_resource_directory_entry;
import cop.icoman.icl.radar.struct.r_bin_pe_addr_t;
import cop.icoman.icl.radar.struct.r_bin_pe_section_t;

import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static cop.icoman.icl.radar.Constant.PE_IMAGE_DIRECTORY_ENTRY_RESOURCE;
import static cop.icoman.icl.radar.Constant.PE_IMAGE_FILE_MACHINE_ARM;
import static cop.icoman.icl.radar.Constant.PE_IMAGE_FILE_MACHINE_RPI2;
import static cop.icoman.icl.radar.Constant.PE_IMAGE_FILE_MACHINE_THUMB;
import static cop.icoman.icl.radar.Constant.PE_IMAGE_SCN_MEM_EXECUTE;
import static cop.icoman.icl.radar.Constant.PE_IMAGE_SCN_MEM_WRITE;

/**
 * @author Oleg Cherednik
 * @since 14.10.2016
 */
public class Radar {
    public static int ut8(byte[] buf, int offs) {
        return buf[offs] & 0xFF;
    }

    public static int ut16(byte[] buf, int offs) {
        return (ut8(buf, offs + 1) << 8) | ut8(buf, offs);
    }

    public static int[] ut16(byte[] buf, int offs, int total) {
        int[] arr = new int[total];

        for (int i = 0; i < arr.length; i++, offs += 2)
            arr[i] = ut16(buf, offs);

        return arr;
    }

    public static long ut32(byte[] buf, int offs) {
        long low = ut16(buf, offs);
        int high = ut16(buf, offs + 2);
        return (high << 16) + low;
    }

    public static long ut64(byte[] buf, int offs) {
        return ut32(buf, offs);
    }

    public static r_bin_pe_addr_t Pe64_r_bin_pe_get_main_vaddr(Pe64_r_bin_pe_obj_t bin) {
        r_bin_pe_addr_t entry;
        int[] /*ut8*/ b = new int[512];

        if (bin == null || bin.b == null) {
            return null;
        }
        entry = Pe64_r_bin_pe_get_entrypoint(bin);
        // option2: /x 8bff558bec83ec20
        b[367] = 0;
//        if (r_buf_read_at(bin -> b, entry -> paddr, b, sizeof(b)) < 0) {
//            eprintf("Warning: Cannot read entry at 0x%08"PFMT64x"\n", entry -> paddr);
//            free(entry);
//            return NULL;
//        }

	/* Decode the jmp instruction, this gets the address of the 'main'
     * function for PE produced by a compiler whose name someone forgot to
	 * write down. */
        if (b[367] == 0xe8) {
            long jmp_dst = b[368] | (b[369] << 8) | (b[370] << 16) | (b[371] << 24);
            entry.paddr += 367 + 5 + jmp_dst;
            entry.vaddr += 367 + 5 + jmp_dst;
            return entry;
        }

        return null;
    }

    public static r_bin_pe_addr_t Pe64_r_bin_pe_get_entrypoint(Pe64_r_bin_pe_obj_t bin) {
        r_bin_pe_addr_t entry = new r_bin_pe_addr_t();
        boolean debug = false;
        long pe_entry;
        int i;
        long /*ut64*/ base_addr = Pe64_r_bin_pe_get_image_base(bin);

        pe_entry = bin.nt_headers.optional_header.AddressOfEntryPoint;
        entry.vaddr = bin_pe_rva_to_va(bin, pe_entry);
        entry.paddr = bin_pe_rva_to_paddr(bin, pe_entry);

        if (entry.paddr >= bin.b.length) {
            r_bin_pe_section_t[] sections = Pe64_r_bin_pe_get_sections(bin);
            long paddr = 0;

            for (i = 0; i < bin.num_sections; i++) {
                if ((sections[i].flags & PE_IMAGE_SCN_MEM_EXECUTE) != 0) {
                    entry.paddr = sections[i].paddr;
                    entry.vaddr = sections[i].vaddr + base_addr;
                    paddr = 1;
                    break;
                }

            }
            if (paddr != 0) {
                long min_off = -1;
                for (i = 0; i < bin.num_sections; i++) {
                    //get the lowest section's paddr
                    if (sections[i].paddr < min_off) {
                        entry.paddr = sections[i].paddr;
                        entry.vaddr = sections[i].vaddr + base_addr;
                        min_off = sections[i].paddr;
                    }
                }
                if (min_off == -1) {
                    //no section just a hack to try to fix entrypoint
                    //maybe doesn't work always
                    entry.paddr = pe_entry & ((bin.nt_headers.optional_header.SectionAlignment << 1) - 1);
                    entry.vaddr = entry.paddr + base_addr;
                }
            }
        }

        if (entry.paddr != 0) {
            r_bin_pe_section_t[] sections = Pe64_r_bin_pe_get_sections(bin);
            for (i = 0; i < bin.num_sections; i++) {
                //If there is a section with x without w perm is a good candidate to be the entrypoint
                if ((sections[i].flags & PE_IMAGE_SCN_MEM_EXECUTE) != 0 && (sections[i].flags & PE_IMAGE_SCN_MEM_WRITE) != 0) {
                    entry.paddr = sections[i].paddr;
                    entry.vaddr = sections[i].vaddr + base_addr;
                    break;
                }

            }
        }

        if (is_arm(bin) != 0 && (entry.vaddr & 1) != 0) {
            entry.vaddr--;
            if ((entry.paddr & 1) != 0) {
                entry.paddr--;
            }
        }

        return entry;
    }

    private static int is_arm(Pe64_r_bin_pe_obj_t bin) {
        switch (bin.nt_headers.file_header.Machine) {
            case PE_IMAGE_FILE_MACHINE_RPI2: // 462
            case PE_IMAGE_FILE_MACHINE_ARM:
            case PE_IMAGE_FILE_MACHINE_THUMB:
                return 1;
        }
        return 0;
    }

    public static r_bin_pe_section_t[] Pe64_r_bin_pe_get_sections(Pe64_r_bin_pe_obj_t bin) {
        r_bin_pe_section_t[] sections;
        Pe64_image_section_header[] shdr;
        int i, j, section_count = 0;

        shdr = bin.section_header;

        for (i = 0; i < bin.num_sections; i++) {
            //just allocate the needed
            if (shdr[i].SizeOfRawData > 0 || shdr[i].Misc.VirtualSize > 0)
                section_count++;
        }

        sections = new r_bin_pe_section_t[section_count];

        for (i = 0, j = 0; i < bin.num_sections; i++) {
            //if sz = 0 r_io_section_add will not add it so just skeep
            if (shdr[i].SizeOfRawData == 0 && shdr[i].Misc.VirtualSize == 0) {
                continue;
            }
            if (shdr[i].Name[0] == '\0') {
//                char *new_name = r_str_newf("sect_%d", j);
//                    strncpy((char *)sections[j].name, new_name, R_ARRAY_SIZE(sections[j].name) - 1);
//                    free(new_name);
            } else {
//                memcpy(sections[j].name, shdr[i].Name, PE_IMAGE_SIZEOF_SHORT_NAME);
//                sections[j].name[PE_IMAGE_SIZEOF_SHORT_NAME - 1] = '\0';
            }

            sections[j].vaddr = shdr[i].VirtualAddress;
            sections[j].size = shdr[i].SizeOfRawData;
            sections[j].vsize = shdr[i].Misc.VirtualSize;
            sections[j].paddr = shdr[i].PointerToRawData;
            sections[j].flags = shdr[i].Characteristics;
            sections[j].last = 0;
            j++;
        }
        sections[j].last = 1;
        bin.num_sections = section_count;
        return sections;
    }

    public static long /*ut64*/ Pe64_r_bin_pe_get_image_base(Pe64_r_bin_pe_obj_t bin) {
        if (bin == null || bin.nt_headers == null) {
            return 0;
        }
        return bin.nt_headers.optional_header.ImageBase;
    }

    public static long bin_pe_rva_to_va(Pe64_r_bin_pe_obj_t bin, long rva) {
        return bin.nt_headers.optional_header.ImageBase + rva;
    }

    public static long bin_pe_va_to_rva(Pe64_r_bin_pe_obj_t bin, long va) {
        if (va < bin.nt_headers.optional_header.ImageBase) {
            return va;
        }
        return va - bin.nt_headers.optional_header.ImageBase;
    }

    public static long bin_pe_rva_to_paddr(Pe64_r_bin_pe_obj_t bin, long rva) {
        long section_base, section_size;
        int i;

        for (i = 0; i < bin.num_sections; i++) {
            section_base = bin.section_header[i].VirtualAddress;
            section_size = bin.section_header[i].Misc.VirtualSize;
            if (rva >= section_base && rva < section_base + section_size) {
                return bin.section_header[i].PointerToRawData + (rva - section_base);
            }
        }
        return rva;
    }

    public static int bin_pe_init_resource(Pe64_r_bin_pe_obj_t bin) {
        Pe64_image_data_directory resource_dir = bin.nt_headers.optional_header.DataDirectory[PE_IMAGE_DIRECTORY_ENTRY_RESOURCE];
        int resource_dir_paddr = (int)bin_pe_rva_to_paddr(bin, resource_dir.VirtualAddress);
        if (resource_dir_paddr == 0) {
            return 0;
        }
        bin.resource_directory.read(bin.b, resource_dir_paddr);
        bin.resource_directory_offset = resource_dir_paddr;

        return 1;
    }

    public static Pe64_r_bin_pe_obj_t Pe64_r_bin_pe_new(ImageInputStream in) throws IOException {
        Pe64_r_bin_pe_obj_t bin = new Pe64_r_bin_pe_obj_t();
        bin.b = toByteArray(in);
        return bin;
    }

    private static byte[] toByteArray(ImageInputStream in) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = in.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();

        return buffer.toByteArray();
    }

    public static int bin_pe_init_hdr(Pe64_r_bin_pe_obj_t bin) {
        bin.dos_header.read(bin.b, 0);

//        sdb_num_set (bin->kv, "pe_dos_header.offset", 0, 0);
//        sdb_set (bin->kv, "pe_dos_header.format", "[2]zwwwwwwwwwwwww[4]www[10]wx"
//                " e_magic e_cblp e_cp e_crlc e_cparhdr e_minalloc e_maxalloc"
//                " e_ss e_sp e_csum e_ip e_cs e_lfarlc e_ovno e_res e_oemid"
//                " e_oeminfo e_res2 e_lfanew", 0);
//        if (bin->dos_header->e_lfanew > (unsigned int)bin->size) {
//            eprintf("Invalid e_lfanew field\n");
//            return false;
//        }
        bin.nt_header_offset = bin.dos_header.e_lfanew;
        bin.nt_headers.read(bin.b, (int)bin.nt_header_offset);

//        sdb_set (bin->kv, "pe_magic.cparse", "enum pe_magic { IMAGE_NT_OPTIONAL_HDR32_MAGIC=0x10b, IMAGE_NT_OPTIONAL_HDR64_MAGIC=0x20b, IMAGE_ROM_OPTIONAL_HDR_MAGIC=0x107 };", 0);
//        sdb_set (bin->kv, "pe_subsystem.cparse", "enum pe_subsystem { IMAGE_SUBSYSTEM_UNKNOWN=0, IMAGE_SUBSYSTEM_NATIVE=1, IMAGE_SUBSYSTEM_WINDOWS_GUI=2, "
//                " IMAGE_SUBSYSTEM_WINDOWS_CUI=3, IMAGE_SUBSYSTEM_OS2_CUI=5, IMAGE_SUBSYSTEM_POSIX_CUI=7, IMAGE_SUBSYSTEM_WINDOWS_CE_GUI=9, "
//                " IMAGE_SUBSYSTEM_EFI_APPLICATION=10, IMAGE_SUBSYSTEM_EFI_BOOT_SERVICE_DRIVER=11, IMAGE_SUBSYSTEM_EFI_RUNTIME_DRIVER=12, "
//                " IMAGE_SUBSYSTEM_EFI_ROM=13, IMAGE_SUBSYSTEM_XBOX=14, IMAGE_SUBSYSTEM_WINDOWS_BOOT_APPLICATION=16 };", 0);
//        sdb_set (bin->kv, "pe_dllcharacteristics.cparse", "enum pe_dllcharacteristics { IMAGE_LIBRARY_PROCESS_INIT=0x0001, IMAGE_LIBRARY_PROCESS_TERM=0x0002, "
//                " IMAGE_LIBRARY_THREAD_INIT=0x0004, IMAGE_LIBRARY_THREAD_TERM=0x0008, IMAGE_DLLCHARACTERISTICS_HIGH_ENTROPY_VA=0x0020, "
//                " IMAGE_DLLCHARACTERISTICS_DYNAMIC_BASE=0x0040, IMAGE_DLLCHARACTERISTICS_FORCE_INTEGRITY=0x0080, "
//                " IMAGE_DLLCHARACTERISTICS_NX_COMPAT=0x0100, IMAGE_DLLCHARACTERISTICS_NO_ISOLATION=0x0200,IMAGE_DLLCHARACTERISTICS_NO_SEH=0x0400, "
//                " IMAGE_DLLCHARACTERISTICS_NO_BIND=0x0800, IMAGE_DLLCHARACTERISTICS_APPCONTAINER=0x1000, IMAGE_DLLCHARACTERISTICS_WDM_DRIVER=0x2000, "
//                " IMAGE_DLLCHARACTERISTICS_GUARD_CF=0x4000, IMAGE_DLLCHARACTERISTICS_TERMINAL_SERVER_AWARE=0x8000};", 0);
//	#if R_BIN_PE64
//        sdb_num_set (bin->kv, "pe_nt_image_headers64.offset", bin->dos_header->e_lfanew, 0);
//        sdb_set (bin->kv, "pe_nt_image_headers64.format", "[4]z?? signature (pe_image_file_header)fileHeader (pe_image_optional_header64)optionalHeader", 0);
//        sdb_set (bin->kv, "pe_image_optional_header64.format", "[2]Ebbxxxxxqxxwwwwwwxxxx[2]E[2]Bqqqqxx[16]?"
//                " (pe_magic)magic majorLinkerVersion minorLinkerVersion sizeOfCode sizeOfInitializedData"
//                " sizeOfUninitializedData addressOfEntryPoint baseOfCode imageBase"
//                " sectionAlignment fileAlignment majorOperatingSystemVersion minorOperatingSystemVersion"
//                " majorImageVersion minorImageVersion majorSubsystemVersion minorSubsystemVersion"
//                " win32VersionValue sizeOfImage sizeOfHeaders checkSum (pe_subsystem)subsystem (pe_dllcharacteristics)dllCharacteristics"
//                " sizeOfStackReserve sizeOfStackCommit sizeOfHeapReserve sizeOfHeapCommit loaderFlags"
//                " numberOfRvaAndSizes (pe_image_data_directory)dataDirectory", 0);

//        sdb_num_set (bin->kv, "pe_nt_image_headers32.offset", bin->dos_header->e_lfanew, 0);
//        sdb_set (bin->kv, "pe_nt_image_headers32.format", "[4]z?? signature (pe_image_file_header)fileHeader (pe_image_optional_header32)optionalHeader", 0);
//        sdb_set (bin->kv, "pe_image_optional_header32.format", "[2]Ebbxxxxxxxxxwwwwwwxxxx[2]E[2]Bxxxxxx[16]?"
//                " (pe_magic)magic majorLinkerVersion minorLinkerVersion sizeOfCode sizeOfInitializedData"
//                " sizeOfUninitializedData addressOfEntryPoint baseOfCode baseOfData imageBase"
//                " sectionAlignment fileAlignment majorOperatingSystemVersion minorOperatingSystemVersion"
//                " majorImageVersion minorImageVersion majorSubsystemVersion minorSubsystemVersion"
//                " win32VersionValue sizeOfImage sizeOfHeaders checkSum (pe_subsystem)subsystem (pe_dllcharacteristics)dllCharacteristics"
//                " sizeOfStackReserve sizeOfStackCommit sizeOfHeapReserve sizeOfHeapCommit loaderFlags numberOfRvaAndSizes"
//                " (pe_image_data_directory)dataDirectory", 0);

//        sdb_set (bin->kv, "pe_machine.cparse", "enum pe_machine { IMAGE_FILE_MACHINE_I386=0x014c, IMAGE_FILE_MACHINE_IA64=0x0200, IMAGE_FILE_MACHINE_AMD64=0x8664 };", 0);
//        sdb_set (bin->kv, "pe_characteristics.cparse", "enum pe_characteristics { "
//                " IMAGE_FILE_RELOCS_STRIPPED=0x0001, IMAGE_FILE_EXECUTABLE_IMAGE=0x0002, IMAGE_FILE_LINE_NUMS_STRIPPED=0x0004, "
//                " IMAGE_FILE_LOCAL_SYMS_STRIPPED=0x0008, IMAGE_FILE_AGGRESIVE_WS_TRIM=0x0010, IMAGE_FILE_LARGE_ADDRESS_AWARE=0x0020, "
//                " IMAGE_FILE_BYTES_REVERSED_LO=0x0080, IMAGE_FILE_32BIT_MACHINE=0x0100, IMAGE_FILE_DEBUG_STRIPPED=0x0200, "
//                " IMAGE_FILE_REMOVABLE_RUN_FROM_SWAP=0x0400, IMAGE_FILE_NET_RUN_FROM_SWAP=0x0800, IMAGE_FILE_SYSTEM=0x1000, "
//                " IMAGE_FILE_DLL=0x2000, IMAGE_FILE_UP_SYSTEM_ONLY=0x4000, IMAGE_FILE_BYTES_REVERSED_HI=0x8000 };", 0);
//        sdb_set (bin->kv, "pe_image_file_header.format", "[2]Ewtxxw[2]B"
//                " (pe_machine)machine numberOfSections timeDateStamp pointerToSymbolTable"
//                " numberOfSymbols sizeOfOptionalHeader (pe_characteristics)characteristics", 0);
//        sdb_set (bin->kv, "pe_image_data_directory.format", "xx virtualAddress size",0);
//
//        // adding compile time to the SDB
//        {
//            struct my_timezone {
//            int tz_minuteswest;     /* minutes west of Greenwich */
//            int tz_dsttime;         /* type of DST correction */
//        } tz;
//            struct timeval tv;
//            int gmtoff;
//		char *timestr;
//            time_t ts = (time_t)bin->nt_headers->file_header.TimeDateStamp;
//            sdb_num_set (bin->kv, "image_file_header.TimeDateStamp", bin->nt_headers->file_header.TimeDateStamp, 0);
//            gettimeofday (&tv, (void*)&tz);
//            gmtoff = (int)(tz.tz_minuteswest*60); // in seconds
//            ts += gmtoff;
//            timestr = r_str_chop (strdup (ctime (&ts)));
//            // gmt offset for pe date is t->tm_gmtoff
//            sdb_set_owned (bin->kv,
//                    "image_file_header.TimeDateStamp_string",
//                    timestr, 0);
//        }
//
//        if (strncmp ((char*)&bin->dos_header->e_magic, "MZ", 2) ||
//        strncmp ((char*)&bin->nt_headers->Signature, "PE", 2)) {
//            return false;
//        }
        return 1;
    }

    public static int bin_pe_init_sections(Pe64_r_bin_pe_obj_t bin) {
        bin.num_sections = bin.nt_headers.file_header.NumberOfSections;

        if (bin.num_sections < 1) {
            return 1;
        }

        bin.section_header = new Pe64_image_section_header[bin.num_sections];
        int offs = (int)bin.nt_header_offset + Pe64_image_nt_headers.SIZE;

        for (int i = 0; i < bin.section_header.length; i++, offs += Pe64_image_section_header.SIZE) {
            bin.section_header[i] = new Pe64_image_section_header();
            bin.section_header[i].read(bin.b, offs);
        }

        return 1;
    }

    public static void Pe64_r_bin_store_all_resource_version_info(Pe64_r_bin_pe_obj_t bin) {
//        char key[ 30];
        long off = 0;
        int counter = 0;
//        Sdb *sdb = NULL;
//        sdb = sdb_new0();
//        if (!sdb) {
//            return;
//        }
        // XXX: assume there is only 3 layers in the tree
        int totalRes = bin.resource_directory.NumberOfNamedEntries + bin.resource_directory.NumberOfIdEntries;
        long curRes = bin.resource_directory.NumberOfNamedEntries;

        for (; curRes < totalRes; curRes++) {
            Pe_image_resource_directory_entry typeEntry = new Pe_image_resource_directory_entry();
            off = bin.resource_directory_offset + Pe_image_resource_directory.SIZE + curRes * Pe_image_resource_directory_entry.SIZE;
            typeEntry.read(bin.b, (int)off);

            if (/*typeEntry.u1.s.NameIsString &&*/ typeEntry.u1.Id == Constant.PE_RESOURCE_ENTRY_ICON) {
                Pe_image_resource_directory identDir = new Pe_image_resource_directory();
                off = bin.resource_directory_offset + typeEntry.u2.s.OffsetToDirectory;

                identDir.read(bin.b, (int)off);
                long totalIdent = identDir.NumberOfNamedEntries + identDir.NumberOfIdEntries;
//                ut32 curIdent = 0;
//                for (; curIdent < totalIdent; curIdent++) {
//                    Pe_image_resource_directory_entry identEntry;
//                    off = bin -> resource_directory_offset + typeEntry.u2.s.OffsetToDirectory +
//                            sizeof(identDir) + curIdent * sizeof(identEntry);
//                    if (off > bin -> size || off + sizeof(Pe_image_resource_directory_entry) > bin -> size) {
//					goto out_error;
//                    }
//                    if (r_buf_read_at(bin -> b, off, (ut8 *) & identEntry, sizeof(Pe_image_resource_directory_entry)) < 1) {
//                        eprintf("Warning: read (resource identifier entry)\n");
//					goto out_error;
//                    }
//                    if (!identEntry.u2.s.DataIsDirectory) {
//                        continue;
//                    }
//                    Pe_image_resource_directory langDir;
//                    off = bin -> resource_directory_offset + identEntry.u2.s.OffsetToDirectory;
//                    if (off > bin -> size || off + sizeof(Pe_image_resource_directory) > bin -> size) {
//					goto out_error;
//                    }
//                    if (r_buf_read_at(bin -> b, off, (ut8 *) & langDir, sizeof(Pe_image_resource_directory)) < 1) {
//                        eprintf("Warning: read (resource language directory)\n");
//					goto out_error;
//                    }
//                    ut32 totalLang = langDir.NumberOfNamedEntries + langDir.NumberOfIdEntries;
//                    ut32 curLang = 0;
//                    for (; curLang < totalLang; curLang++) {
//                        Pe_image_resource_directory_entry langEntry;
//                        off = bin -> resource_directory_offset + identEntry.u2.s.OffsetToDirectory + sizeof(langDir) + curLang * sizeof(langEntry);
//                        if (off > bin -> size || off + sizeof(Pe_image_resource_directory_entry) > bin -> size) {
//						goto out_error;
//                        }
//                        if (r_buf_read_at(bin -> b, off, (ut8 *) & langEntry, sizeof(Pe_image_resource_directory_entry)) < 1) {
//                            eprintf("Warning: read (resource language entry)\n");
//						goto out_error;
//                        }
//                        if (langEntry.u2.s.DataIsDirectory) {
//                            continue;
//                        }
//                        Pe_image_resource_data_entry data;
//                        off = bin -> resource_directory_offset + langEntry.u2.OffsetToData;
//                        if (off > bin -> size || off + sizeof(Pe_image_resource_data_entry) > bin -> size) {
//						goto out_error;
//                        }
//                        if (r_buf_read_at(bin -> b, off, (ut8 *) & data, sizeof(data)) != sizeof(data)) {
//                            eprintf("Warning: read (resource data entry)\n");
//						goto out_error;
//                        }
//                        PE_DWord data_paddr = bin_pe_rva_to_paddr(bin, data.OffsetToData);
//                        if (!data_paddr) {
//                            eprintf("Warning: bad RVA in resource data entry\n");
//						goto out_error;
//                        }
//                        PE_DWord cur_paddr = data_paddr;
//                        if ((cur_paddr & 0x3) != 0) {
//                            // XXX: mb align address and read structure?
//                            eprintf("Warning: not aligned version info address\n");
//                            continue;
//                        }
//                        while (cur_paddr < data_paddr + data.Size && cur_paddr < bin -> size) {
//                            PE_VS_VERSIONINFO * vs_VersionInfo = Pe_r_bin_pe_parse_version_info(bin, cur_paddr);
//                            if (vs_VersionInfo) {
//                                snprintf(key, 30, "VS_VERSIONINFO%d", counter++);
//                                sdb_ns_set(sdb, key, Pe_r_bin_store_resource_version_info(vs_VersionInfo));
//                            } else {
//                                break;
//                            }
//                            cur_paddr += vs_VersionInfo -> wLength;
//                            free_VS_VERSIONINFO(vs_VersionInfo);
//                            align32(cur_paddr);
//                        }
//                    }
                }
//            }
        }
//        sdb_ns_set(bin -> kv, "vs_version_info", sdb);
//        out_error:
//        sdb_free(sdb);
        return;
    }

}

