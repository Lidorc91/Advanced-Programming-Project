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
	HTTPServer server=new MyHTTPServer(8080,10);
	server.addServlet("GET", "/publish", new TopicDisplayer()); 
	server.addServlet("POST", "/upload", new ConfLoader());
	server.addServlet("GET", "/app/", new HtmlLoader("html_files"));
	server.start();
	System.in.read();
	server.close();
	Thread.sleep(400);
	//TODO - Check active thread at exit
	System.out.println("Active threads: " + Thread.activeCount());
	System.out.println("done"); 
	//TODO - Remove all debug messages
	}
}