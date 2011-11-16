/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.eventsbinder.annotations.managers;
import net.codjo.dataprocess.common.eventsbinder.AnnotationManager;
import net.codjo.dataprocess.common.eventsbinder.annotations.events.OnGenericEvent;
import net.codjo.dataprocess.common.eventsbinder.dynalistener.EventChecker;
import java.util.EventListener;
import java.util.EventObject;
/**
 * 
 */
public class OnGenericEventAnnotationManager extends AnnotationManager<OnGenericEvent> {
    @Override
    public Class<?extends EventListener> getListenerClass(OnGenericEvent currentAnnotation) {
        return currentAnnotation.listenerClass();
    }


    @Override
    public String[] getBoundPropertiesNames(OnGenericEvent currentAnnotation) {
        return currentAnnotation.propertiesBound();
    }


    @Override
    public EventChecker createEventChecker(OnGenericEvent currentAnnotation) {
        final String methodFilter = currentAnnotation.methodFilter();
        return new EventChecker() {
                public boolean checkEvent(EventObject eventObject, String methodCalled) {
                    return methodFilter == null || methodFilter.length() == 0
                    || methodCalled.startsWith(methodFilter);
                }
            };
    }


}
