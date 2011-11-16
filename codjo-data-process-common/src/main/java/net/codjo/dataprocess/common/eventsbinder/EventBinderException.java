/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.eventsbinder;
import java.lang.annotation.Annotation;
/**
 * 
 */
public class EventBinderException extends Exception {
    public EventBinderException(String message) {
        super(message);
    }


    public EventBinderException(String message, Throwable cause) {
        super(message, cause);
    }


    public EventBinderException(Throwable cause) {
        super(cause);
    }

    public static String printNotABindAnnotation(Annotation currentAnnotation) {
        return "Annotation '" + currentAnnotation.annotationType().getName()
        + "' is not an event annotation (no @BindAnnotation found on it)";
    }
}
