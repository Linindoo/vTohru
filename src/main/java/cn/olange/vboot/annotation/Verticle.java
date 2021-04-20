package cn.olange.vboot.annotation;

import io.micronaut.runtime.context.scope.ScopedProxy;

import java.lang.annotation.*;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@ScopedProxy
@Documented
@Retention(RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Verticle {

}
