package servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import server.RequestParser.RequestInfo;
/**
 * This class handles the incoming requests for main application and sends the HTML file to the client.
 */
public class HtmlLoader implements Servlet {

    private final String htmlRootDir;

    public HtmlLoader(String htmlRootDir) {
        this.htmlRootDir = htmlRootDir;
    }

    /**
     * Handles an HTTP request by extracting the HTML filename from the URI, checking if the file exists,
     * reading the file content, sending the HTTP response headers, sending the file content, and flushing the
     * OutputStream to ensure all content is sent.
     *
     * @param  requestInfo  the RequestInfo object containing information about the HTTP request
     * @param  toClient     the OutputStream to write the HTTP response to
     * @throws IOException  if an I/O error occurs while reading the file or writing to the OutputStream
     */
    @Override
    public void handle(RequestInfo requestInfo, OutputStream toClient) throws IOException {
        // Extract the HTML filename from the URI
        String uri = requestInfo.getUri();
        String[] uriComponents = requestInfo.getUriSegments();

        String htmlFileName;
        Path filePath;
        if(uriComponents.length == 1 && uriComponents[0].equals("app")) {
            htmlFileName = "index.html";
            filePath = Paths.get("html_files", htmlFileName);  
        }else if (uriComponents.length >1 ) {
            htmlFileName = uriComponents[uriComponents.length - 1]; 
            filePath = Paths.get("html_files", htmlFileName);                       
        }else{
            htmlFileName = "index.html";
            filePath = Paths.get("html_files", htmlFileName);
        }

        if (uriComponents.length < 1) {
            sendErrorResponse(toClient, "Invalid URL");
            return;
        }

        //String htmlFileName = uriComponents[uriComponents.length - 1];
        //Path filePath = Paths.get(htmlRootDir, htmlFileName);

        // Check if the file exists
        if (!Files.exists(filePath)) {
            sendErrorResponse(toClient, "File not found: " + htmlFileName);
            return;
        }

        // Read the file content
        byte[] fileContent = Files.readAllBytes(filePath);

        // Send the HTTP response headers
        toClient.write(("HTTP/1.1 200 OK\r\n").getBytes());
        toClient.write(("Content-Type: text/html\r\n").getBytes());
        toClient.write(("Content-Length: " + fileContent.length + "\r\n").getBytes());
        toClient.write(("\r\n").getBytes());

        // Send the file content
        toClient.write(fileContent);

        // Flush the toClient to ensure all content is sent
        toClient.flush();
    }

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub
        // No resources to close
    }

    /**
     * Sends an error response to the client for an Invalid URL.
     *
     * @param  toClient    the output stream to write the error response to
     * @param  errorMessage   the error message to include in the response
     * @throws IOException if an I/O error occurs while writing the response
     */
    private void sendErrorResponse(OutputStream toClient, String errorMessage) throws IOException {
        String response = "<html><body><h2>" + errorMessage + "</h2></body></html>";
        byte[] responseBytes = response.getBytes();

        toClient.write(("HTTP/1.1 404 Not Found\r\n").getBytes());
        toClient.write(("Content-Type: text/html\r\n").getBytes());
        toClient.write(("Content-Length: " + responseBytes.length + "\r\n").getBytes());
        toClient.write(("\r\n").getBytes());
        toClient.write(responseBytes);
        toClient.flush();
    }

}