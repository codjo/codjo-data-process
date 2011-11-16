package net.codjo.dataprocess.common.eventsbinder.dynalistener;
import java.util.EventObject;
/**
 *
 */
public interface EventReaction {

    void reactToAnEvent(EventObject eventObject, String methodCalled);
}
