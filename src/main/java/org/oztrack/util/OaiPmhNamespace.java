package org.oztrack.util;

public class OaiPmhNamespace {
    public final String nsPrefix;
    public final String nsUri;
    public final String xsdUri;
    public OaiPmhNamespace(String nsPrefix, String nsUri, String xsdUri) {
        this.nsPrefix = nsPrefix;
        this.nsUri = nsUri;
        this.xsdUri = xsdUri;
    }
}