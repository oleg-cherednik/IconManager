package cop.icoman.icl;

import cop.icoman.AbstractIconFile;
import cop.icoman.ImageKey;
import cop.icoman.exceptions.FormatNotSupportedException;
import cop.icoman.exceptions.IconManagerException;
import cop.icoman.exceptions.ImageNotFoundException;
import cop.icoman.icl.imageio.IclReaderSpi;
import cop.icoman.ico.IcoFile;

import javax.imageio.stream.ImageInputStream;
import javax.validation.constraints.NotNull;
import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static cop.icoman.icl.SectionHeader.readString;

/**
 * @author Oleg Cherednik
 * @since 02.10.2016
 */
public final class IclFile extends AbstractIconFile {
    private static final int SIZE_DOS_HEADER = 58;
    private static long base;

    private final Map<String, Map<String, Image>> icoByName;

    public IclFile(ImageInputStream in) throws Exception {
        this(read(in));
    }

    private IclFile(Map<String, Map<String, Image>> images) {
        super(createImageById(images));
        icoByName = createIcoByName(images);
    }

    @NotNull
    public Set<String> getNames() {
        return icoByName.isEmpty() ? Collections.emptySet() : icoByName.keySet();
    }

    @NotNull
    public Map<String, Image> getIcoImages(String name) throws ImageNotFoundException {
        if (!icoByName.containsKey(name))
            throw new ImageNotFoundException(name);
        return icoByName.get(name);
    }

    // ========== static ==========

    private static Map<String, Map<String, Image>> createIcoByName(Map<String, Map<String, Image>> imagesByNameId) {
        Map<String, Map<String, Image>> idByName = new LinkedHashMap<>();

        for (Map.Entry<String, Map<String, Image>> entry : imagesByNameId.entrySet())
            idByName.put(entry.getKey(), Collections.unmodifiableMap(entry.getValue()));

        return Collections.unmodifiableMap(idByName);
    }

    private static Map<String, Image> createImageById(Map<String, Map<String, Image>> imagesByNameId) {
        Map<String, Image> imageById = new LinkedHashMap<>();
        imagesByNameId.values().forEach(imageById::putAll);
        return Collections.unmodifiableMap(imageById);
    }

    private static Map<String, Map<String, Image>> read(ImageInputStream in) throws Exception {
        in.mark();
        checkMarkZbikowskiSignature(in);
        in.skipBytes(SIZE_DOS_HEADER);
        int peHeaderOffs = in.readUnsignedShort();
        in.reset();
        in.skipBytes(peHeaderOffs);
        in.mark();  // 0x0

        PeHeader peHeader = PeHeader.read(in);
        Map<String, SectionHeader> sectionHeaders = readSectionTable(in, peHeader.getFileHeader().getNumberOfSection());
        long rva = peHeader.getOptionalHeader().getDataDirectory(OptionalHeader.DirectoryEntry.RESOURCE).getVirtualAddress();
        long offs = rvaToOff(sectionHeaders.values(), rva, peHeader.getOptionalHeader().getSectionAlignment()) - peHeaderOffs;
        in.reset();
        in.skipBytes(offs);
        in.mark();

        base = offs;

        return readIconsResources(in);
    }

    private static final int PE_RESOURCE_ENTRY_ICON = 3;
    private static final int PE_RESOURCE_ENTRY_GROUP_ICON = 14;
    private static final int PE_RESOURCE_ENTRY_GROUP_ICON_NAME = 45;

