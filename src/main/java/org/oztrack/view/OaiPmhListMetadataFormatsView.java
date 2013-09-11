package org.oztrack.view;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.oztrack.util.OaiPmhConstants;
import org.oztrack.util.OaiPmhMetadataFormat;
import org.oztrack.util.StaxUtil;

// Implements ListMetadataFormats verb response format
// http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListMetadataFormats
public class OaiPmhListMetadataFormatsView extends OaiPmhView {
    @Override
    protected void writeMainElement(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement("ListMetadataFormats");
        for (OaiPmhMetadataFormat metadataFormat : OaiPmhConstants.supportedMetadataFormats) {
            out.writeStartElement("metadataFormat");
            StaxUtil.writeSimpleElement(out, "metadataPrefix", metadataFormat.nsPrefix);
            StaxUtil.writeSimpleElement(out, "schema", metadataFormat.xsdUri);
            StaxUtil.writeSimpleElement(out, "metadataNamespace", metadataFormat.nsUri);
            out.writeEndElement();
        }
        out.writeEndElement();
    }
}