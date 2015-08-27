package cop.swing.icoman.icns.foo;

/**
 * @author Oleg Cherednik
 * @since 24.08.2015
 */
public class icns_element_t {
    public int elementType;    /* uint32, 'ICN#', 'icl8', etc... */
    public int elementSize;    /* int32, Total size of element  */
    public byte[] elementData; /* icon image data */
}
