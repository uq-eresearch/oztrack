package org.oztrack.error;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 9/08/11
 * Time: 10:52 AM
 */
public class RServeInterfaceException extends Exception {

   private String message;

    public RServeInterfaceException(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
