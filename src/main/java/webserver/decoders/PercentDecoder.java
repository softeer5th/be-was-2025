package webserver.decoders;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class PercentDecoder implements ByteDecoder {
    private final static byte PERCENT = 0x25;
    private final static byte ZERO = 0x30;
    private final static byte NINE = 0x39;
    private final static byte LARGE_A = 0x41;
    private final static byte LARGE_F = 0x46;
    private final static byte SMALL_A = 0x61;
    private final static byte SMALL_F = 0x66;
    private static final byte PLUS = 0x2B;
    private static final byte SP = 0x20;

    private boolean isHexNumber(byte b) {
        return ((ZERO <= b) && (b <= NINE)) || ((LARGE_A <= b) && (b <= LARGE_F)) || ((SMALL_A <= b) && (b <= SMALL_F));
    }

    @Override
    public ByteArrayOutputStream decode(byte[] bytes) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == PLUS) {
                out.write(SP);
            }
            if (bytes[i] == PERCENT && i + 2 < bytes.length) {
                byte hex1 = bytes[i + 1];
                byte hex2 = bytes[i + 2];
                if (isHexNumber(hex1) && isHexNumber(hex2)) {
                    String hexString = new String(new byte[]{hex1, hex2}, StandardCharsets.US_ASCII);
                    byte hexValue = Byte.parseByte(hexString, 16);
                    out.write(hexValue);
                    i = i + 2;
                }
            } else {
                out.write(bytes[i]);
            }
        }
        return out;
    }
}
