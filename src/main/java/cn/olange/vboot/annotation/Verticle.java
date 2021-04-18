package cn.olange.vboot.annotation;

import io.micronaut.runtime.context.scope.ScopedProxy;
import io.vertx.core.AbstractVerticle;

import java.lang.annotation.*;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@ScopedProxy
@Documented
@Retention(RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Verticle {
    Class<? extends AbstractVerticle> bind();
}
