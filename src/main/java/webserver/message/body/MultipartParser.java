package webserver.message.body;

import util.HeterogeneousContainer;
import webserver.enumeration.HTTPContentType;
import webserver.exception.HTTPException;
import webserver.message.header.records.ContentTypeRecord;
import webserver.message.record.FileRecord;
import webserver.reader.ByteStreamReader;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class MultipartParser implements BodyParser {
    private static class BoundaryReader {
        private BufferedInputStream in;
        private final byte [] delimiter;
        private byte [] closeDelimiter;
        private boolean isFinish;
        private BoundaryReader(BufferedInputStream inputStream, String boundary) {
            this.in = inputStream;
            this.delimiter = ("\r\n--" + boundary).getBytes();
            this.closeDelimiter = ("\r\n--" + boundary + "--").getBytes();
            this.isFinish = false;
        }

        boolean isFinish() {
            return isFinish;
        }

        ByteArrayOutputStream readUntilBoundary() throws IOException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Queue<Integer> waitingQueue = new LinkedList<Integer>();
            int matchedCount = 0;
            int b;
            while((b = in.read()) != -1) {
                if (b == delimiter[matchedCount]) {
                    matchedCount++;
                    waitingQueue.add(b);
                    if (matchedCount == delimiter.length) {
                        break;
                    }
                }
                else {
                    while (!waitingQueue.isEmpty()) {
                        out.write(waitingQueue.poll());
                    }
                    out.write(b);
                }
            }
            if (matchedCount == delimiter.length) {
                int f = in.read();
                int s = in.read();
                if (f == closeDelimiter[matchedCount] && s == closeDelimiter[matchedCount + 1]) {
                    isFinish = true;
                }
            }
            return out;
        }
    }

    private Map<String, String> parseDisposition(String headerLine) {
        Map<String, String> headerAttributes = new HashMap<>();
        String [] splited = headerLine.split(":", 2);
        if (splited.length != 2) {
            throw new IllegalArgumentException("Invalid header line: " + headerLine);
        }
        String [] attributes = splited[1].split(";");
        for (int i = 1 ; i < attributes.length ; i++) {
            String trimmed = attributes[i].trim();
            String [] keyValue = trimmed.split("=", 2);
            if (keyValue.length != 2) {
                throw new IllegalArgumentException("Invalid header line: " + headerLine);
            }
            String key = keyValue[0];
            String value = keyValue[1];
            if (value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length() - 1);
            }
            headerAttributes.put(key, value);
        }
        return headerAttributes;
    }

    @Override
    public HeterogeneousContainer parse(HeterogeneousContainer headers, BufferedInputStream inputStream) {
        try {

            HeterogeneousContainer body = new HeterogeneousContainer(new HashMap<>());
            ContentTypeRecord contentType = headers.get("content-type", ContentTypeRecord.class)
                    .orElseThrow(() -> new HTTPException.Builder()
                            .causedBy(MultipartParser.class)
                            .badRequest("content-type parse error"));

            ByteStreamReader lineReader = new ByteStreamReader(inputStream);
            BoundaryReader boundaryReader = new BoundaryReader(inputStream, contentType.boundary());
            lineReader.readLine();
            while (!boundaryReader.isFinish()) {
                // multiform 본문 내부 파트 헤더
                HTTPContentType dataContentType = HTTPContentType.TEXT_PLAIN;
                String contentDispositionLine = lineReader.readLine();
                Map<String, String> disposition = parseDisposition(contentDispositionLine);
                String contentTypeLine = lineReader.readLine();
                if (!contentTypeLine.isEmpty()) {
                    dataContentType = HTTPContentType.fromFullType(contentTypeLine);
                    lineReader.readLine(); // 데이터 시작 부분 공백 부분 제거
                }
                ByteArrayOutputStream bodyContent = boundaryReader.readUntilBoundary();

                String name = disposition.get("name");
                if (dataContentType == HTTPContentType.TEXT_PLAIN) {
                    body.put(disposition.get("name"), bodyContent.toString(StandardCharsets.UTF_8));
                    System.out.println("name: " + disposition.get("name"));
                    System.out.println("body: " + bodyContent.toString(StandardCharsets.UTF_8));
                    continue;
                }
                if (HTTPContentType.isImage(dataContentType)) {
                    FileRecord record = new FileRecord(
                            bodyContent.toByteArray(),
                            dataContentType,
                            disposition.getOrDefault("filename","")
                    );
                    body.put(name, record, FileRecord.class);
                }
            }
            return body;
        } catch (IOException e) {
            throw new HTTPException.Builder()
                    .causedBy(MultipartParser.class)
                    .badRequest(e.getMessage());
        }
    }
}
