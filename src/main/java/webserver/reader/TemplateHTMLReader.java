package webserver.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TemplateHTMLReader implements AutoCloseable {
    private BufferedReader reader;
    public TemplateHTMLReader(InputStream inputStream) {
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
    }
    public String readUntil(char target) throws IOException {
        int ch;
        StringBuilder sb = new StringBuilder();
        while ((ch = this.reader.read()) != -1) {
            char c = (char) ch;
            if (c == target) {
                break;
            }
            sb.append(c);
        }
        return sb.toString();
    }
    public String readBraceValue() throws IOException {
        StringBuilder sb = new StringBuilder();
        int ch;
        while ((ch = this.reader.read()) != -1) {
            char c = (char) ch;
            if (c == '{') { break;}
        }
        while ((ch = this.reader.read()) != -1) {
            char c = (char) ch;
            if (c == '}') { break; }
            sb.append(c);
        }
        return sb.toString();
    }

    @Override
    public void close() throws IOException {
        this.reader.close();
    }
}
