package cn.olange.vboot.annotation;

import io.micronaut.core.annotation.EntryPoint;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
@EntryPoint
public @interface WebAutoConfigure {
    int port() default -1;
    String host() default "0.0.0.0";
}
