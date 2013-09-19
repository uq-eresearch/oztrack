package org.oztrack.view;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.oztrack.util.OaiPmhMetadataFormat;

// Implements ListIdentifiers and ListRecords verb response formats
// http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListIdentifiers
// http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListRecords
public class OaiPmhListIdentifiersOrListRecordsView extends OaiPmhView {
    private final String verb;
    private final OaiPmhMetadataFormat metadataFormat;

    public OaiPmhListIdentifiersOrListRecordsView(String verb, OaiPmhMetadataFormat metadataFormat) {
        this.verb = verb;
        this.metadataFormat = metadataFormat;
    }

    @Override
    protected void writeMainElement(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(verb);
        OaiPmhRecord repositoryRecord = new OaiPmhRecord(
            "http://oztrack.org/id/repository",
            "OzTrack",
            "OzTrack is a free-to-use web-based platform for analysing and visualising individual-based animal location data.",
            "http://oztrack.org/",
            "The University of Queensland",
            "2011-11-02T03:47:24Z",
            "2011-11-02T03:47:24Z",
            "Service",
            "service",
            "report",
            "OzTrack"
        );
        new OaiPmhRecordWriter(out, metadataFormat, verb.equals("ListIdentifiers")).write(repositoryRecord);
        out.writeEndElement(); // verb
    }
}