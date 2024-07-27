package project;


import server.HTTPServer;
import server.MyHTTPServer;
import servlets.*;

public class Main {
	/**
	 * The main function that starts the HTTP server and adds servlets for handling different requests.
	 *
	 * @param  args  the command line arguments
	 * @throws Exception  if there is an error starting the server
	 */
	 public static void main(String[] args) throws Exception{
	HTTPServer server=new MyHTTPServer(8080,5);
	server.addServlet("GET", "/publish", new TopicDisplayer()); 
	server.addServlet("POST", "/upload", new ConfLoader());
	server.addServlet("GET", "/app/", new HtmlLoader("html_files"));
	server.start();
	System.in.read();
	System.out.println("Active threads: " + Thread.activeCount());
	System.in.read();
	server.close();
	System.out.println("done"); 
	}
}