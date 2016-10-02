package cop.icoman.exceptions;

/**
 * @author Oleg Cherednik
 * @since 16.08.2015
 */
public class ImageKeyNotSupportedException extends IconManagerException {
    private static final long serialVersionUID = -6932141332945046977L;

    public ImageKeyNotSupportedException(String type) {
        super(String.format("Image key '%s' is not supported", type));
    }
}
