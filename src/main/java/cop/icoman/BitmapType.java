package cop.icoman;

/**
 * @author Oleg Cherednik
 * @since 03.07.2013
 */
public enum BitmapType {
    NONE(0),
    ICO(1),
    CUR(2);

    private final int code;

    BitmapType(int code) {
        this.code = code;
    }

    // ========== static ==========

    public static BitmapType parseCode(int code) {
        for (BitmapType type : values())
            if (type.code == code)
                return type;

        return NONE;
    }
}
