package org.oztrack.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.oztrack.util.OaiPmhConstants;
import org.springframework.web.servlet.view.AbstractView;

// Implements the OAI-PMH XML Response Format
// http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#XMLResponse
public abstract class OaiPmhView extends AbstractView {
    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Encoding of the XML must use the UTF-8 representation of Unicode.
        response.setCharacterEncoding("UTF-8");

        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
        XMLStreamWriter out = xmlOutputFactory.createXMLStreamWriter(response.getWriter());

        // The first tag output is an XML declaration where the version is always 1.0 and the encoding is always UTF-8.
        out.writeStartDocument("UTF-8", "1.0");

        // The root element has the name OAI-PMH and must define the namespace URI and schema URL for OAI-PMH.
        out.writeStartElement("OAI-PMH");
        out.setDefaultNamespace(OaiPmhConstants.OAI_PMH.nsUri);
        out.writeDefaultNamespace(OaiPmhConstants.OAI_PMH.nsUri);
        out.setPrefix(OaiPmhConstants.XSI.nsPrefix, OaiPmhConstants.XSI.nsUri);
        out.writeNamespace(OaiPmhConstants.XSI.nsPrefix, OaiPmhConstants.XSI.nsUri);
        out.writeAttribute(OaiPmhConstants.XSI.nsUri, "schemaLocation", OaiPmhConstants.OAI_PMH.nsUri + " " + OaiPmhConstants.OAI_PMH.xsdUri);

        // The first child of the root element is the responseDate element, indicating the UTC datetime that the response was sent.
        SimpleDateFormat utcDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        utcDateTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String responseDate = utcDateTimeFormat.format(new Date());
        writeSimpleElement(out, "responseDate", responseDate);

        // The second child of the root element is the request element, indicating the protocol request that generated this response.
        // The content of the request element must always be the base URL of the protocol request;
        // The attribute values must correspond to the key=value pairs of protocol request.
        String baseUrl = request.getRequestURL().toString();
        out.writeStartElement("request");
        if (shouldIncludeRequestArguments()) {
            for (Entry<String, String[]> parameterEntry : request.getParameterMap().entrySet()) {
                out.writeAttribute(parameterEntry.getKey(), parameterEntry.getValue()[0]);
            }
        }
        out.writeCharacters(baseUrl);
        out.writeEndElement();

        // The third child of the root element is either:
        // an error element that must be used in case of an error or exception condition;
        // an element with the same name as the verb of the respective OAI-PMH request.
        writeMainElement(out);

        out.writeEndElement();
        out.writeEndDocument();
    }

    protected boolean shouldIncludeRequestArguments() {
        // In cases where the request that generated this response did not result in an error or exception condition,
        // the attributes and attribute values of the request element must match the key=value pairs of the protocol request.
        return true;
    }

    protected abstract void writeMainElement(XMLStreamWriter out) throws XMLStreamException;

    protected static void writeSimpleElement(XMLStreamWriter out, String localName, String text) throws XMLStreamException {
        out.writeStartElement(localName);
        out.writeCharacters(text);
        out.writeEndElement();
    }

    protected static void writeSimpleElement(XMLStreamWriter out, String namespaceURI, String localName, String text) throws XMLStreamException {
        out.writeStartElement(namespaceURI, localName);
        out.writeCharacters(text);
        out.writeEndElement();
    }
}