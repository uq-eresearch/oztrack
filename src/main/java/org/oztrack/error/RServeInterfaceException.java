package org.oztrack.error;

public class RServeInterfaceException extends Exception {
    public RServeInterfaceException(String message) {
        super(message);
    }
    
    public RServeInterfaceException(String message, Throwable cause) {
        super(message, cause);
    }
}