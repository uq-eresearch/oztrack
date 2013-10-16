package org.oztrack.view;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.oztrack.data.access.OaiPmhEntityProducer;
import org.oztrack.data.model.types.OaiPmhSet;

public class OaiPmhListSetsView extends OaiPmhView {
    private OaiPmhEntityProducer<OaiPmhSet> setProducer;

    public OaiPmhListSetsView(OaiPmhEntityProducer<OaiPmhSet> setProducer) {
        this.setProducer = setProducer;
    }

    @Override
    protected void writeMainElement(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement("ListSets");
        for (OaiPmhSet set : setProducer) {
            out.writeStartElement("set");
            {
                out.writeStartElement("setSpec");
                out.writeCharacters(set.getSetSpec());
                out.writeEndElement(); // setSpec
            }
            {
                out.writeStartElement("setName");
                out.writeCharacters(set.getSetName());
                out.writeEndElement(); // setName
            }
            out.writeEndElement(); // set
        }
        out.writeEndElement(); // ListSets
    }
}
