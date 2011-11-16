/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.eventsbinder.annotations.managers;
import net.codjo.dataprocess.common.eventsbinder.AnnotationManager;
import net.codjo.dataprocess.common.eventsbinder.annotations.events.OnAction;
import net.codjo.dataprocess.common.eventsbinder.dynalistener.EventChecker;
import java.awt.event.ActionListener;
import java.util.EventListener;
import java.util.EventObject;
/**
 * 
 */
public class OnActionAnnotationManager extends AnnotationManager<OnAction> {
    @Override
    public Class<?extends EventListener> getListenerClass(OnAction currentAnnotation) {
        return ActionListener.class;
    }


    @Override
    public String[] getBoundPropertiesNames(OnAction currentAnnotation) {
        return currentAnnotation.propertiesBound();
    }


    @Override
    public EventChecker createEventChecker(OnAction currentAnnotation) {
        return new EventChecker() {
                public boolean checkEvent(EventObject eventObject, String methodCalled) {
                    return true;
                }
            };
    }
}
