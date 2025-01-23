package webserver.resolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.enumeration.HTTPContentType;
import webserver.exception.HTTPException;
import webserver.message.HTTPRequest;
import webserver.message.HTTPResponse;
import webserver.writer.html.template.ErrorPageWriter;

public class ExceptionResolver implements ResourceResolver {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionResolver.class);
    private ResourceResolver resolver;

    public ExceptionResolver(ResourceResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void resolve(HTTPRequest request, HTTPResponse.Builder response) {
        try {
            this.resolver.resolve(request, response);
        }catch (HTTPException e) {
            logger.error(e.getMessage(), e);
            String body = ErrorPageWriter.write(e);
            response.body(body.getBytes());
            response.statusCode(e.getStatusCode());
            response.contentType(HTTPContentType.TEXT_HTML);
        }
    }
}
