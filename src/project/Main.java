package project;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import server.HTTPServer;
import server.MyHTTPServer;
import server.RequestParser.RequestInfo;
import servlets.Servlet;

public class Main {
	
	 public static void main(String[] args) throws IOException {   
		 HTTPServer server = new MyHTTPServer(3000,5);
			
	    	//server.addServlet("GET", "/api/calculate", new CalculateServlet());
			//server.addServlet("POST", "/upload", new CalculateServlet());
			//server.addServlet("GET", "/app/", new CalculateServlet());
			
			server.addServlet("GET", "/hello", new Servlet() {
		            @Override
		            public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
		                PrintWriter writer = new PrintWriter(toClient, true);

		                // Constructing the HTTP response
		                writer.println("HTTP/1.1 200 OK");
		                writer.println("Content-Type: text/html");
		                writer.println();
		                writer.println("<html><body>");
		                writer.println("<h1>Hello, World!</h1>");
		                writer.println("</body></html>");
		                writer.flush();
		            }

					@Override
					public void close() throws IOException {
						// TODO Auto-generated method stub
						
					}
			});

	        server.addServlet("GET", "/", new Servlet() { //Root URI servlet
	            @Override
	            public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
	                PrintWriter writer = new PrintWriter(toClient, true);

	                // Constructing the HTTP response
	                writer.println("HTTP/1.1 200 OK");
	                writer.println("Content-Type: text/html");
	                writer.println();
	                writer.println("<html><body>");
	                writer.println("<h1>My Cool New HomePage!</h1>");
	                writer.println("</body></html>");
	                writer.flush();
	            }

	            @Override
	            public void close() throws IOException {
	                // TODO Auto-generated method stub
	                
	            }
	    });

			int theards = Thread.activeCount();
			server.start();
			System.out.println("server started");
			theards = Thread.activeCount();
			System.in.read();
			server.close();
			theards = Thread.activeCount();
			System.out.println("server closed");
	        System.out.println("done");
	    } 
    
}
