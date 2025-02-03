package webserver.writer.html.template;

import webserver.exception.HTTPException;
import webserver.writer.html.HTMLTag;
import webserver.writer.html.HTMLElement;
import webserver.writer.html.HTMLWriter;

public class ErrorPageWriter {
    public static String write(HTTPException exception) {
        String message = exception.getMessage();

        HTMLElement html = HTMLElement.Builder.tag(HTMLTag.DIV)
            .className("error-page")
            .appendChild(
                HTMLElement.Builder.tag(HTMLTag.H1)
                    .appendChild(HTMLElement.Builder.value("Error " + exception.getStatusCode()))
                    .build()
            )
            .appendChild(
                HTMLElement.Builder.tag(HTMLTag.P)
                    .appendChild(HTMLElement.Builder.value("Reason: " + message))
                    .build()
            )
            .appendChild(
                    HTMLElement.Builder.tag(HTMLTag.DIV)
                            .appendChild(
                                    HTMLElement.Builder.tag(HTMLTag.A)
                                            .appendChild(HTMLElement.Builder.value("Back to previous page"))
                                            .href("javascript:history.back()")
                                            .build()
                            )
                            .build()
            )
            .build();
        return HTMLWriter.render(html);
    }
}
