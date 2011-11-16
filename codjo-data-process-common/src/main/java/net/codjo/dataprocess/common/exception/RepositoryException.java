/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.exception;
/**
 *
 */
public class RepositoryException extends DataProcessException {
    public RepositoryException(String message) {
        super(message);
    }


    public RepositoryException(Throwable cause) {
        super(cause);
    }


    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
