package org.oztrack.error;

public class RserveInterfaceException extends Exception {
    private static final long serialVersionUID = 4210425478239958555L;

    public RserveInterfaceException(String message) {
        super(message);
    }

    public RserveInterfaceException(String message, Throwable cause) {
        super(message, cause);
    }
}