package cn.vtohru;

import io.micronaut.inject.BeanDefinition;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;


public abstract class VerticleEvent {
    public abstract Future<Void> start(BeanDefinition<?> beanDefinition);
    public abstract Future<Void> stop(BeanDefinition<?> beanDefinition);
}
