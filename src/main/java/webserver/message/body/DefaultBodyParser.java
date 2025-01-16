package webserver.message.body;

import util.HeterogeneousContainer;
import webserver.enumeration.HTTPStatusCode;
import webserver.exception.HTTPException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class DefaultBodyParser implements BodyParser {

    @Override
    public HeterogeneousContainer parse(HeterogeneousContainer headers, InputStream inputStream) {
        HeterogeneousContainer body = new HeterogeneousContainer(new HashMap<>());
        int contentLength = headers.get("content-length", Integer.class)
                .orElseThrow(() -> new HTTPException.Builder()
                        .causedBy(DefaultBodyParser.class)
                        .statusCode(HTTPStatusCode.LENGTH_REQUIRED)
                        .build()
                );
        try {
            ByteArrayInputStream raw = new ByteArrayInputStream(inputStream.readNBytes(contentLength));
            body.put("CONTENT", raw, ByteArrayInputStream.class);
        } catch (IOException e) {
            throw new HTTPException.Builder()
                    .causedBy(DefaultBodyParser.class)
                    .internalServerError(e.getMessage());
        }
        return body;
    }
}
