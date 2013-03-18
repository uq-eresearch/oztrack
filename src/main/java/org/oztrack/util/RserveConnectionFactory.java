package org.oztrack.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.oztrack.error.RserveInterfaceException;
import org.rosuda.REngine.Rserve.RConnection;

public class RserveConnectionFactory extends BasePoolableObjectFactory<RConnection> {
    protected final Log logger = LogFactory.getLog(getClass());

    @Override
    public RConnection makeObject() throws Exception {
        logger.info("Creating Rserve connection");
        if (!RserveUtils.checkLocalRserve()) {
            throw new RserveInterfaceException("Error starting Rserve.");
        }
        RConnection rConnection = null;
        try {
            rConnection = createConnection();
            loadLibraries(rConnection);
            loadScripts(rConnection);
        }
        catch (Exception e) {
            logger.error("Error setting up Rserve connection.");
            try {rConnection.close();} catch (Exception e2) {};
            throw e;
        }
        return rConnection;
    }

    @Override
    public void destroyObject(RConnection rConnection) {
        logger.info("Destroying Rserve connection");
        rConnection.close();
    }

    @Override
    public boolean validateObject(RConnection rConnection) {
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
            logger.error("Error validating R connection", e);
            return false;
        }
    }

    private RConnection createConnection() throws RserveInterfaceException {
        try {
            RConnection rConnection = new RConnection();
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
            "plotKML"
        };
        for (String library : libraries) {
            try {
                rConnection.voidEval("library(" + library + ")");
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
            "speedfilter.r"
        };
        for (String scriptFileName : scriptFileNames) {
            String scriptString = null;
            try {
                scriptString = IOUtils.toString(getClass().getResourceAsStream("/r/" + scriptFileName), "UTF-8");
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