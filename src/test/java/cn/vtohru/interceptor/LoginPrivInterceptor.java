package cn.vtohru.interceptor;

import cn.vtohru.annotation.Verticle;
import cn.vtohru.web.Interceptor;
import cn.vtohru.web.Priv;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.Order;
import io.micronaut.core.util.StringUtils;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.ExecutableMethod;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;

@Verticle
@Order(4)
public class LoginPrivInterceptor implements Interceptor {
    @Override
    public Future<Void> preHandler(BeanDefinition<?> beanDefinition, ExecutableMethod<Object, ?> method, RoutingContext routingContext) {
        System.out.println("priv");
        String userName = routingContext.get("userName");
        System.out.println("userName:" + userName);
        AnnotationValue<Priv> annotation = method.getAnnotation(Priv.class);
        if (annotation != null) {
            Boolean login = annotation.booleanValue("login").orElse(false);
            if (login && StringUtils.isEmpty(userName)) {
                return Future.failedFuture("未登录");
            }
        }
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> afterHandler(BeanDefinition<?> beanDefinition, ExecutableMethod<Object, ?> method, RoutingContext routingContext, AsyncResult<Object> asyncResult) {
        System.out.println("after loginPriv");
        return Future.succeededFuture();
    }
}
