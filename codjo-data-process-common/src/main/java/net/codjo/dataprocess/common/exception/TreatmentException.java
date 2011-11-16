/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.exception;
/**
 *
 */
public class TreatmentException extends DataProcessException {
    public TreatmentException(String message) {
        super(message);
    }


    public TreatmentException(Throwable cause) {
        super(cause);
    }


    public TreatmentException(String message, Throwable cause) {
        super(message, cause);
    }
}
