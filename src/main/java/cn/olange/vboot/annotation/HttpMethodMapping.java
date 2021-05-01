package cn.olange.vboot.annotation;

import io.micronaut.context.annotation.Executable;
import io.micronaut.core.annotation.EntryPoint;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
@Executable
@EntryPoint
public @interface HttpMethodMapping {
    String value() default "/";

    String[] uris() default {"/"};
}