    private static Map<String, Map<String, Image>> readIconsResources(ImageInputStream in) throws IOException, IconManagerException {
        moveToOffs(in, 0x0);
        ResourceDirectory resourceDirectory = new ResourceDirectory(in);
        in.skipBytes(resourceDirectory.getNumberOfNamedEntries() * ResourceDirectoryEntry.SIZE);
        Map<Integer, ResourceDirectoryEntry> entries = new HashMap<>();

        for (int i = 0; i < resourceDirectory.getNumberOfIdEntries(); i++) {
            ResourceDirectoryEntry entry = new ResourceDirectoryEntry(in);
            entries.put(entry.id, entry);
        }

        Map<String, Set<ImageHeader>> headers = readHeaders(entries, readNames(entries, in), in);
        Map<Integer, Image> images = readImages(entries, in);
        Map<String, Map<String, Image>> imageByIdName = new LinkedHashMap<>();

        for (Map.Entry<String, Set<ImageHeader>> entry : headers.entrySet()) {
            String name = entry.getKey();
            Map<String, Image> imageById = new LinkedHashMap<>();

            for (ImageHeader header : entry.getValue()) {
                String id = ImageKey.parse(name, header.width, header.height, header.bitsPerPixel);
                Image image = images.get(header.num);

                if (image != null)
                    imageById.put(id, image);
            }

            imageByIdName.put(name, imageById);
        }

        return imageByIdName;
    }

    private static long getLeafOffs(long offs, ImageInputStream in) throws IOException, IconManagerException {
        moveToOffs(in, offs);
        ResourceDirectory resourceDirectory = new ResourceDirectory(in);

        if (resourceDirectory.getNumberOfNamedEntries() != 0)
            throw new IconManagerException();
        if (resourceDirectory.getNumberOfIdEntries() != 1)
            throw new IconManagerException();

        ResourceDirectoryEntry entry = new ResourceDirectoryEntry(in);
        return entry.leaf ? entry.offsData : getLeafOffs(entry.offsData, in);
    }

    private static List<String> readNames(Map<Integer, ResourceDirectoryEntry> entries, ImageInputStream in)
            throws IOException, IconManagerException {
        ResourceDirectoryEntry entry = entries.get(PE_RESOURCE_ENTRY_GROUP_ICON_NAME);

        if (entry == null)
            return Collections.emptyList();

        long offs = entry.leaf ? entry.offsData : getLeafOffs(entry.offsData, in);
        reset(in);
        in.skipBytes(offs);
        ResourceDataEntry resourceDataEntry = new ResourceDataEntry(in);
        in.seek(resourceDataEntry.rva);

        checkIclSignature(in);

        int length;
        List<String> names = new ArrayList<>();

        while ((length = in.readUnsignedByte()) != 0) {
            names.add(readString(in, length));
        }

        return names;
    }

    private static Map<String, Set<ImageHeader>> readHeaders(Map<Integer, ResourceDirectoryEntry> entries, List<String> names,
            ImageInputStream in) throws IOException, IconManagerException {
        ResourceDirectoryEntry entryGroupIcon = entries.get(PE_RESOURCE_ENTRY_GROUP_ICON);

        if (entryGroupIcon == null || names.isEmpty())
            return Collections.emptyMap();

        if (entryGroupIcon.leaf)
            throw new IconManagerException();

        moveToOffs(in, entryGroupIcon.offsData);
        ResourceDirectory resourceDirectory = new ResourceDirectory(in);

        if (resourceDirectory.getNumberOfNamedEntries() != 0)
            throw new IconManagerException();
        if (resourceDirectory.getNumberOfIdEntries() != names.size())
            throw new IconManagerException();

        entries = readResourceDirectoryEntries(in, resourceDirectory.getNumberOfIdEntries());
        Map<String, Set<ImageHeader>> map = new LinkedHashMap<>();

        for (Map.Entry<Integer, ResourceDirectoryEntry> ent : entries.entrySet()) {
            long offs = ent.getValue().leaf ? ent.getValue().offsData : getLeafOffs(ent.getValue().offsData, in);
            reset(in);
            in.skipBytes(offs);
            ResourceDataEntry resourceDataEntry = new ResourceDataEntry(in);
            in.seek(resourceDataEntry.rva);

            int total = resourceDataEntry.size / ImageHeader.SIZE;
            Set<ImageHeader> res = new TreeSet<>(ImageHeader.SORT_BY_BITS_SIZE_ASC);

            for (int i = 0; i < total; i++)
                res.add(new ImageHeader(i, in));

            map.put(names.get(ent.getKey()), res);
        }

        return map;
    }

