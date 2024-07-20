package servlets;

import java.io.IOException;
import java.io.OutputStream;
import server.RequestParser.RequestInfo;

public class CalculateServlet implements Servlet {

	@Override
	public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
		// TODO Auto-generated method stub
		byte[] content = ri.getContent();
	}
	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}
}
