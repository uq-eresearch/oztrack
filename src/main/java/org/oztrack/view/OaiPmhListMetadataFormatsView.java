package org.oztrack.view;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.oztrack.util.OaiPmhConstants;
import org.oztrack.util.OaiPmhMetadataFormat;

// Implements ListMetadataFormats verb response format
// http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListMetadataFormats
public class OaiPmhListMetadataFormatsView extends OaiPmhView {
    @Override
    protected void writeMainElement(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement("ListMetadataFormats");
        for (OaiPmhMetadataFormat metadataFormat : OaiPmhConstants.supportedMetadataFormats) {
            out.writeStartElement("metadataFormat");

            out.writeStartElement("metadataPrefix");
            out.writeCharacters(metadataFormat.nsPrefix);
            out.writeEndElement(); // metadataPrefix

            out.writeStartElement("schema");
            out.writeCharacters(metadataFormat.xsdUri);
            out.writeEndElement(); // schema

            out.writeStartElement("metadataNamespace");
            out.writeCharacters(metadataFormat.nsUri);
            out.writeEndElement(); // metadataNamespace

            out.writeEndElement(); // metadataFormat
        }
        out.writeEndElement();
    }
}