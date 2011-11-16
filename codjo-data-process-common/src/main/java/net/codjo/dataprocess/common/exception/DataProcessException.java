package net.codjo.dataprocess.common.exception;
/**
 *
 */
public class DataProcessException extends Exception {
    public DataProcessException(String message) {
        super(message);
    }


    public DataProcessException(Throwable cause) {
        super(cause);
    }


    public DataProcessException(String message, Throwable cause) {
        super(message, cause);
    }


    @Override
    public String getLocalizedMessage() {
        String localizedMessage = super.getLocalizedMessage();

        if (getCause() != null && !localizedMessage.equals(getCause().getLocalizedMessage())) {
            return localizedMessage + ", causée par: " + getCause().getLocalizedMessage();
        }
        else {
            return localizedMessage;
        }
    }
}
