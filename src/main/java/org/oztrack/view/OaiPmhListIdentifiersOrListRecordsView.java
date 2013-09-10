package org.oztrack.view;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.oztrack.util.OaiPmhConstants;

// Implements ListIdentifiers and ListRecords verb response formats
// http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListIdentifiers
// http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListRecords
public class OaiPmhListIdentifiersOrListRecordsView extends OaiPmhView {
    private final String verb;
    private final String metadataPrefix;

    public OaiPmhListIdentifiersOrListRecordsView(String verb, String metadataPrefix) {
        this.verb = verb;
        this.metadataPrefix = metadataPrefix;
    }

    @Override
    protected void writeMainElement(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(verb);
        if (metadataPrefix.equals(OaiPmhConstants.OAI_DC.nsPrefix)) {
            writeOaiDcRepositoryRecordElement(out);
        }
        out.writeEndElement();
    }

    private void writeOaiDcRepositoryRecordElement(XMLStreamWriter out) throws XMLStreamException {
        final String oztrackRepositoryTitle = "OzTrack";
        final String oztrackRepositoryDescription =
            "OzTrack is a free-to-use web-based platform for analysing and visualising " +
            "individual-based animal location data. Upload your tracking data now.";
        final String oztrackRepositoryCreator = "The University of Queensland";
        final String oztrackRepositoryStartDate = "2011-11-02T03:47:24Z";
        final String oztrackRepositoryIdentifier = "http://oztrack.org/id/repository";
        out.writeStartElement("record");
        out.writeStartElement("header");
        writeSimpleElement(out, "identifier", oztrackRepositoryIdentifier);
        writeSimpleElement(out, "datestamp", oztrackRepositoryStartDate);
        out.writeEndElement();
        if (verb.equals("ListRecords")) {
            out.writeStartElement("metadata");
            out.writeStartElement(OaiPmhConstants.OAI_DC.nsPrefix, "dc", OaiPmhConstants.OAI_DC.nsUri);
            out.setPrefix(OaiPmhConstants.OAI_DC.nsPrefix, OaiPmhConstants.OAI_DC.nsUri);
            out.writeNamespace(OaiPmhConstants.OAI_DC.nsPrefix, OaiPmhConstants.OAI_DC.nsUri);
            out.setPrefix(OaiPmhConstants.DC.nsPrefix, OaiPmhConstants.DC.nsUri);
            out.writeNamespace(OaiPmhConstants.DC.nsPrefix, OaiPmhConstants.DC.nsUri);
            out.writeAttribute(OaiPmhConstants.XSI.nsUri, "schemaLocation", OaiPmhConstants.OAI_DC.nsUri + " " + OaiPmhConstants.OAI_DC.xsdUri);
            writeSimpleElement(out, OaiPmhConstants.DC.nsUri, "title", oztrackRepositoryTitle);
            writeSimpleElement(out, OaiPmhConstants.DC.nsUri, "description", oztrackRepositoryDescription);
            writeSimpleElement(out, OaiPmhConstants.DC.nsUri, "creator", oztrackRepositoryCreator);
            writeSimpleElement(out, OaiPmhConstants.DC.nsUri, "date", oztrackRepositoryStartDate);
            writeSimpleElement(out, OaiPmhConstants.DC.nsUri, "type", "Service");
            writeSimpleElement(out, OaiPmhConstants.DC.nsUri, "identifier", oztrackRepositoryIdentifier);
            writeSimpleElement(out, OaiPmhConstants.DC.nsUri, "language", "english");
            out.writeEndElement();
            out.writeEndElement();
        }
        out.writeEndElement();
    }
}