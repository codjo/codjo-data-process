/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.exception;
/**
 *
 */
public class UserManagerException extends DataProcessException {
    public UserManagerException(String message) {
        super(message);
    }


    public UserManagerException(Throwable cause) {
        super(cause);
    }


    public UserManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
