package org.oztrack.view;

import static org.oztrack.util.OaiPmhConstants.DC;
import static org.oztrack.util.OaiPmhConstants.OAI_DC;
import static org.oztrack.util.OaiPmhConstants.RIF_CS;
import static org.oztrack.util.OaiPmhConstants.XSI;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.oztrack.util.OaiPmhMetadataFormat;
import org.oztrack.util.StaxUtil;

public class OaiPmhRecordWriter {
    private SimpleDateFormat utcDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private final XMLStreamWriter out;
    private final OaiPmhMetadataFormat metadataFormat;
    private final boolean headerOnly;

    public OaiPmhRecordWriter(XMLStreamWriter out, OaiPmhMetadataFormat metadataFormat, boolean headerOnly) {
        this.out = out;
        this.metadataFormat = metadataFormat;
        this.headerOnly = headerOnly;
    }

    public void write(OaiPmhRecordProducer producer) throws XMLStreamException {
        for (OaiPmhRecord record : producer) {
            write(record);
        }
    }

    private void write(OaiPmhRecord record) throws XMLStreamException {
        if (record.getOaiPmhIdentifier() == null) {
            throw new IllegalArgumentException("Record must have OAI-PMH identifier");
        }

        out.writeStartElement("record");

        out.writeStartElement("header");

        // A unique identifier unambiguously identifies an item within a repository.
        // The format of the unique identifier must correspond to that of the URI syntax.
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#UniqueIdentifier
        StaxUtil.writeSimpleElement(out, "identifier", record.getOaiPmhIdentifier());

        // Date of creation, modification or deletion of the record for the purpose of selective harvesting.
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#Record
        Date datestampDate =
            (record.getUpdateDate() != null) ? record.getUpdateDate() :
            (record.getCreateDate() != null) ? record.getCreateDate() :
            null;
        if (datestampDate != null) {
            StaxUtil.writeSimpleElement(out, "datestamp", utcDateTimeFormat.format(datestampDate));
        }

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

        if (record.getObjectIdentifier() != null) {
            StaxUtil.writeSimpleElement(out, DC.nsUri, "identifier", record.getObjectIdentifier());
        }
        if (record.getTitle() != null) {
            StaxUtil.writeSimpleElement(out, DC.nsUri, "title", record.getTitle());
        }
        if (record.getDescription() != null) {
            StaxUtil.writeSimpleElement(out, DC.nsUri, "description", record.getDescription());
        }
        if (record.getCreator() != null) {
            StaxUtil.writeSimpleElement(out, DC.nsUri, "creator", record.getCreator());
        }
        if (record.getCreateDate() != null) {
            StaxUtil.writeSimpleElement(out, DC.nsUri, "created", utcDateTimeFormat.format(record.getCreateDate()));
        }
        if (record.getUpdateDate() != null) {
            StaxUtil.writeSimpleElement(out, DC.nsUri, "date", utcDateTimeFormat.format(record.getUpdateDate()));
        }
        if (record.getDcType() != null) {
            StaxUtil.writeSimpleElement(out, DC.nsUri, "type", record.getDcType());
        }
        if (record.getIsPartOfObjectIdentifier() != null) {
            out.writeStartElement(DC.nsUri, "relation");
            out.writeAttribute("type", "isPartOf");
            out.writeCharacters(record.getIsPartOfObjectIdentifier());
            out.writeEndElement(); // relation
        }
        if (record.getIsPresentedByObjectIdentifier() != null) {
            out.writeStartElement(DC.nsUri, "relation");
            out.writeAttribute("type", "isPresentedBy");
            out.writeCharacters(record.getIsPresentedByObjectIdentifier());
            out.writeEndElement(); // relation
        }
        if (record.getIsAvailableThroughObjectIdentifier() != null) {
            out.writeStartElement(DC.nsUri, "relation");
            out.writeAttribute("type", "isAvailableThrough");
            out.writeCharacters(record.getIsAvailableThroughObjectIdentifier());
            out.writeEndElement(); // relation
        }

        out.writeEndElement(); // dc
    }

