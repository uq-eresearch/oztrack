package org.oztrack.util;

import java.net.InetAddress;

import org.apache.commons.io.IOUtils;
import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;
import org.apache.log4j.Logger;
import org.oztrack.error.RserveInterfaceException;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.Rserve.RConnection;

public class RserveConnectionFactory extends BaseKeyedPoolableObjectFactory<String, RConnection> {
    private final Logger logger = Logger.getLogger(getClass());

    @Override
    public RConnection makeObject(String host) throws Exception {
        logger.info("Creating Rserve connection to " + host);
        if (InetAddress.getByName(host).isLoopbackAddress()) {
            if (!RserveUtils.checkLocalRserve()) {
                throw new RserveInterfaceException("Error starting Rserve on " + host);
            }
        }
        RConnection rConnection = null;
        try {
            rConnection = createConnection(host);
            loadLibraries(rConnection);
            loadScripts(rConnection);
        }
        catch (Exception e) {
            logger.error("Error setting up Rserve connection to " + host, e);
            try {rConnection.close();} catch (Exception e2) {};
            throw e;
        }
        logger.info("Successfully created Rserve connection to " + host);
        return rConnection;
    }

    @Override
    public void destroyObject(String host, RConnection rConnection) {
        logger.info("Destroying Rserve connection to " + host);
        rConnection.close();
    }

    @Override
    public boolean validateObject(String host, RConnection rConnection) {
        // We can't use RConnection.isConnected() here because, from the doc:
        // "currently this state is not checked on-the-spot, that is if connection
        // went down by an outside event this is not reflected by the flag".
        // We evaluate a simple expression to test the connection and Rserve
        // itself are working.
        try {
            rConnection.eval("1");
            return true;
        }
        catch (Exception e) {
            try {rConnection.close();} catch (Exception e2) {};
            logger.error("Error validating R connection to " + host, e);
            return false;
        }
    }

    private RConnection createConnection(String host) throws RserveInterfaceException {
        try {
            RConnection rConnection = new RConnection(host);
            rConnection.setSendBufferSize(10 * 1024 * 1024);
            return rConnection;
        }
        catch (Exception e) {
            throw new RserveInterfaceException("Error connecting to Rserve.", e);
        }
    }

    private void loadLibraries(RConnection rConnection) throws RserveInterfaceException {
        String[] libraries = new String[] {
            "adehabitatHR",
            "adehabitatMA",
            "shapefiles",
            "rgdal",
            "alphahull",
            "sp",
            "raster",
            "plyr",
            "spatstat",
            "maptools",
            "Grid2Polygons",
            "RColorBrewer",
            "googleVis",
            "spacetime",
            "plotKML",
            "kftrack",
            "ukfsst"
        };
        for (String library : libraries) {
            try {
                String cmd = "library(" + library + ")";
                REXP r = rConnection.parseAndEval("try(" + cmd + ",silent=TRUE)");
                if (r.inherits("try-error")) {
                    throw new RserveInterfaceException("Error loading '" + library + "' library: " + r.asString());
                }
            }
            catch (RserveInterfaceException e) {
                throw e;
            }
            catch (Exception e) {
                throw new RserveInterfaceException("Error loading '" + library + "' library.", e);
            }
        }
    }

    private void loadScripts(RConnection rConnection) throws RserveInterfaceException {
        String[] scriptFileNames = new String[] {
            "kmlPolygons.r",
            "elide2.r",
            "mcp.r",
            "kernelud.r",
            "kernelbb.r",
            "alphahull.r",
            "locoh.r",
            "heatmap.r",
            "speedfilter.r",
            "kftrack.r",
            "kalman.r",
            "kfsst.r"
        };
        for (String scriptFileName : scriptFileNames) {
            String scriptString = null;
            try {
                scriptString = IOUtils.toString(getClass().getResourceAsStream("/org/oztrack/r/" + scriptFileName), "UTF-8");
            }
            catch (Exception e) {
                throw new RserveInterfaceException("Error reading '" + scriptFileName + "' script.", e);
            }
            try {
                rConnection.voidEval(scriptString);
            }
            catch (Exception e) {
                throw new RserveInterfaceException("Error running '" + scriptFileName + "' script.", e);
            }
        }
    }
}