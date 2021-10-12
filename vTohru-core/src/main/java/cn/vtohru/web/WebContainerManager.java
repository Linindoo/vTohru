package cn.vtohru.web;

import cn.vtohru.VerticleEvent;
import cn.vtohru.annotation.Verticle;
import cn.vtohru.context.VerticleApplicationContext;
import cn.vtohru.microservice.MicroServiceDiscovery;
import cn.vtohru.web.annotation.WebAutoConfigure;
import cn.vtohru.web.annotation.WebService;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.util.StringUtils;
import io.micronaut.inject.BeanDefinition;
import io.vertx.core.Future;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.types.HttpEndpoint;

import java.util.Optional;

@Verticle
public class WebContainerManager extends VerticleEvent {
    private static final Logger logger = LoggerFactory.getLogger(WebContainerManager.class);
    private VerticleApplicationContext applicationContext;

    public WebContainerManager(VerticleApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Future<Void> start(BeanDefinition<?> beanDefinition) {
        AnnotationValue<WebAutoConfigure> annotation = beanDefinition.getDeclaredAnnotation(WebAutoConfigure.class);
        if (annotation == null) {
            return Future.succeededFuture();
        }
        int port = annotation.intValue("port").orElse(0);
        String host = annotation.stringValue("host").orElse("0.0.0.0");
        VerticleRouterHandler routerHandler = applicationContext.getBean(VerticleRouterHandler.class);
        MicroServiceDiscovery serviceDiscovery = applicationContext.getBean(MicroServiceDiscovery.class);
        return routerHandler.registerRouter(host, port).compose(x->{
            Optional<AnnotationValue<WebService>> webServiceAnnotationValue = annotation.getAnnotation("service", WebService.class);
            if (!webServiceAnnotationValue.isPresent()) {
                return Future.succeededFuture();
            }
            String name = webServiceAnnotationValue.get().stringValue("name").orElse("");
            if (StringUtils.isEmpty(name)) {
                return Future.succeededFuture();
            }
            String root = webServiceAnnotationValue.get().stringValue("root").orElse("/");
            Record record = HttpEndpoint.createRecord(name, x.getHost(), x.getPort(), root,
                    new JsonObject().put("api.name", name));
            return serviceDiscovery.publishService(record).compose(y -> {
                logger.info("publish web service success:" + name);
                return Future.succeededFuture();
            });
        });
    }

    @Override
    public Future<Void> stop(BeanDefinition<?> beanDefinition) {
        VerticleRouterHandler routerHandler = applicationContext.getBean(VerticleRouterHandler.class);
        return routerHandler.stopServer();
    }
}
