package servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import server.RequestParser.RequestInfo;

public class HtmlLoader implements Servlet {

    private String htmlRootDir;

    public HtmlLoader(String htmlRootDir) {
        this.htmlRootDir = htmlRootDir;
    }

    @Override
    public void handle(RequestInfo requestInfo, OutputStream toClient) throws IOException {
        // Extract the HTML filename from the URI
        String uri = requestInfo.getUri();
        String[] uriComponents = requestInfo.getUriSegments();

        if (uriComponents.length < 2) {
            sendErrorResponse(toClient, "Invalid URL");
            return;
        }

        String htmlFileName = uriComponents[uriComponents.length - 1];
        Path filePath = Paths.get(htmlRootDir, htmlFileName);

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
        // No resources to close
    }

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