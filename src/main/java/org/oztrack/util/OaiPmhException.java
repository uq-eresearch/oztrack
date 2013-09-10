package org.oztrack.util;

@SuppressWarnings("serial")
public class OaiPmhException extends Exception {
    private final String code;

    public OaiPmhException(String code, String message) {
        super(message);
        this.code = code;
    }

    public OaiPmhException(String code) {
        this(code, null);
    }

    public String getCode() {
        return code;
    }
}
