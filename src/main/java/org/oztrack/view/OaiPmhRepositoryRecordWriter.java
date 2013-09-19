package org.oztrack.view;

import static org.oztrack.util.OaiPmhConstants.DC;
import static org.oztrack.util.OaiPmhConstants.OAI_DC;
import static org.oztrack.util.OaiPmhConstants.RIF_CS;
import static org.oztrack.util.OaiPmhConstants.XSI;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.oztrack.util.OaiPmhMetadataFormat;
import org.oztrack.util.StaxUtil;

public class OaiPmhRepositoryRecordWriter {
    private static final String oztrackRifCsGroup = "OzTrack";
    private static final String oztrackRepositoryTitle = "OzTrack";
    private static final String oztrackRepositoryDescription =
        "OzTrack is a free-to-use web-based platform for analysing and visualising " +
        "individual-based animal location data. Upload your tracking data now.";
    private static final String oztrackRepositoryCreator = "The University of Queensland";
    private static final String oztrackRepositoryCreateDate = "2011-11-02T03:47:24Z";
    private static final String oztrackRepositoryUpdateDate = oztrackRepositoryCreateDate;
    private static final String oztrackRepositoryIdentifier = "http://oztrack.org/id/repository";
    private static final String oztrackRepositoryUrl = "http://oztrack.org/";

    private final boolean headerOnly;
    private final OaiPmhMetadataFormat metadataFormat;

    public OaiPmhRepositoryRecordWriter(boolean headerOnly, OaiPmhMetadataFormat metadataFormat) {
        this.headerOnly = headerOnly;
        this.metadataFormat = metadataFormat;
    }

    public void write(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement("record");

        out.writeStartElement("header");
        StaxUtil.writeSimpleElement(out, "identifier", oztrackRepositoryIdentifier);
        StaxUtil.writeSimpleElement(out, "datestamp", oztrackRepositoryUpdateDate);
        out.writeEndElement(); // header

        if (!headerOnly) {
            out.writeStartElement("metadata");
            if (metadataFormat.equals(OAI_DC)) {
                writeOaiDcRepositoryMetadataElement(out);
            }
            else if (metadataFormat.equals(RIF_CS)) {
                writeRifCsRepositoryMetadataElement(out);
            }
            out.writeEndElement(); // metadata
        }

        out.writeEndElement(); // record
    }

    private void writeOaiDcRepositoryMetadataElement(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(OAI_DC.nsPrefix, "dc", OAI_DC.nsUri);

        // Every metadata part must include xmlns attributes for its metadata formats.
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#Record
        out.setPrefix(OAI_DC.nsPrefix, OAI_DC.nsUri);
        out.writeNamespace(OAI_DC.nsPrefix, OAI_DC.nsUri);
        out.setPrefix(DC.nsPrefix, DC.nsUri);
        out.writeNamespace(DC.nsPrefix, DC.nsUri);

        // Every metadata part must include the attributes xmlns:xsi (namespace URI for XML schema) and
        // xsi:schemaLocation (namespace URI and XML schema URL for validating metadata that follows).
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#Record
        out.setPrefix(XSI.nsPrefix, XSI.nsUri);
        out.writeNamespace(XSI.nsPrefix, XSI.nsUri);
        out.writeAttribute(XSI.nsUri, "schemaLocation", OAI_DC.nsUri + " " + OAI_DC.xsdUri);

        StaxUtil.writeSimpleElement(out, DC.nsUri, "title", oztrackRepositoryTitle);
        StaxUtil.writeSimpleElement(out, DC.nsUri, "description", oztrackRepositoryDescription);
        StaxUtil.writeSimpleElement(out, DC.nsUri, "creator", oztrackRepositoryCreator);
        StaxUtil.writeSimpleElement(out, DC.nsUri, "created", oztrackRepositoryCreateDate);
        StaxUtil.writeSimpleElement(out, DC.nsUri, "date", oztrackRepositoryUpdateDate);
        StaxUtil.writeSimpleElement(out, DC.nsUri, "type", "Service");
        StaxUtil.writeSimpleElement(out, DC.nsUri, "identifier", oztrackRepositoryIdentifier);
        StaxUtil.writeSimpleElement(out, DC.nsUri, "language", "english");

        out.writeEndElement(); // dc
    }

    private void writeRifCsRepositoryMetadataElement(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(RIF_CS.nsPrefix, "registryObjects", RIF_CS.nsUri);

        // Every metadata part must include xmlns attributes for its metadata formats.
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#Record
        out.setPrefix(RIF_CS.nsPrefix, RIF_CS.nsUri);
        out.writeNamespace(RIF_CS.nsPrefix, RIF_CS.nsUri);

        // Every metadata part must include the attributes xmlns:xsi (namespace URI for XML schema) and
        // xsi:schemaLocation (namespace URI and XML schema URL for validating metadata that follows).
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#Record
        out.setPrefix(XSI.nsPrefix, XSI.nsUri);
        out.writeNamespace(XSI.nsPrefix, XSI.nsUri);
        out.writeAttribute(XSI.nsUri, "schemaLocation", RIF_CS.nsUri + " " + RIF_CS.xsdUri);

        out.writeStartElement(RIF_CS.nsUri, "registryObject");
        out.writeAttribute("group", oztrackRifCsGroup);

        // TODO: Do not use the identifier for an object as the key for a metadata record describing
        // that object - the metadata record needs its own unique separate identifier.
        // http://ands.org.au/guides/cpguide/cpgidentifiers.html
        StaxUtil.writeSimpleElement(out, RIF_CS.nsUri, "key", oztrackRepositoryIdentifier);

        out.writeStartElement(RIF_CS.nsUri, "originatingSource");
        out.writeAttribute("type", "authoritative");
        out.writeCharacters(oztrackRepositoryUrl);
        out.writeEndElement(); // originatingSource

        out.writeStartElement(RIF_CS.nsUri, "service");
        out.writeAttribute("type", "report");
        out.writeAttribute("dateModified", oztrackRepositoryUpdateDate);

        out.writeStartElement(RIF_CS.nsUri, "identifier");
        out.writeAttribute("type", "uri");
        out.writeCharacters(oztrackRepositoryIdentifier);
        out.writeEndElement(); // identifier

        out.writeStartElement(RIF_CS.nsUri, "name");
        out.writeAttribute("type", "primary");
        StaxUtil.writeSimpleElement(out, RIF_CS.nsUri, "namePart", oztrackRepositoryTitle);
        out.writeEndElement(); // name

        StaxUtil.writeSimpleElement(out, RIF_CS.nsUri, "description", oztrackRepositoryDescription);

        out.writeStartElement(RIF_CS.nsUri, "location");
        out.writeStartElement(RIF_CS.nsUri, "address");
        out.writeStartElement(RIF_CS.nsUri, "electronic");
        out.writeAttribute("type", "url");
        StaxUtil.writeSimpleElement(out, RIF_CS.nsUri, "value", oztrackRepositoryUrl);
        out.writeEndElement(); // electronic
        out.writeEndElement(); // address
        out.writeEndElement(); // location

        out.writeStartElement(RIF_CS.nsUri, "existenceDates");
        out.writeStartElement(RIF_CS.nsUri, "startDate");
        out.writeAttribute("dateFormat", "W3CDTF");
        out.writeCharacters(oztrackRepositoryCreateDate);
        out.writeEndElement(); // startDate
        out.writeEndElement(); //existenceDates

        out.writeEndElement(); // service

        out.writeEndElement(); // registryObject

        out.writeEndElement(); // registryObjects
    }
}
