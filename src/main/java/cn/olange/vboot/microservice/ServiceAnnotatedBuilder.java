package cn.olange.vboot.microservice;

import cn.olange.vboot.annotation.Verticle;
import io.micronaut.context.processor.ExecutableMethodProcessor;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.ExecutableMethod;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.servicediscovery.Record;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Verticle
public class ServiceAnnotatedBuilder implements ExecutableMethodProcessor<Service> {
    private static final Logger logger = LoggerFactory.getLogger(ServiceAnnotatedBuilder.class);
    private MicroServiceRegister microServiceRegister;
    private List<BeanDefinition> records = new ArrayList<>();

    public ServiceAnnotatedBuilder(MicroServiceRegister microServiceRegister) {
        this.microServiceRegister = microServiceRegister;
    }

    @Override
    public void process(BeanDefinition<?> beanDefinition, ExecutableMethod<?, ?> method) {
        this.records.add(beanDefinition);
    }

    public List<BeanDefinition> getRecords() {
        return records;
    }

}
