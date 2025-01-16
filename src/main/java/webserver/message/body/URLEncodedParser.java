package webserver.message.body;

import util.ByteConst;
import util.HeterogeneousContainer;
import webserver.decoders.ByteDecoder;
import webserver.decoders.PercentDecoder;
import webserver.enumeration.HTTPStatusCode;
import webserver.exception.HTTPException;
import webserver.message.HTTPRequest;
import webserver.reader.ByteStreamReader;

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
    public HeterogeneousContainer parse(HTTPRequest request, InputStream inputStream) {
        try {
            HeterogeneousContainer body = new HeterogeneousContainer(new LinkedHashMap<>());
            int bodyLength = request.getHeader("content-length", Integer.class)
                    .orElseThrow(() -> new HTTPException.Builder()
                            .causedBy(URLEncodedParser.class)
                            .message(request.getUri())
                            .statusCode(HTTPStatusCode.LENGTH_REQUIRED)
                            .build());
            ByteStreamReader reader = new ByteStreamReader(inputStream, bodyLength);
            while (reader.hasNext()) {
                ByteArrayOutputStream name = reader.readUntil(ByteConst.EQUAL);
                String nameString = this.decoder.decode(name.toByteArray()).toString("UTF-8");
                ByteArrayOutputStream value = reader.readUntil(ByteConst.AMPERSAND);
                String valueString = this.decoder.decode(value.toByteArray()).toString("UTF-8");
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
