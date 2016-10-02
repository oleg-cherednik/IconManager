package cop.icoman.exceptions;

/**
 * @author Oleg Cherednik
 * @since 26.07.2015
 */
public class FormatNotSupportedException extends IconManagerException {
    private static final long serialVersionUID = 5289183736564114818L;

    public FormatNotSupportedException(String message) {
        super(message);
    }
}
