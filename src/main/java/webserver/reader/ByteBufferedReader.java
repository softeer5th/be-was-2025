package webserver.reader;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ByteBufferedReader {
    private BufferedInputStream inputStream;
    byte [] buffer;
    int pos = 0;

    public ByteBufferedReader(BufferedInputStream inputStream, int bufferSize) throws IOException {
        this.inputStream = inputStream;
        this.buffer = new byte[bufferSize];
        this.inputStream.read(buffer, 0, bufferSize);
    }

    public boolean hasNext() {
        return pos < buffer.length;
    }

    public ByteArrayOutputStream readUntil(byte target) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (; pos < buffer.length; pos++) {
            byte b = buffer[pos];
            if (b == target) {
                pos++;
                break;
            }
            out.write(b);
        }
        return out;
    }
}
