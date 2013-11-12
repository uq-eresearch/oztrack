package org.oztrack.app;

import java.io.IOException;

import org.eclipse.jetty.http.HttpSchemes;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;

public class OzTrackJettyServer {
    public static void main(String[] args) throws Exception {
        Server server = new Server();

        SelectChannelConnector httpConnector = new SelectChannelConnector();
        httpConnector.setPort(8181);
        server.addConnector(httpConnector);

        SelectChannelConnector httpsConnector = new SelectChannelConnector() {
            @Override
            public void customize(EndPoint endpoint, Request request) throws IOException {
                request.setScheme(HttpSchemes.HTTPS);
                super.customize(endpoint, request);
            }
        };
        httpsConnector.setForwarded(true);
        httpsConnector.setPort(8553);
        server.addConnector(httpsConnector);

        WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        context.setResourceBase("src/main/webapp");
        context.setParentLoaderPriority(true);
        context.setInitParameter("org.eclipse.jetty.servlet.SessionIdPathParameterName", "none");
        server.setHandler(context);

        server.start();
        server.join();
    }
}