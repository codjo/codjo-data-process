/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.eventsbinder;
import net.codjo.dataprocess.common.eventsbinder.dynalistener.EventChecker;
import java.lang.reflect.Method;
import java.util.EventListener;
/**
 *
 */
public abstract class AnnotationManager<T> {
    public abstract Class<? extends EventListener> getListenerClass(T currentAnnotation);


    public abstract String[] getBoundPropertiesNames(T currentAnnotation);


    public abstract EventChecker createEventChecker(T currentAnnotation);


    public void registerListener(T currentAnnotation, Object property, EventListener listenerProxy,
                                 Class<? extends EventListener> listenerClass)
          throws EventBinderException {
        try {
            Method method =
                  property.getClass().getMethod("add" + listenerClass.getSimpleName(), listenerClass);
            method.invoke(property, listenerProxy);
        }
        catch (Exception e) {
            throw new EventBinderException(e);
        }
    }


    public String getHexKey(T ann) {
        return Long.toHexString(System.currentTimeMillis());
    }
}
