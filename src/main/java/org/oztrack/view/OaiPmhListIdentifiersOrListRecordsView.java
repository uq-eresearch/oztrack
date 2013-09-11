package org.oztrack.view;

import static org.oztrack.util.OaiPmhConstants.DC;
import static org.oztrack.util.OaiPmhConstants.OAI_DC;
import static org.oztrack.util.OaiPmhConstants.XSI;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

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
        if (metadataPrefix.equals(OAI_DC.nsPrefix)) {
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
            out.writeStartElement(OAI_DC.nsPrefix, "dc", OAI_DC.nsUri);
            out.setPrefix(OAI_DC.nsPrefix, OAI_DC.nsUri);
            out.writeNamespace(OAI_DC.nsPrefix, OAI_DC.nsUri);
            out.setPrefix(DC.nsPrefix, DC.nsUri);
            out.writeNamespace(DC.nsPrefix, DC.nsUri);
            out.writeAttribute(XSI.nsUri, "schemaLocation", OAI_DC.nsUri + " " + OAI_DC.xsdUri);
            writeSimpleElement(out, DC.nsUri, "title", oztrackRepositoryTitle);
            writeSimpleElement(out, DC.nsUri, "description", oztrackRepositoryDescription);
            writeSimpleElement(out, DC.nsUri, "creator", oztrackRepositoryCreator);
            writeSimpleElement(out, DC.nsUri, "date", oztrackRepositoryStartDate);
            writeSimpleElement(out, DC.nsUri, "type", "Service");
            writeSimpleElement(out, DC.nsUri, "identifier", oztrackRepositoryIdentifier);
            writeSimpleElement(out, DC.nsUri, "language", "english");
            out.writeEndElement();
            out.writeEndElement();
        }
        out.writeEndElement();
    }
}