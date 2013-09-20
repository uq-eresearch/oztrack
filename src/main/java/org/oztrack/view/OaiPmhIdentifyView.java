package org.oztrack.view;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

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

        out.writeStartElement("repositoryName");
        out.writeCharacters(repositoryName);
        out.writeEndElement(); // repositoryName

        out.writeStartElement("baseURL");
        out.writeCharacters(baseUrl);
        out.writeEndElement(); // baseURL

        out.writeStartElement("protocolVersion");
        out.writeCharacters("2.0");
        out.writeEndElement(); // protocolVersion

        out.writeStartElement("adminEmail");
        out.writeCharacters(adminEmail);
        out.writeEndElement(); // adminEmail

        out.writeStartElement("earliestDatestamp");
        out.writeCharacters(earliestDatestamp);
        out.writeEndElement(); // earliestDatestamp

        out.writeStartElement("deletedRecord");
        out.writeCharacters("transient");
        out.writeEndElement(); // deletedRecord

        out.writeStartElement("granularity");
        out.writeCharacters("YYYY-MM-DDThh:mm:ssZ");
        out.writeEndElement(); // granularity

        out.writeEndElement(); // Identify
    }
}