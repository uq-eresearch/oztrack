package org.oztrack.view;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.oztrack.data.model.types.OaiPmhRecord;
import org.oztrack.util.OaiPmhMetadataFormat;

// Implements GetRecord verb response format
// http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#GetRecord
public class OaiPmhGetRecordView extends OaiPmhView {
    private final OaiPmhMetadataFormat metadataFormat;
    private final OaiPmhRecord record;

    public OaiPmhGetRecordView(OaiPmhMetadataFormat metadataFormat, OaiPmhRecord record) {
        this.metadataFormat = metadataFormat;
        this.record = record;
    }

    @Override
    protected void writeMainElement(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement("GetRecord");
        OaiPmhRecordWriter writer = new OaiPmhRecordWriter(out, metadataFormat, false);
        writer.write(record);
        out.writeEndElement(); // GetRecord
    }
}