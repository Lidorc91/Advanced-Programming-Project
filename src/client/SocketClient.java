package client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClient {
    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 8080;

        // Send GET request to load the HTML file
        sendGetRequest(hostname, port, "/app/html_files/index.html");

        // Send POST request to upload a file
        sendPostRequest(hostname, port, "/upload");
    }

    private static void sendGetRequest(String hostname, int port, String path) {
        try (Socket socket = new Socket(hostname, port)) {
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            String request = "GET " + path + " HTTP/1.1\r\n"
                           + "Host: " + hostname + "\r\n"
                           + "Connection: keep-alive\r\n"
                           + "\r\n";

            writer.print(request);
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void sendPostRequest(String hostname, int port, String path) {
        try (Socket socket = new Socket(hostname, port)) {
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            String data = "filename=myConfigFile.txt&content=This is the content of the file.";

            String request = "POST " + path + " HTTP/1.1\r\n"
                           + "Host: " + hostname + "\r\n"
                           + "Content-Type: application/x-www-form-urlencoded\r\n"
                           + "Content-Length: " + data.length() + "\r\n"
                           + "Connection: close\r\n"
                           + "\r\n"
                           + data;

            writer.print(request);
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}