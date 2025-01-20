package webserver.message.body;

import util.ByteConst;
import util.HeterogeneousContainer;
import webserver.decoders.ByteDecoder;
import webserver.decoders.PercentDecoder;
import webserver.enumeration.HTTPStatusCode;
import webserver.exception.HTTPException;
import webserver.reader.ByteBufferedReader;
import webserver.reader.ByteStreamReader;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;

public class URLEncodedParser implements BodyParser {
    private ByteDecoder decoder;
    public URLEncodedParser() {
        this.decoder = new PercentDecoder();
    }
    @Override
    public HeterogeneousContainer parse(HeterogeneousContainer headers, BufferedInputStream inputStream) {
        try {
            HeterogeneousContainer body = new HeterogeneousContainer(new LinkedHashMap<>());
            final int bodyLength = headers.get("content-length", Integer.class)
                    .orElseThrow(() -> new HTTPException.Builder()
                            .causedBy(URLEncodedParser.class)
                            .statusCode(HTTPStatusCode.LENGTH_REQUIRED)
                            .build());
            ByteBufferedReader reader = new ByteBufferedReader(inputStream, bodyLength);
            while (reader.hasNext()) {
                byte [] name = reader.readUntil(ByteConst.EQUAL).toByteArray();
                byte [] value = reader.readUntil(ByteConst.AMPERSAND).toByteArray() ;
                String nameString = this.decoder.decode(name).toString("UTF-8");
                String valueString = this.decoder.decode(value).toString("UTF-8");
                System.out.println(nameString + ": " + valueString);
                body.put(nameString, valueString);
            }
            return body;
        } catch (IOException e) {
            throw new HTTPException.Builder()
                    .causedBy(URLEncodedParser.class)
                    .internalServerError(e.getMessage());
        }
    }
}
