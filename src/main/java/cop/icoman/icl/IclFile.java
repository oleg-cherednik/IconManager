package cop.icoman.icl;

import cop.icoman.AbstractIconFile;
import cop.icoman.IconIO;
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

/**
 * @author Oleg Cherednik
 * @since 02.10.2016
 */
public final class IclFile extends AbstractIconFile {
    private static final int SIZE_DOS_HEADER = 58;

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
    public Map<String, Image> getImages(String name) throws ImageNotFoundException {
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
        checkMarkZbikowskiSignature(in);
        in.skipBytes(SIZE_DOS_HEADER);
        int ntHeaderOffs = in.readUnsignedShort();
        in.seek(ntHeaderOffs);
        in.mark();

        NtHeader ntHeader = new NtHeader(in);
        Map<String, SectionHeader> sectionHeaders = readSectionTable(in, ntHeader.getFileHeader().getNumberOfSection());
        long rva = ntHeader.getOptionalHeader().getDataDirectory(OptionalHeader.DirectoryEntry.RESOURCE).getRva();
        long offs = rvaToOff(sectionHeaders.values(), rva, ntHeader.getOptionalHeader().getSectionAlignment());
        in.seek(offs);
        in.mark();

        return readIconsResources(in, offs);
    }

    private static final int PE_RESOURCE_ENTRY_ICON = 3;
    private static final int PE_RESOURCE_ENTRY_GROUP_ICON = 14;
    private static final int PE_RESOURCE_ENTRY_GROUP_ICON_NAME = 45;

    private static void skipNamedEntries(ImageInputStream in, int total) throws IOException {
        in.skipBytes(total * ResourceDirectoryEntry.SIZE);
    }

    private static Map<Integer, ResourceDirectoryEntry> readResourceDirectoryEntries(ImageInputStream in, int total, boolean idDec) throws IOException {
        ResourceDirectoryEntry entry;
        Map<Integer, ResourceDirectoryEntry> entries = new HashMap<>();

        for (int i = 0; i < total; i++)
            entries.put((entry = new ResourceDirectoryEntry(in, idDec)).getId(), entry);

        return entries;
    }

    private static Map<String, Map<String, Image>> readIconsResources(ImageInputStream in, long offs) throws IOException, IconManagerException {
        ResourceDirectory.read(in);
        skipNamedEntries(in, ResourceDirectory.getNumberOfNamedEntries());
        Map<Integer, ResourceDirectoryEntry> resourceDirectoryEntries =
                readResourceDirectoryEntries(in, ResourceDirectory.getNumberOfIdEntries(), false);
        List<String> groupIconNames = readGroupIconName(resourceDirectoryEntries.get(PE_RESOURCE_ENTRY_GROUP_ICON_NAME), in);
        Map<String, Set<ImageHeader>> headers = readGroupIcon(resourceDirectoryEntries.get(PE_RESOURCE_ENTRY_GROUP_ICON), groupIconNames, in);
        Map<Integer, Image> images = readIcon(resourceDirectoryEntries.get(PE_RESOURCE_ENTRY_ICON), in);
        Map<String, Map<String, Image>> imageByIdName = new LinkedHashMap<>();

        for (Map.Entry<String, Set<ImageHeader>> entry : headers.entrySet()) {
            String name = entry.getKey();
            Map<String, Image> imageById = new LinkedHashMap<>();

            for (ImageHeader header : entry.getValue()) {
                String id = ImageKey.parse(name, header.width, header.height, header.bitsPerPixel);
                Image image = images.get(header.pos);

                if (image != null)
                    imageById.put(id, image);
            }

            imageByIdName.put(name, imageById);
        }

        return imageByIdName;
    }

