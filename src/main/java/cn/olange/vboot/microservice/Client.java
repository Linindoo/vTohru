package cn.olange.vboot.microservice;

import cn.olange.vboot.annotation.Verticle;
import io.micronaut.aop.Introduction;
import io.micronaut.context.annotation.Type;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Introduction
@Type(MicroServiceClientIntroductionAdvice.class)
@Verticle
public @interface Client {
    String value() default "";
    String type();
}
