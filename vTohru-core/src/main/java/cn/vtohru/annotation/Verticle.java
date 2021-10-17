package cn.vtohru.annotation;

import io.micronaut.runtime.context.scope.ScopedProxy;
import jakarta.inject.Scope;

import java.lang.annotation.*;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@ScopedProxy
@Documented
@Retention(RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Scope
public @interface Verticle {

}
