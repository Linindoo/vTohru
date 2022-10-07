package cn.vtohru.web;

import cn.vtohru.context.VerticleApplicationContext;
import cn.vtohru.plugin.PluginApplicationContext;
import cn.vtohru.plugin.VTohruPluginManager;
import cn.vtohru.web.annotation.Controller;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.processor.ExecutableMethodProcessor;
import io.micronaut.core.annotation.Indexed;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.ExecutableMethod;
import io.micronaut.inject.qualifiers.Qualifiers;
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
    private VerticleApplicationContext verticleApplicationContext;

    public VerticleAnnotatedMethodRouteBuilder(ApplicationContext applicationContext) {
        this.verticleApplicationContext = (VerticleApplicationContext) applicationContext;
        checkPluginRoute();
    }

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

    private void checkPluginRoute() {
        PluginApplicationContext context = this.verticleApplicationContext.getCurentpluginContext();
        if (context == null) {
            return;
        }
        Collection<BeanDefinition<?>> beanDefinitions = context.getBeanDefinitions(Qualifiers.byStereotype(Controller.class));
        for (BeanDefinition<?> beanDefinition : beanDefinitions) {
            Collection<? extends ExecutableMethod<?, ?>> methods = beanDefinition.getExecutableMethods();
            for (ExecutableMethod<?, ?> method : methods) {
                try {
                    Optional<Class<? extends Annotation>> actionAnn = method.getAnnotationTypeByStereotype(Path.class);
                    actionAnn.ifPresent(annotationClass -> {
                        List<ExecutableMethod<?, ?>> executableMethods = routerMap.computeIfAbsent(beanDefinition, k -> new ArrayList<>());
                        if (!executableMethods.contains(method)) {
                            executableMethods.add(method);
                        }
                    });
                } catch (Exception e) {
                    logger.error("Error processing bean [" + beanDefinition + "] method definition [" + method + "]: " + e.getMessage());
                }
            }
        }
    }
}
