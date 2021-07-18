package cn.olange.vboot.microservice;

import cn.olange.vboot.annotation.Verticle;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.processor.ExecutableMethodProcessor;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.ExecutableMethod;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.util.*;

@Verticle
public class ServiceAnnotatedBuilder implements ExecutableMethodProcessor<Service> {
    private static final Logger logger = LoggerFactory.getLogger(ServiceAnnotatedBuilder.class);
    private Map<Class<?>, List<BeanDefinition<?>>> routerMap = new HashMap<>();
    private ApplicationContext context;
    private MicroServiceRegister serviceRegister;

    public ServiceAnnotatedBuilder(ApplicationContext context, MicroServiceRegister serviceRegister) {
        this.context = context;
        this.serviceRegister = serviceRegister;
    }

    @Override
    public void process(BeanDefinition<?> beanDefinition, ExecutableMethod<?, ?> method) {
        Class<?> declaringType = method.getDeclaringType();
        Collection<? extends BeanDefinition<?>> beanDefinitions = context.getBeanDefinitions(declaringType);
        List<BeanDefinition<?>> routerDefinitions = routerMap.get(declaringType);
        if (routerDefinitions == null) {
            routerDefinitions = new ArrayList<>();
            routerMap.put(declaringType, routerDefinitions);
        }
        for (BeanDefinition<?> definition : beanDefinitions) {
            if (!definition.isPrimary()) {
                routerDefinitions.add(definition);
            }
        }
    }

    public void registerService() {
        if (routerMap != null) {
            for (Map.Entry<Class<?>, List<BeanDefinition<?>>> entry : routerMap.entrySet()) {
                for (BeanDefinition<?> definition : entry.getValue()) {
                    this.serviceRegister.registerService(entry.getKey(), definition);
                }
            }
        }
    }
}
