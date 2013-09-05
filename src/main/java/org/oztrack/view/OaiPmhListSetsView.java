package org.oztrack.view;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

// Implements ListSets verb response format
// http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListSets
public class OaiPmhListSetsView extends OaiPmhView {
    @Override
    protected void writeMainElement(XMLStreamWriter out) throws XMLStreamException {
        out.writeEmptyElement("ListSets");
    }
}