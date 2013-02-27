package org.oztrack.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.oztrack.error.RServeInterfaceException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

public class RServeConnectionFactory extends BasePoolableObjectFactory<RConnection> {
    protected final Log logger = LogFactory.getLog(getClass());

    @Override
    public RConnection makeObject() throws RServeInterfaceException {
        logger.info("Creating RServe connection");
        RConnection rConnection = null;
        String rWorkingDir = null;
        if (StartRserve.checkLocalRserve()) {
            try {
                rConnection = new RConnection();
                rConnection.setSendBufferSize(10485760);
            }
            catch (RserveException e) {
                throw new RServeInterfaceException("Error starting Rserve.", e);
            }

            try {
                rWorkingDir = rConnection.eval("getwd()").asString() + File.separator;
            }
            catch (Exception e) {
                throw new RServeInterfaceException("Error getting Rserve working directory.", e);
            }
            String osname = System.getProperty("os.name");
            if (StringUtils.startsWith(osname, "Windows")) {
                rWorkingDir = rWorkingDir.replace("\\","/");
            }

            loadLibraries(rConnection);
            loadScripts(rConnection);
        }
        else {
            throw new RServeInterfaceException("Could not start Rserve.");
        }
        return rConnection;
    }

    @Override
    public void destroyObject(RConnection rConnection) {
        rConnection.close();
    }

    @Override
    public boolean validateObject(RConnection rConnection) {
        return rConnection.isConnected();
    }

    private void loadLibraries(RConnection rConnection) throws RServeInterfaceException {
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
            loadLibrary(rConnection, library);
        }
    }

    private void loadLibrary(RConnection rConnection, String library) throws RServeInterfaceException {
        try {
            rConnection.voidEval("library(" + library + ")");
        }
        catch (RserveException e) {
            throw new RServeInterfaceException("Error loading '" + library + "' library.", e);
        }
    }

    private void loadScripts(RConnection rConnection) throws RServeInterfaceException {
        String[] scriptFileNames = new String[] {
            "kmlPolygons.r",
            "mcp.r",
            "kernelud.r",
            "kernelbb.r",
            "alphahull.r",
            "locoh.r",
            "heatmap.r",
            "speedfilter.r"
        };
        for (String scriptFileName : scriptFileNames) {
            loadScript(rConnection, scriptFileName);
        }
    }

    private void loadScript(RConnection rConnection, String scriptFileName) throws RServeInterfaceException {
        String scriptString = null;
        try {
            scriptString = IOUtils.toString(getClass().getResourceAsStream("/r/" + scriptFileName), "UTF-8");
        }
        catch (IOException e) {
            throw new RServeInterfaceException("Error reading '" + scriptFileName + "' script.", e);
        }
        try {
            rConnection.voidEval(scriptString);
        }
        catch (RserveException e) {
            throw new RServeInterfaceException("Error running '" + scriptFileName + "' script.", e);
        }
    }
}