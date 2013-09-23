package org.oztrack.view;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.oztrack.data.access.impl.OaiPmhRepositoryRecordProducer;
import org.oztrack.util.OaiPmhMetadataFormat;

// Implements ListIdentifiers and ListRecords verb response formats
// http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListIdentifiers
// http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListRecords
public class OaiPmhListIdentifiersOrListRecordsView extends OaiPmhView {
    private final String verb;
    private final OaiPmhMetadataFormat metadataFormat;
    private final OaiPmhRepositoryRecordProducer recordProducer;

    public OaiPmhListIdentifiersOrListRecordsView(
        String verb,
        OaiPmhMetadataFormat metadataFormat,
        OaiPmhRepositoryRecordProducer recordProducer
    ) {
        this.verb = verb;
        this.metadataFormat = metadataFormat;
        this.recordProducer = recordProducer;
    }

    @Override
    protected void writeMainElement(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(verb);
        boolean headerOnly = verb.equals("ListIdentifiers");
        OaiPmhRecordWriter writer = new OaiPmhRecordWriter(out, metadataFormat, headerOnly);
        writer.write(recordProducer);
        out.writeEndElement(); // verb
    }
}