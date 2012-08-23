package org.oztrack.error;

public class RServeInterfaceException extends Exception {
    private static final long serialVersionUID = 4210425478239958555L;

    public RServeInterfaceException(String message) {
        super(message);
    }

    public RServeInterfaceException(String message, Throwable cause) {
        super(message, cause);
    }
}