    private void writeRifCsRepositoryMetadataElement(OaiPmhRecord record) throws XMLStreamException {
        if (record.getRifCsObjectElemName() == null) {
            throw new IllegalArgumentException("Record must have RIF-CS object element name");
        }

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

        if (record.getRifCsGroup() != null) {
            out.writeAttribute("group", record.getRifCsGroup());
        }

        // Do not use the identifier for an object as the key for a metadata record describing
        // that object - the metadata record needs its own unique separate identifier.
        // http://ands.org.au/guides/cpguide/cpgidentifiers.html
        if (record.getObjectIdentifier() != null) {
            StaxUtil.writeSimpleElement(out, RIF_CS.nsUri, "key", record.getObjectIdentifier());
        }

        if (record.getUrl() != null) {
            out.writeStartElement(RIF_CS.nsUri, "originatingSource");
            out.writeAttribute("type", "authoritative");
            out.writeCharacters(record.getUrl());
            out.writeEndElement(); // originatingSource
        }

        out.writeStartElement(RIF_CS.nsUri, record.getRifCsObjectElemName());

        if (record.getRifCsObjectTypeAttr() != null) {
            out.writeAttribute("type", record.getRifCsObjectTypeAttr());
        }

        if (record.getUpdateDate() != null) {
            out.writeAttribute("dateModified", utcDateTimeFormat.format(record.getUpdateDate()));
        }

        if (record.getObjectIdentifier() != null) {
            out.writeStartElement(RIF_CS.nsUri, "identifier");
            out.writeAttribute("type", "uri");
            out.writeCharacters(record.getObjectIdentifier());
            out.writeEndElement(); // identifier
        }

        if (record.getTitle() != null) {
            out.writeStartElement(RIF_CS.nsUri, "name");
            out.writeAttribute("type", "primary");
            StaxUtil.writeSimpleElement(out, RIF_CS.nsUri, "namePart", record.getTitle());
            out.writeEndElement(); // name
        }

        if (record.getDescription() != null) {
            StaxUtil.writeSimpleElement(out, RIF_CS.nsUri, "description", record.getDescription());
        }

        if (record.getUrl() != null) {
            out.writeStartElement(RIF_CS.nsUri, "location");
            out.writeStartElement(RIF_CS.nsUri, "address");
            out.writeStartElement(RIF_CS.nsUri, "electronic");
            out.writeAttribute("type", "url");
            StaxUtil.writeSimpleElement(out, RIF_CS.nsUri, "value", record.getUrl());
            out.writeEndElement(); // electronic
            out.writeEndElement(); // address
            out.writeEndElement(); // location
        }

        if (record.getCreateDate() != null) {
            out.writeStartElement(RIF_CS.nsUri, "existenceDates");
            out.writeStartElement(RIF_CS.nsUri, "startDate");
            out.writeAttribute("dateFormat", "W3CDTF");
            out.writeCharacters(utcDateTimeFormat.format(record.getCreateDate()));
            out.writeEndElement(); // startDate
            out.writeEndElement(); //existenceDates
        }

        if (record.getIsPartOfObjectIdentifier() != null) {
            out.writeStartElement(RIF_CS.nsUri, "relatedObject");
            out.writeStartElement(RIF_CS.nsUri, "key");
            out.writeCharacters(record.getIsPartOfObjectIdentifier());
            out.writeEndElement(); // key
            out.writeStartElement(RIF_CS.nsUri, "relation");
            out.writeAttribute("type", "isPartOf");
            out.writeEndElement(); // relation
            out.writeEndElement(); // relatedObject
        }
        if (record.getIsPresentedByObjectIdentifier() != null) {
            out.writeStartElement(RIF_CS.nsUri, "relatedObject");
            out.writeStartElement(RIF_CS.nsUri, "key");
            out.writeCharacters(record.getIsPresentedByObjectIdentifier());
            out.writeEndElement(); // key
            out.writeStartElement(RIF_CS.nsUri, "relation");
            out.writeAttribute("type", "isPresentedBy");
            out.writeEndElement(); // relation
            out.writeEndElement(); // relatedObject
        }
        if (record.getIsAvailableThroughObjectIdentifier() != null) {
            out.writeStartElement(RIF_CS.nsUri, "relatedObject");
            out.writeStartElement(RIF_CS.nsUri, "key");
            out.writeCharacters(record.getIsAvailableThroughObjectIdentifier());
            out.writeEndElement(); // key
            out.writeStartElement(RIF_CS.nsUri, "relation");
            out.writeAttribute("type", "isAvailableThrough");
            out.writeEndElement(); // relation
            out.writeEndElement(); // relatedObject
        }

        out.writeEndElement(); // service

        out.writeEndElement(); // registryObject

        out.writeEndElement(); // registryObjects
    }
}
