package org.oztrack.error;
/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 9/08/11
 * Time: 10:52 AM
 * sorry
 */
public class DataSpaceInterfaceException extends Exception {

   private String message;

    public DataSpaceInterfaceException(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}



