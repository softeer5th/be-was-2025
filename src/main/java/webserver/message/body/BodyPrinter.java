package webserver.message.body;


import util.HeterogeneousContainer;
import webserver.exception.HTTPException;
import webserver.message.header.records.ContentTypeRecord;
import webserver.reader.ByteStreamReader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.HashMap;

public class BodyPrinter implements BodyParser {
    @Override
    public HeterogeneousContainer parse(HeterogeneousContainer headers, BufferedInputStream inputStream) {
        ByteStreamReader reader = new ByteStreamReader(inputStream);
        ContentTypeRecord contentType = headers.get("content-type", ContentTypeRecord.class)
                .orElseThrow(() -> new HTTPException.Builder()
                        .causedBy(BodyPrinter.class)
                        .badRequest("content-type parse error"));
        final String boundaryLine = "--" + contentType.boundary();
        final String finishLine = boundaryLine + "--";
        String line = "";
        System.out.println("finish line : "+ finishLine);
        int lineNumber = 0;
        try {
            while (!(line = reader.readLine()).equals(finishLine)) {
                System.out.println(line);
                lineNumber++;
                if (lineNumber >= 100) {
                    break;
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return new HeterogeneousContainer(new HashMap<>());
    }
}
