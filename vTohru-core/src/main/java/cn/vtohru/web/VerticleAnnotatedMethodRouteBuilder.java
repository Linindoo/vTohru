package cn.vtohru.web;

import cn.vtohru.web.annotation.Controller;
import io.micronaut.context.processor.ExecutableMethodProcessor;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.ExecutableMethod;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import javax.inject.Singleton;
import javax.ws.rs.Path;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Singleton
public class VerticleAnnotatedMethodRouteBuilder implements ExecutableMethodProcessor<Controller> {
    private static final Logger logger = LoggerFactory.getLogger(VerticleAnnotatedMethodRouteBuilder.class);
    private List<VerticleAnnotatedMethodRouteBuilder.RouteDefinition> routeDefinitions = new ArrayList<>();


    @Override
    public void process(BeanDefinition<?> beanDefinition, ExecutableMethod<?, ?> method) {
        Optional<Class<? extends Annotation>> actionAnn = method.getAnnotationTypeByStereotype(Path.class);
        actionAnn.ifPresent(annotationClass -> {
            routeDefinitions.add(new VerticleAnnotatedMethodRouteBuilder.RouteDefinition(beanDefinition, method, -1));
        });
    }

    public List<RouteDefinition> getRouteDefinitions() {
        return routeDefinitions;
    }

    /**
     * state class for defining routes.
     */
    public final class RouteDefinition {
        private final BeanDefinition beanDefinition;
        private final ExecutableMethod executableMethod;
        private final int port;

        public RouteDefinition(BeanDefinition beanDefinition, ExecutableMethod executableMethod, int port) {
            this.beanDefinition = beanDefinition;
            this.executableMethod = executableMethod;
            this.port = port;
        }

        public BeanDefinition getBeanDefinition() {
            return beanDefinition;
        }

        public ExecutableMethod getExecutableMethod() {
            return executableMethod;
        }

        public int getPort() {
            return port;
        }
    }
}
