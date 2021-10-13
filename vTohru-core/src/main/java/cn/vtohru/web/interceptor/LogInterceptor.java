package cn.vtohru.web.interceptor;

import cn.vtohru.annotation.GlobalScope;
import cn.vtohru.annotation.Verticle;
import cn.vtohru.web.Interceptor;
import cn.vtohru.web.WebContainerManager;
import io.micronaut.core.annotation.Order;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.ExecutableMethod;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

@Verticle
@Order(0)
@GlobalScope
public class LogInterceptor implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(WebContainerManager.class);
    @Override
    public Future<Void> preHandler(BeanDefinition<?> beanDefinition, ExecutableMethod<Object, ?> method, RoutingContext routingContext) {
        logger.info("url:" + routingContext.request().uri());
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> afterHandler(BeanDefinition<?> beanDefinition, ExecutableMethod<Object, ?> method, RoutingContext routingContext, AsyncResult<Object> asyncResult) {
        if (asyncResult.succeeded()) {
            logger.info("success:" + asyncResult.result());
        } else {
            logger.info("error:" + asyncResult.cause().getMessage());
        }
        return Future.succeededFuture();
    }

}
