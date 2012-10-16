package org.oztrack.app;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class OzTrackJettyServer {
    public static void main(String[] args) throws Exception {
        WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        context.setResourceBase("src/main/webapp");
        context.setParentLoaderPriority(true);
        context.setInitParameter("org.eclipse.jetty.servlet.SessionIdPathParameterName", "none");
        Server server = new Server(8181);
        server.setHandler(context);
        server.start();
        server.join();
    }
}