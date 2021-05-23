package cn.olange.vboot.microservice;

import cn.olange.vboot.annotation.Verticle;
import cn.olange.vboot.context.VerticleApplicationContext;
import io.micronaut.context.ApplicationContext;
import io.micronaut.inject.BeanDefinition;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.types.EventBusService;
import io.vertx.serviceproxy.ServiceBinder;

@Verticle
public class MicroServiceRegister {
    private static final Logger logger = LoggerFactory.getLogger(MicroServiceRegister.class);

    private VerticleApplicationContext verticleApplicationContext;
    private MicroServiceDiscovery serviceDiscovery;
    private ServiceBinder serviceBinder;

    public MicroServiceRegister(ApplicationContext applicationContext, MicroServiceDiscovery serviceDiscovery) {
        this.verticleApplicationContext = (VerticleApplicationContext) applicationContext;
        this.serviceDiscovery = serviceDiscovery;
        this.serviceBinder = new ServiceBinder(this.verticleApplicationContext.getVertx());
    }

    public <T> void registerService(BeanDefinition<T> beanDefinition) {
        this.serviceBinder.setAddress(beanDefinition.getName())
                .register(beanDefinition.getBeanType(), this.verticleApplicationContext.getBean(beanDefinition));
        Record record = EventBusService.createRecord(beanDefinition.getName(), beanDefinition.getName(), beanDefinition.getBeanType());
        this.serviceDiscovery.publish(record).onSuccess(x->{
            logger.info("Service <" + x.getName() + "> published");
        }).onFailure(logger::error);
    }
}
