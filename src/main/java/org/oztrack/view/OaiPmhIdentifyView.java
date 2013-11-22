package org.oztrack.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang3.StringUtils;

// Implements Identify verb response format
// http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#Identify
public class OaiPmhIdentifyView extends OaiPmhView {
    private SimpleDateFormat utcDateTimeFormat;

    private final String baseUrl;
    private final String repositoryName;
    private final String adminEmail;
    private final Date earliestDate;

    public OaiPmhIdentifyView(String baseUrl, String repositoryName, String adminEmail, Date earliestDate) {
        this.utcDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        utcDateTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.baseUrl = baseUrl;
        this.repositoryName = repositoryName;
        this.adminEmail = adminEmail;
        this.earliestDate = earliestDate;
    }

    @Override
    protected void writeMainElement(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement("Identify");

        if (StringUtils.isNotBlank(repositoryName)) {
            out.writeStartElement("repositoryName");
            out.writeCharacters(repositoryName);
            out.writeEndElement(); // repositoryName
        }

        if (StringUtils.isNotBlank(baseUrl)) {
            out.writeStartElement("baseURL");
            out.writeCharacters(baseUrl);
            out.writeEndElement(); // baseURL
        }

        out.writeStartElement("protocolVersion");
        out.writeCharacters("2.0");
        out.writeEndElement(); // protocolVersion

        if (StringUtils.isNotBlank(adminEmail)) {
            out.writeStartElement("adminEmail");
            out.writeCharacters(adminEmail);
            out.writeEndElement(); // adminEmail
        }

        if (earliestDate != null) {
            out.writeStartElement("earliestDatestamp");
            out.writeCharacters(utcDateTimeFormat.format(earliestDate));
            out.writeEndElement(); // earliestDatestamp
        }

        out.writeStartElement("deletedRecord");
        out.writeCharacters("transient");
        out.writeEndElement(); // deletedRecord

        out.writeStartElement("granularity");
        out.writeCharacters("YYYY-MM-DDThh:mm:ssZ");
        out.writeEndElement(); // granularity

        out.writeEndElement(); // Identify
    }
}