package webserver.decoders;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static util.ByteConst.*;

public class PercentDecoder implements ByteDecoder {
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
