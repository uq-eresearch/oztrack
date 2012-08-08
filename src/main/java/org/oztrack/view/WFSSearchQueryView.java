package org.oztrack.view;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import net.opengis.wfs.FeatureCollectionType;
import net.opengis.wfs.WfsFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.emf.common.util.EList;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.gml2.GMLConfiguration;
import org.geotools.wfs.WFS;
import org.geotools.wfs.v1_1.WFSConfiguration;
import org.geotools.xml.Encoder;
import org.oztrack.data.model.SearchQuery;
import org.springframework.web.servlet.view.AbstractView;

public abstract class WFSSearchQueryView extends AbstractView {
    protected final Log logger = LogFactory.getLog(getClass());

    protected final String namespacePrefix = "oztrack";
    protected final String namespaceURI = "http://oztrack.org/xmlns#";
    
    protected final SearchQuery searchQuery;
    
    public WFSSearchQueryView(SearchQuery searchQuery) {
        this.searchQuery = searchQuery;
    }
    
    @Override
    protected void renderMergedOutputModel(
        @SuppressWarnings("rawtypes") Map model,
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        FeatureCollectionType featureCollectionType = WfsFactory.eINSTANCE.createFeatureCollectionType();
        @SuppressWarnings("unchecked")
        EList<SimpleFeatureCollection> featureCollections = featureCollectionType.getFeature();
        featureCollections.add(buildFeatureCollection());

        WFSConfiguration wfsConfiguration = new WFSConfiguration();
        @SuppressWarnings("unchecked")
        Set<QName> wfsConfigurationProperties = wfsConfiguration.getProperties();
        wfsConfigurationProperties.add(GMLConfiguration.NO_FEATURE_BOUNDS);
        Encoder encoder = new Encoder(wfsConfiguration);
        encoder.getNamespaces().declarePrefix(namespacePrefix, namespaceURI);
        encoder.setIndenting(true);

        response.setContentType("text/xml");
        response.setHeader("Content-Encoding", "gzip");
        GZIPOutputStream gzipOutputStream = null;
        try {
            gzipOutputStream = new GZIPOutputStream(response.getOutputStream());
            encoder.encode(featureCollectionType, WFS.FeatureCollection, gzipOutputStream);
        }
        catch (IOException e) {
            logger.error("Error writing output stream", e);
        }
        finally {
            IOUtils.closeQuietly(gzipOutputStream);
        }
    }

    protected abstract SimpleFeatureCollection buildFeatureCollection();
}