package cn.vtohru.web;

import cn.vtohru.web.annotation.Controller;
import io.micronaut.context.processor.ExecutableMethodProcessor;
import io.micronaut.core.annotation.Indexed;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.ExecutableMethod;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import javax.inject.Singleton;
import javax.ws.rs.Path;
import java.lang.annotation.Annotation;
import java.util.*;

@Singleton
@Indexed(VerticleAnnotatedMethodRouteBuilder.class)
public class VerticleAnnotatedMethodRouteBuilder implements ExecutableMethodProcessor<Controller> {
    private static final Logger logger = LoggerFactory.getLogger(VerticleAnnotatedMethodRouteBuilder.class);
    private final Map<BeanDefinition<?>, List<ExecutableMethod<?, ?>>> routerMap = new HashMap<>();

    @Override
    public void process(BeanDefinition<?> beanDefinition, ExecutableMethod<?, ?> method) {
        Optional<Class<? extends Annotation>> actionAnn = method.getAnnotationTypeByStereotype(Path.class);
        actionAnn.ifPresent(annotationClass -> {
            List<ExecutableMethod<?, ?>> executableMethods = routerMap.computeIfAbsent(beanDefinition, k -> new ArrayList<>());
            if (!executableMethods.contains(method)) {
                executableMethods.add(method);
            }
        });
    }

    public Map<BeanDefinition<?>, List<ExecutableMethod<?, ?>>> getRouterMap() {
        return routerMap;
    }
}
