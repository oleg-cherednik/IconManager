package cop.swing.icoman.icns.foo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Oleg Cherednik
 * @since 24.08.2015
 */
public class icns_family_t {
    public int resourceType;	/* uint32, Always should be 'icns' */
    public int resourceSize;	/* int32, Total size of resource  */
    public List<icns_element_t> elements = new ArrayList<>();    /* icon elements */
}
