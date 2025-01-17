package webserver.reader;

import util.ByteConst;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ByteStreamReader {
    private BufferedInputStream inputStream;
    public ByteStreamReader(BufferedInputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String readLine() throws IOException {
        ByteArrayOutputStream out = readByteLine();
        return out.toString();
    }

    public ByteArrayOutputStream readByteLine() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int b;
        while ( (b = inputStream.read()) != -1) {
            if (b != ByteConst.CR) {
                out.write(b);
                continue;
            }
            int next = inputStream.read();
            if (next == -1) {
                out.write(ByteConst.CR);
                break;
            }
            if (next == ByteConst.LF) {
                break;
            }
            out.write(b);
            out.write(next);
        }
        return out;
    }
}
