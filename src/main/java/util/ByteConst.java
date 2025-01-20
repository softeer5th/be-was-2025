package util;

public class ByteConst {
    public final static byte PERCENT = 0x25;
    public final static byte ZERO = 0x30;
    public final static byte NINE = 0x39;
    public final static byte LARGE_A = 0x41;
    public final static byte LARGE_F = 0x46;
    public final static byte SMALL_A = 0x61;
    public final static byte SMALL_F = 0x66;
    public static final byte PLUS = 0x2B;
    public static final byte SP = 0x20;
    public static final byte CR = 0x0D;
    public static final byte LF = 0x0A;
    public static final byte AMPERSAND = 0x26;
    public static final byte EQUAL = 0x3D;

    public static boolean isHexNumber(byte b) {
        return ((ZERO <= b) && (b <= NINE)) || ((LARGE_A <= b) && (b <= LARGE_F)) || ((SMALL_A <= b) && (b <= SMALL_F));
    }
}
