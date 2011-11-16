/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.eventsbinder.annotations.events;
import net.codjo.dataprocess.common.eventsbinder.annotations.BindAnnotation;
import net.codjo.dataprocess.common.eventsbinder.annotations.managers.OnGenericEventAnnotationManager;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.EventListener;
/**
 *
 */
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
@BindAnnotation(managerClass = OnGenericEventAnnotationManager.class)
public @interface OnGenericEvent {
    String[] propertiesBound();
    Class<?extends EventListener> listenerClass();
    String methodFilter() default "";
}
