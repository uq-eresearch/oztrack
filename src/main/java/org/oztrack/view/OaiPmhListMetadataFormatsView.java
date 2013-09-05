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
            writeSimpleElement(out, "metadataPrefix", metadataFormat.nsPrefix);
            writeSimpleElement(out, "schema", metadataFormat.xsdUri);
            writeSimpleElement(out, "metadataNamespace", metadataFormat.nsUri);
            out.writeEndElement();
        }
        out.writeEndElement();
    }
}