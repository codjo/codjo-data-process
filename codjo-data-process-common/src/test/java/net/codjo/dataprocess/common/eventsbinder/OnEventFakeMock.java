package net.codjo.dataprocess.common.eventsbinder;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import net.codjo.dataprocess.common.eventsbinder.annotations.BindAnnotation;
/**
 *
 */
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface OnEventFakeMock {

    String [] properties();


}
