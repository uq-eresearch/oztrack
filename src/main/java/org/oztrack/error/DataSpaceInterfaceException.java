package org.oztrack.error;

public class DataSpaceInterfaceException extends Exception {
	private static final long serialVersionUID = -6224201396104632760L;

	private String message;

    public DataSpaceInterfaceException(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}