    private static Map<Integer, Image> readImages(Map<Integer, ResourceDirectoryEntry> entries, ImageInputStream in)
            throws IconManagerException, IOException {
        ResourceDirectoryEntry entryIcon = entries.get(PE_RESOURCE_ENTRY_ICON);

        if (entryIcon == null)
            return Collections.emptyMap();

        if (entryIcon.leaf)
            throw new IconManagerException();

        moveToOffs(in, entryIcon.offsData);
        ResourceDirectory resourceDirectory = new ResourceDirectory(in);

        if (resourceDirectory.getNumberOfNamedEntries() != 0)
            throw new IconManagerException();

        entries = readResourceDirectoryEntries(in, resourceDirectory.getNumberOfIdEntries());
        Map<Integer, Image> map = new LinkedHashMap<>();

        for (Map.Entry<Integer, ResourceDirectoryEntry> ent : entries.entrySet()) {
            long offs = ent.getValue().leaf ? ent.getValue().offsData : getLeafOffs(ent.getValue().offsData, in);
            reset(in);
            in.skipBytes(offs);
            ResourceDataEntry resourceDataEntry = new ResourceDataEntry(in);
            in.seek(resourceDataEntry.rva);
            map.put(ent.getKey(), IcoFile.readIconImage(in, resourceDataEntry.size));
        }

        return map;
    }

    private static Map<Integer, ResourceDirectoryEntry> readResourceDirectoryEntries(ImageInputStream in, int total) throws IOException {
        Map<Integer, ResourceDirectoryEntry> entries = new HashMap<>();

        for (int i = 0; i < total; i++) {
            ResourceDirectoryEntry entry = new ResourceDirectoryEntry(in);
            entries.put(entry.id - 1, entry);
        }

        return entries;
    }

    private static void checkIclSignature(ImageInputStream in) throws IOException, IconManagerException {
        if (!"ICL".equals(readString(in, in.readUnsignedByte())))
            throw new IconManagerException();
    }

    private static void reset(ImageInputStream in) throws IOException {
        in.reset();
        in.mark();
    }

    private static void moveToOffs(ImageInputStream in, long offs) throws IOException {
        reset(in);
        in.skipBytes(offs);
    }

    private static Map<String, SectionHeader> readSectionTable(ImageInputStream in, int numberOfSection) throws IOException {
        if (numberOfSection <= 0)
            return Collections.emptyMap();

        SectionHeader header;
        Map<String, SectionHeader> sectionHeaders = new LinkedHashMap<>(numberOfSection);

        for (int i = 0; i < numberOfSection; i++)
            sectionHeaders.put((header = new SectionHeader(in)).getName(), header);

        return sectionHeaders;

    }

    private static void checkMarkZbikowskiSignature(ImageInputStream in) throws IOException, FormatNotSupportedException {
        if (!IclReaderSpi.isHeaderValid(in.readUnsignedShort()))
            throw new FormatNotSupportedException("Expected MZ format: 'rva:0, size:2' should be 'MZ'");
    }

    private static long allignDown(long x, long align) {
        return x & ~(align - 1);
    }

    private static long allignUp(long x, long align) {
        return (x & (align - 1)) != 0 ? allignDown(x, align) + align : x;
    }


    private static SectionHeader defSection(Collection<SectionHeader> sectionHeaders, long rva, long sectionAlignment) {
        for (SectionHeader sectionHeader : sectionHeaders) {
            long start = sectionHeader.getVirtualAddress();
            long end = start + allignUp(sectionHeader.getMisc(), sectionAlignment);

            if (rva >= start && rva < end)
                return sectionHeader;
        }

        return null;
    }

    private static long rvaToOff(Collection<SectionHeader> sectionHeaders, long rva, long sectionAlignment) {
        SectionHeader sectionHeader = defSection(sectionHeaders, rva, sectionAlignment);

        if (sectionHeader != null)
            return rva - sectionHeader.getVirtualAddress() + sectionHeader.getPointerToRawData();
        else
            return 0;
    }
}
