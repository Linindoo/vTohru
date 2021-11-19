package cn.vtohru.annotation;

import io.micronaut.context.annotation.Bean;
import jakarta.inject.Scope;

import java.lang.annotation.*;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target({ElementType.TYPE})
@Scope
@Bean
public @interface Verticle {

}
