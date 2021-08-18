package cn.vtohru.annotation;

import io.micronaut.aop.Around;
import io.micronaut.core.annotation.EntryPoint;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Around
@EntryPoint
public @interface VerticleContaner {
    String value() default "";
    String[] usePackage() default "";
}
