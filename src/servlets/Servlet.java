package servlets;

import java.io.IOException;
import java.io.OutputStream;
import server.RequestParser.RequestInfo;

/**
 * This Interface defines the methods of a servlet, which is used to handle HTTP requests.
 * It mostly handles the request and sends the response to the client.
 */
public interface Servlet {
    void handle(RequestInfo ri, OutputStream toClient) throws IOException;
    void close() throws IOException;
}
