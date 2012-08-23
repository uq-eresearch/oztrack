package org.oztrack.error;

public class FileProcessingException extends Exception {
    private static final long serialVersionUID = 1629590417322111765L;

    public FileProcessingException(String message) {
        super(message);
    }

    public FileProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}