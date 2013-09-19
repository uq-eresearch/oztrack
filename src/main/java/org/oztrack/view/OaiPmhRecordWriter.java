package org.oztrack.view;

import static org.oztrack.util.OaiPmhConstants.DC;
import static org.oztrack.util.OaiPmhConstants.OAI_DC;
import static org.oztrack.util.OaiPmhConstants.RIF_CS;
import static org.oztrack.util.OaiPmhConstants.XSI;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.oztrack.util.OaiPmhMetadataFormat;
import org.oztrack.util.StaxUtil;

public class OaiPmhRecordWriter {
    private final XMLStreamWriter out;
    private final OaiPmhMetadataFormat metadataFormat;
    private final boolean headerOnly;

    public OaiPmhRecordWriter(XMLStreamWriter out, OaiPmhMetadataFormat metadataFormat, boolean headerOnly) {
        this.out = out;
        this.metadataFormat = metadataFormat;
        this.headerOnly = headerOnly;
    }

    public void write(OaiPmhRecord record) throws XMLStreamException {
        out.writeStartElement("record");

        out.writeStartElement("header");
        StaxUtil.writeSimpleElement(out, "identifier", record.getIdentifier());
        StaxUtil.writeSimpleElement(out, "datestamp", record.getUpdateDate());
        out.writeEndElement(); // header

        if (!headerOnly) {
            out.writeStartElement("metadata");
            if (metadataFormat.equals(OAI_DC)) {
                writeOaiDcRepositoryMetadataElement(record);
            }
            else if (metadataFormat.equals(RIF_CS)) {
                writeRifCsRepositoryMetadataElement(record);
            }
            out.writeEndElement(); // metadata
        }

        out.writeEndElement(); // record
    }

    private void writeOaiDcRepositoryMetadataElement(OaiPmhRecord record) throws XMLStreamException {
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

        StaxUtil.writeSimpleElement(out, DC.nsUri, "title", record.getTitle());
        StaxUtil.writeSimpleElement(out, DC.nsUri, "description", record.getDescription());
        StaxUtil.writeSimpleElement(out, DC.nsUri, "creator", record.getCreator());
        StaxUtil.writeSimpleElement(out, DC.nsUri, "created", record.getCreateDate());
        StaxUtil.writeSimpleElement(out, DC.nsUri, "date", record.getUpdateDate());
        StaxUtil.writeSimpleElement(out, DC.nsUri, "type", record.getDcType());
        StaxUtil.writeSimpleElement(out, DC.nsUri, "identifier", record.getIdentifier());

        out.writeEndElement(); // dc
    }

    private void writeRifCsRepositoryMetadataElement(OaiPmhRecord record) throws XMLStreamException {
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
        out.writeAttribute("group", record.getRifCsGroup());

        // TODO: Do not use the identifier for an object as the key for a metadata record describing
        // that object - the metadata record needs its own unique separate identifier.
        // http://ands.org.au/guides/cpguide/cpgidentifiers.html
        StaxUtil.writeSimpleElement(out, RIF_CS.nsUri, "key", record.getIdentifier());

        out.writeStartElement(RIF_CS.nsUri, "originatingSource");
        out.writeAttribute("type", "authoritative");
        out.writeCharacters(record.getUrl());
        out.writeEndElement(); // originatingSource

        out.writeStartElement(RIF_CS.nsUri, record.getRifCsObjectElemName());
        out.writeAttribute("type", record.getRifCsObjectTypeAttrValue());
        out.writeAttribute("dateModified", record.getUpdateDate());

        out.writeStartElement(RIF_CS.nsUri, "identifier");
        out.writeAttribute("type", "uri");
        out.writeCharacters(record.getIdentifier());
        out.writeEndElement(); // identifier

        out.writeStartElement(RIF_CS.nsUri, "name");
        out.writeAttribute("type", "primary");
        StaxUtil.writeSimpleElement(out, RIF_CS.nsUri, "namePart", record.getTitle());
        out.writeEndElement(); // name

        StaxUtil.writeSimpleElement(out, RIF_CS.nsUri, "description", record.getDescription());

        out.writeStartElement(RIF_CS.nsUri, "location");
        out.writeStartElement(RIF_CS.nsUri, "address");
        out.writeStartElement(RIF_CS.nsUri, "electronic");
        out.writeAttribute("type", "url");
        StaxUtil.writeSimpleElement(out, RIF_CS.nsUri, "value", record.getUrl());
        out.writeEndElement(); // electronic
        out.writeEndElement(); // address
        out.writeEndElement(); // location

        out.writeStartElement(RIF_CS.nsUri, "existenceDates");
        out.writeStartElement(RIF_CS.nsUri, "startDate");
        out.writeAttribute("dateFormat", "W3CDTF");
        out.writeCharacters(record.getCreateDate());
        out.writeEndElement(); // startDate
        out.writeEndElement(); //existenceDates

        out.writeEndElement(); // service

        out.writeEndElement(); // registryObject

        out.writeEndElement(); // registryObjects
    }
}
