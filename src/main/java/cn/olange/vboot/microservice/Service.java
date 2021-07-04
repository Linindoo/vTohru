package cn.olange.vboot.microservice;

import io.micronaut.aop.Introduction;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Primary;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Introduction
@Bean
@Primary
@Documented
@Retention(RUNTIME)
@Target({TYPE, ANNOTATION_TYPE, METHOD, FIELD})
public @interface Service {
    String value() default "";
}
