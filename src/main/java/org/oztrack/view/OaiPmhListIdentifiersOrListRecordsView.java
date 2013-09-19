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
        new OaiPmhRepositoryRecordWriter(verb.equals("ListRecords"), metadataFormat).write(out);
        out.writeEndElement(); // verb
    }
}