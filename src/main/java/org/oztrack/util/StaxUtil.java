package org.oztrack.util;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class StaxUtil {
    public static void writeSimpleElement(XMLStreamWriter out, String localName, String text) throws XMLStreamException {
        out.writeStartElement(localName);
        out.writeCharacters(text);
        out.writeEndElement();
    }

    public static void writeSimpleElement(XMLStreamWriter out, String namespaceURI, String localName, String text) throws XMLStreamException {
        out.writeStartElement(namespaceURI, localName);
        out.writeCharacters(text);
        out.writeEndElement();
    }
}
