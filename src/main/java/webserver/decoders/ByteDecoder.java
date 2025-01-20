package webserver.decoders;

import java.io.ByteArrayOutputStream;

public interface ByteDecoder {
    ByteArrayOutputStream decode(byte [] bytes);
}
