package cn.vtohru.web;

import cn.vtohru.VerticleEvent;
import cn.vtohru.context.VerticleApplicationContext;
import cn.vtohru.microservice.MicroServiceDiscovery;
import cn.vtohru.web.annotation.WebAutoConfigure;
import cn.vtohru.web.annotation.WebService;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.Indexed;
import io.micronaut.core.util.StringUtils;
import io.micronaut.inject.BeanDefinition;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.types.HttpEndpoint;

import javax.inject.Singleton;
import java.util.Optional;

@Singleton
@Indexed(VerticleEvent.class)
public class WebContainerManager extends VerticleEvent {
    private static final String WEB_CONFIG_PREFIX = "vtohru.web";
    private static final Logger logger = LoggerFactory.getLogger(WebContainerManager.class);
    private VerticleApplicationContext applicationContext;
    private HttpServer httpServer;

    public WebContainerManager(ApplicationContext applicationContext) {
        this.applicationContext = (VerticleApplicationContext) applicationContext;
    }

    @Override
    public Future<Void> start(BeanDefinition<?> beanDefinition) {
        AnnotationValue<WebAutoConfigure> annotation = beanDefinition.getDeclaredAnnotation(WebAutoConfigure.class);
        if (annotation == null) {
            return Future.succeededFuture();
        }
        JsonObject httpConfig = applicationContext.getProperty(WEB_CONFIG_PREFIX, JsonObject.class).orElse(new JsonObject());
        int port = annotation.intValue("port").orElse(httpConfig.getInteger("port", 0));
        String host = annotation.stringValue("host").orElse(httpConfig.getString("host", "0.0.0.0"));
        MicroServiceDiscovery serviceDiscovery = applicationContext.getBean(MicroServiceDiscovery.class);
        VerticleRouterHandler verticleRouterHandler = applicationContext.getBean(VerticleRouterHandler.class);
        HttpServerOptions httpServerOptions = new HttpServerOptions(httpConfig);
        httpServerOptions.setPort(port);
        httpServerOptions.setHost(host);
        this.httpServer = applicationContext.getVertx().createHttpServer(httpServerOptions);
        Router router = verticleRouterHandler.buildRouter();
        return this.httpServer.requestHandler(router).listen().compose(x->{
            logger.info(applicationContext.getScopeName() + "-start http server success at port:" + x.actualPort());
            Optional<AnnotationValue<WebService>> webServiceAnnotationValue = annotation.getAnnotation("service", WebService.class);
            if (!webServiceAnnotationValue.isPresent()) {
                return Future.succeededFuture();
            }
            String name = webServiceAnnotationValue.get().stringValue("name").orElse("");
            if (StringUtils.isEmpty(name)) {
                return Future.succeededFuture();
            }
            String root = webServiceAnnotationValue.get().stringValue("root").orElse("/");
            Record record = HttpEndpoint.createRecord(name, host, x.actualPort(), root,
                    new JsonObject().put("api.name", name));
            return serviceDiscovery.publishService(record).compose(y -> {
                logger.info("publish web service success:" + name);
                return Future.succeededFuture();
            });
        });
    }

    @Override
    public Future<Void> stop(BeanDefinition<?> beanDefinition) {
        if (httpServer != null) {
            return httpServer.close();
        }
        return Future.succeededFuture();
    }

}
