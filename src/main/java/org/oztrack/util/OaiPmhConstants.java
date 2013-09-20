package org.oztrack.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OaiPmhConstants {
    public static OaiPmhNamespace XSI = new OaiPmhNamespace(
        "xsi",
        "http://www.w3.org/2001/XMLSchema-instance",
        null
    );
    public static OaiPmhNamespace OAI_PMH = new OaiPmhNamespace(
        null,
        "http://www.openarchives.org/OAI/2.0/",
        "http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd"
    );
    public static OaiPmhNamespace DC = new OaiPmhNamespace(
        "dc",
        "http://purl.org/dc/elements/1.1/",
        "http://dublincore.org/schemas/xmls/simpledc20021212.xsd"
    );
    public static OaiPmhMetadataFormat OAI_DC = new OaiPmhMetadataFormat(
        "oai_dc",
        "http://www.openarchives.org/OAI/2.0/oai_dc/",
        "http://www.openarchives.org/OAI/2.0/oai_dc.xsd"
    );
    public static OaiPmhMetadataFormat RIF_CS = new OaiPmhMetadataFormat(
        "rif",
        "http://ands.org.au/standards/rif-cs/registryObjects",
        "http://services.ands.org.au/documentation/rifcs/schema/registryObjects.xsd"
    );
    public static List<OaiPmhMetadataFormat> supportedMetadataFormats = new ArrayList<OaiPmhMetadataFormat>(Arrays.asList(
        OAI_DC,
        RIF_CS
    ));
}
