package org.oztrack.util;

import java.util.Arrays;
import java.util.List;

import org.oztrack.data.model.types.OaiPmhRecord;

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
    public static List<OaiPmhMetadataFormat> supportedMetadataFormats = Arrays.asList(
        OAI_DC,
        RIF_CS
    );
    public static final String repositoryServiceLocalIdentifier = "service";
    public static final String oaiPmhServiceLocalIdentifier = "oai-pmh";
    public static final String repositoryCollectionLocalIdentifier = "collection";
    public static List<OaiPmhRecord.Subject> defaultRecordSubjects = Arrays.asList(
        new OaiPmhRecord.Subject("anzsrc-for", "0502"), // Environmental Science and Management
        new OaiPmhRecord.Subject("anzsrc-for", "0602"), // Ecology
        new OaiPmhRecord.Subject("anzsrc-for", "0608"), // Zoology
        new OaiPmhRecord.Subject("local", "Animal Tracking")
    );
}
