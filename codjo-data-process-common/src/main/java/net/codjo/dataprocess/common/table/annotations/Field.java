package net.codjo.dataprocess.common.table.annotations;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 *
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface Field {
    public String name() default "";


    public Type type();


    public int precision();


    public boolean required() default false;


    public String description() default "";


    public String defaultValue() default "";
}
