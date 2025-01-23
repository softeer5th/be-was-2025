package webserver;

import java.io.*;
import java.lang.reflect.Method;
import java.net.Socket;
import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;
import webserver.http.HttpStatus;
import webserver.http.MimeType;
import webserver.http.servlet.HttpServlet;
import webserver.http.servlet.exception.ServletException;
import webserver.http.servlet.ServletInfo;
import webserver.http.servlet.ServletMapper;
import webserver.support.HttpRequestParser;
import webserver.support.ResourceResolver;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private final Socket connection;
    private final ServletMapper servletManager;

    public RequestHandler(Socket connectionSocket, ServletMapper servletManager) {
        this.connection = connectionSocket;
        this.servletManager = servletManager;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest request = HttpRequestParser.parse(in);
            HttpResponse response = new HttpResponse(request, dos);
            request.setResponse(response);

            if (requestStaticResource(request)) {
                serveStaticResource(request, response);
            } else {
                handleServlet(request,response);
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void serveStaticResource(HttpRequest request, HttpResponse response) throws IOException {
        File file = ResourceResolver.getResource(request.getPath());
        response.setBody(file);
        response.setContentType(MimeType.getMimeType(file.getName()));
        response.setContentLength(file.length());
        response.setStatus(HttpStatus.OK);
        response.send();
    }

    private boolean requestStaticResource(HttpRequest request) throws IOException {
        if(!"GET".equals(request.getMethod())) return false;

        File file = ResourceResolver.getResource(request.getPath());
        return file.exists() && file.isFile();
    }

    private void handleServlet(HttpRequest request, HttpResponse response) throws Exception {
        try {
            ServletInfo servletInfo = servletManager.getServlet(request.getPath(), request.getMethod());
            String servletClassName = servletInfo.className();
            Class<HttpServlet> servletClass = (Class<HttpServlet>) Class.forName(servletClassName);
            Object servlet = servletClass.getDeclaredConstructor().newInstance();
            Method serviceMethod = servletClass.getSuperclass().getDeclaredMethod("service", HttpRequest.class, HttpResponse.class);
            serviceMethod.setAccessible(true);
            serviceMethod.invoke(servlet, request, response);
        } catch (ServletException e) {
            response.sendError(e.getStatus(), e.getMessage());
        } catch (Exception e) {
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        finally {
            response.send();
        }
    }
}
