package webserver.reader;

import util.ByteConst;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ByteStreamReader {
    private InputStream inputStream;
    private int lengthBudget;
    private int readLength;

    public ByteStreamReader(InputStream inputStream, int length) {
        this.inputStream = inputStream;
        this.lengthBudget = length;
        readLength = 0;
    }

    public ByteArrayOutputStream readUntil(byte target) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int b;
        while ( (b = readWithCount()) != -1) {
            if (b == target) {
                break;
            }
            out.write((byte)b);
        }
        return out;
    }

    public ByteArrayOutputStream readLine() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int b;
        while ( (b = readWithCount()) != -1) {
            if (b != ByteConst.CR) {
                out.write(b);
                continue;
            }
            int next = readWithCount();
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

    public boolean hasNext() {
        return readLength < lengthBudget;
    }

    private int readWithCount() throws IOException {
        int b = inputStream.read();
        if (b == -1 || lengthBudget <= readLength) {
            return -1;
        }
        readLength++;
        return b;
    }
}
