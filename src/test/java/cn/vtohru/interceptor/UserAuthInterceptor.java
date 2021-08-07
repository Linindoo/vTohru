package cn.vtohru.interceptor;

import cn.vtohru.annotation.Verticle;
import cn.vtohru.web.Interceptor;
import io.micronaut.core.annotation.Order;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.ExecutableMethod;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;

@Verticle
@Order(1)
public class UserAuthInterceptor implements Interceptor {
    @Override
    public Future<Void> preHandler(BeanDefinition<?> beanDefinition, ExecutableMethod<Object, ?> method, RoutingContext routingContext) {
        System.out.println("userAuth");
        routingContext.put("userName", "sb");
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> afterHandler(BeanDefinition<?> beanDefinition, ExecutableMethod<Object, ?> method, RoutingContext routingContext, AsyncResult<Object> asyncResult) {
        routingContext.remove("userName");
        System.out.println("after userAuth");
        return Future.succeededFuture();
    }
}
