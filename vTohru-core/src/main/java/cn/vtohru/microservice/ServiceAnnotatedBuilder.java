package cn.vtohru.microservice;

import cn.vtohru.context.VerticleApplicationContext;
import cn.vtohru.microservice.annotation.Service;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.processor.ExecutableMethodProcessor;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.ExecutableMethod;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import javax.inject.Singleton;
import java.util.*;

@Singleton
public class ServiceAnnotatedBuilder implements ExecutableMethodProcessor<Service> {
    private static final Logger logger = LoggerFactory.getLogger(ServiceAnnotatedBuilder.class);
    private Map<Class<?>, List<BeanDefinition<?>>> serviceMap = new HashMap<>();
    private VerticleApplicationContext context;
    private MicroServiceRegister serviceRegister;

    public ServiceAnnotatedBuilder(ApplicationContext context, MicroServiceRegister serviceRegister) {
        this.context = (VerticleApplicationContext) context;
        this.serviceRegister = serviceRegister;
    }

    @Override
    public void process(BeanDefinition<?> beanDefinition, ExecutableMethod<?, ?> method) {
        Class<?> declaringType = method.getDeclaringType();
        if (!beanDefinition.isPrimary()) {
            return;
        }
        Collection<? extends BeanDefinition<?>> beanDefinitions = context.getBeanDefinitions(declaringType);
        List<BeanDefinition<?>> routerDefinitions = serviceMap.computeIfAbsent(declaringType, k -> new ArrayList<>());
        for (BeanDefinition<?> definition : beanDefinitions) {
            if (!definition.isPrimary() && !routerDefinitions.contains(definition)) {
                routerDefinitions.add(definition);
            }
        }
    }

    public void registerService() {
        if (serviceMap != null) {
            for (Map.Entry<Class<?>, List<BeanDefinition<?>>> entry : serviceMap.entrySet()) {
                for (BeanDefinition<?> definition : entry.getValue()) {
                    this.serviceRegister.registerService(entry.getKey(), definition);
                }
            }
        }
    }
}
