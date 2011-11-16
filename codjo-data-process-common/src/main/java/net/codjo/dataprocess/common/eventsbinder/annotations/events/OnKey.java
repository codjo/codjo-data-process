/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.eventsbinder.annotations.events;
import net.codjo.dataprocess.common.eventsbinder.annotations.BindAnnotation;
import net.codjo.dataprocess.common.eventsbinder.annotations.managers.OnKeyAnnotationManager;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 *
 */
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
@BindAnnotation(managerClass = OnKeyAnnotationManager.class)
public @interface OnKey {
    String[] propertiesBound();
    EventType eventType() default EventType.TYPED;
    char keyChar() default 0;
    int keyCode() default -1;
    int modifiers() default -1;
    enum EventType {TYPED,
        PRESSED,
        RELEASED,
        ALL;
    }
}
