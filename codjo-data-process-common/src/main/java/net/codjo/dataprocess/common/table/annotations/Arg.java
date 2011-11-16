package net.codjo.dataprocess.common.table.annotations;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 *
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.PARAMETER, ElementType.METHOD})
public @interface Arg {
    public String value();


    public Type type() default Type.UNDEFINED;
}
