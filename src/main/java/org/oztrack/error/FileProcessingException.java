package org.oztrack.error;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 5/05/11
 * Time: 10:09 AM
 * sorry
 */
public class FileProcessingException extends Exception {

    private String message;

    public FileProcessingException(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
