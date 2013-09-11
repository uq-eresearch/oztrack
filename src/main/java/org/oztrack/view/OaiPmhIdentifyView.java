package org.oztrack.view;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.oztrack.util.StaxUtil;

// Implements Identify verb response format
// http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#Identify
public class OaiPmhIdentifyView extends OaiPmhView {
    private final String repositoryName;
    private final String baseUrl;
    private final String adminEmail;

    public OaiPmhIdentifyView(String repositoryName, String baseUrl, String adminEmail) {
        this.repositoryName = repositoryName;
        this.baseUrl = baseUrl;
        this.adminEmail = adminEmail;
    }

    @Override
    protected void writeMainElement(XMLStreamWriter out) throws XMLStreamException {
        String earliestDatestamp = "1970-01-01T00:00:00Z"; // TODO: Query from database
        out.writeStartElement("Identify");
        StaxUtil.writeSimpleElement(out, "repositoryName", repositoryName);
        StaxUtil.writeSimpleElement(out, "baseURL", baseUrl);
        StaxUtil.writeSimpleElement(out, "protocolVersion", "2.0");
        StaxUtil.writeSimpleElement(out, "adminEmail", adminEmail);
        StaxUtil.writeSimpleElement(out, "earliestDatestamp", earliestDatestamp);
        StaxUtil.writeSimpleElement(out, "deletedRecord", "transient");
        StaxUtil.writeSimpleElement(out, "granularity", "YYYY-MM-DDThh:mm:ssZ");
        out.writeEndElement();
    }
}