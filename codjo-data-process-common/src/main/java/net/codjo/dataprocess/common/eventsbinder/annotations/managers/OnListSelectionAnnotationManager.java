/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.eventsbinder.annotations.managers;
import net.codjo.dataprocess.common.eventsbinder.AnnotationManager;
import net.codjo.dataprocess.common.eventsbinder.annotations.events.OnListSelection;
import net.codjo.dataprocess.common.eventsbinder.dynalistener.EventChecker;
import java.util.EventListener;
import java.util.EventObject;
import javax.swing.event.ListSelectionListener;
/**
 * 
 */
public class OnListSelectionAnnotationManager extends AnnotationManager<OnListSelection> {
    @Override
    public Class<?extends EventListener> getListenerClass(OnListSelection currentAnnotation) {
        return ListSelectionListener.class;
    }


    @Override
    public String[] getBoundPropertiesNames(OnListSelection currentAnnotation) {
        return currentAnnotation.propertiesBound();
    }


    @Override
    public EventChecker createEventChecker(OnListSelection currentAnnotation) {
        return new EventChecker() {
                public boolean checkEvent(EventObject eventObject, String methodCalled) {
                    return true;
                }
            };
    }
}
