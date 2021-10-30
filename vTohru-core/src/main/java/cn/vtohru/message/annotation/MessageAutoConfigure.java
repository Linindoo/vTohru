package cn.vtohru.message.annotation;

import cn.vtohru.annotation.VerticleContaner;
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
@VerticleContaner
public @interface MessageAutoConfigure {
}