    private static long getLeafOffs(long offs, ImageInputStream in) throws IOException, IconManagerException {
        moveToOffs(in, offs);
        ResourceDirectory.read(in);

        if (ResourceDirectory.getNumberOfNamedEntries() != 0)
            throw new IconManagerException();
        if (ResourceDirectory.getNumberOfIdEntries() != 1)
            throw new IconManagerException();

        ResourceDirectoryEntry entry = new ResourceDirectoryEntry(in, false);
        return entry.leaf ? entry.offsData : getLeafOffs(entry.offsData, in);
    }

    private static List<String> readGroupIconName(ResourceDirectoryEntry entryGroupIconName, ImageInputStream in)
            throws IOException, IconManagerException {
        if (entryGroupIconName == null)
            return Collections.emptyList();

        long offs = entryGroupIconName.leaf ? entryGroupIconName.offsData : getLeafOffs(entryGroupIconName.offsData, in);
        reset(in);
        in.skipBytes(offs);
        ResourceDataEntry resourceDataEntry = new ResourceDataEntry(in);
        in.seek(resourceDataEntry.rva);

        checkIclSignature(in);

        int length;
        List<String> names = new ArrayList<>();

        while ((length = in.readUnsignedByte()) != 0) {
            names.add(IconIO.readString(in, length));
        }

        return names;
    }

    private static Map<String, Set<ImageHeader>> readGroupIcon(ResourceDirectoryEntry entryGroupIcon, List<String> groupIconNames,
            ImageInputStream in) throws IOException, IconManagerException {
        if (entryGroupIcon == null || groupIconNames.isEmpty())
            return Collections.emptyMap();
        if (entryGroupIcon.leaf)
            throw new IconManagerException();

        moveToOffs(in, entryGroupIcon.offsData);
        ResourceDirectory.read(in);

        if (ResourceDirectory.getNumberOfNamedEntries() != 0)
            throw new IconManagerException();
        if (ResourceDirectory.getNumberOfIdEntries() != groupIconNames.size())
            throw new IconManagerException();

        Map<Integer, ResourceDirectoryEntry> entries = readResourceDirectoryEntries(in, ResourceDirectory.getNumberOfIdEntries(), true);
        Map<String, Set<ImageHeader>> map = new LinkedHashMap<>();
        int pos = 0;

        for (Map.Entry<Integer, ResourceDirectoryEntry> ent : entries.entrySet()) {
            long offs = ent.getValue().leaf ? ent.getValue().offsData : getLeafOffs(ent.getValue().offsData, in);
            reset(in);
            in.skipBytes(offs);
            ResourceDataEntry resourceDataEntry = new ResourceDataEntry(in);
            in.seek(resourceDataEntry.rva);

            int total = resourceDataEntry.size / ImageHeader.SIZE;
            Set<ImageHeader> res = new TreeSet<>(ImageHeader.SORT_BY_BITS_SIZE_ASC);

            for (int i = 0; i < total; i++, pos++)
                res.add(new ImageHeader(pos, in));

            map.put(groupIconNames.get(ent.getKey()), res);
        }

        return map;
    }

    private static Map<Integer, Image> readIcon(ResourceDirectoryEntry entryIcon, ImageInputStream in) throws IconManagerException, IOException {
        if (entryIcon == null)
            return Collections.emptyMap();
        if (entryIcon.leaf)
            throw new IconManagerException();

        moveToOffs(in, entryIcon.offsData);
        ResourceDirectory.read(in);

        if (ResourceDirectory.getNumberOfNamedEntries() != 0)
            throw new IconManagerException();

        Map<Integer, ResourceDirectoryEntry> entries = readResourceDirectoryEntries(in, ResourceDirectory.getNumberOfIdEntries(), true);
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

    private static void checkIclSignature(ImageInputStream in) throws IOException, IconManagerException {
        if (!"ICL".equals(IconIO.readString(in, in.readUnsignedByte())))
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
        return sectionHeader != null ? rva - sectionHeader.getVirtualAddress() + sectionHeader.getPointerToRawData() : 0;
    }
}
