package cn.vtohru.web;

import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.ExecutableMethod;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;

public interface Interceptor {
    Future<Object> preHandler(BeanDefinition<?> beanDefinition, ExecutableMethod<Object, ?> method, RoutingContext routingContext);

    Future<Object> afterHandler(BeanDefinition<?> beanDefinition, ExecutableMethod<Object, ?> method, RoutingContext routingContext, Object result);

}