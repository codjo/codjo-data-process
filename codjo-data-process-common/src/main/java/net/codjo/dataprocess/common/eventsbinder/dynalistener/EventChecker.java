package net.codjo.dataprocess.common.eventsbinder.dynalistener;
import java.util.EventObject;
/**
 * This interface check the Event characteristics and return if the eventType is amanged or not.
 */
public interface EventChecker {

    boolean checkEvent(EventObject eventObject, String methodCalled);
}
