package server;

import servlets.Servlet;

/**
 * This interface defines the methods of the HTTP server we have implemented.
 */
public interface HTTPServer extends Runnable{
    public void addServlet(String httpCommanmd, String uri, Servlet s);
    public void removeServlet(String httpCommanmd, String uri);
    public void start();
    public void close();
}
