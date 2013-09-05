package org.oztrack.view;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

// Implements Error and Exception Conditions response format
// http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ErrorConditions
public class OaiPmhErrorView extends OaiPmhView {
    private final String code;
    private final String message;

    public OaiPmhErrorView(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    protected boolean shouldIncludeRequestArguments() {
        // In cases where the request that generated this response resulted in a badVerb or badArgument error condition,
        // the repository must return the base URL of the protocol request only. Attributes must not be provided in these cases.
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#XMLResponse
        return !(code.equals("badVerb") || code.equals("badArgument"));
    }

    @Override
    protected void writeMainElement(XMLStreamWriter out) throws XMLStreamException {
        // In event of an error or exception condition, repositories must include one or more error elements in the response.
        // Each error element must have a code attribute and may have a string value to provide information to a human reader.
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ErrorConditions
        out.writeStartElement("error");
        out.writeAttribute("code", code);
        if (message != null) {
            out.writeCharacters(message);
        }
        out.writeEndElement();
    }
}