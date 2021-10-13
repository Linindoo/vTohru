package cn.vtohru.microservice;

import cn.vtohru.annotation.GlobalScope;
import cn.vtohru.annotation.Verticle;
import cn.vtohru.context.VerticleApplicationContext;
import io.micronaut.context.ApplicationContext;
import io.micronaut.inject.BeanDefinition;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.types.EventBusService;
import io.vertx.serviceproxy.ServiceException;
import io.vertx.serviceproxy.ServiceExceptionMessageCodec;

import java.util.List;
import java.util.function.Function;

@Verticle
@GlobalScope
public class MicroServiceRegister {
    private static final Logger logger = LoggerFactory.getLogger(MicroServiceRegister.class);
    private boolean topLevel = true;
    private long timeoutSeconds = 300L;
    private List<Function<Message<JsonObject>, Future<Message<JsonObject>>>> interceptors;
    private boolean includeDebugInfo = false;
    private VerticleApplicationContext verticleApplicationContext;
    private MicroServiceDiscovery serviceDiscovery;

    public MicroServiceRegister(ApplicationContext applicationContext, MicroServiceDiscovery serviceDiscovery) {
        this.verticleApplicationContext = (VerticleApplicationContext) applicationContext;
        try {
            this.verticleApplicationContext.getVertx().eventBus().registerDefaultCodec(ServiceException.class, new ServiceExceptionMessageCodec());
        } catch (IllegalStateException ex) {}
        this.serviceDiscovery = serviceDiscovery;
    }

    public <T> void registerService(Class<?> serviceClass, BeanDefinition<?> definition) {
        ServiceProxyHandler<T> serviceProxyHandler = new ServiceProxyHandler(verticleApplicationContext, definition, topLevel, timeoutSeconds, includeDebugInfo);
        serviceProxyHandler.register(verticleApplicationContext.getVertx().eventBus(), serviceClass.getName());
        Record record = EventBusService.createRecord(serviceClass.getName(), serviceClass.getName(), serviceClass);
        this.serviceDiscovery.publishService(record).onSuccess(x->{
            logger.info("Service <" + x.getName() + "> published");
        }).onFailure(logger::error);
    }
}
