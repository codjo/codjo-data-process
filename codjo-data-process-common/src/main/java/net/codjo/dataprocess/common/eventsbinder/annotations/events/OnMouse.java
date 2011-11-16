/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.eventsbinder.annotations.events;
import net.codjo.dataprocess.common.eventsbinder.annotations.BindAnnotation;
import net.codjo.dataprocess.common.eventsbinder.annotations.managers.OnMouseAnnotationManager;
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
@BindAnnotation(managerClass = OnMouseAnnotationManager.class)
public @interface OnMouse {
    String[] value();
    OnMouse.EventType eventType() default OnMouse.EventType.PRESSED;
    PopupType popupTriggered() default PopupType.ALL;
    int button() default -1;
    int clickCount() default -1;
    int modifiers() default -1;
    enum PopupType {TRUE,
        FALSE,
        ALL;
    }
    enum EventType {CLICKED,
        PRESSED,
        RELEASED,
        ENTERED,
        EXITED,
        ALL;
    }
}
