package cn.olange.vboot.router;

import cn.olange.vboot.annotation.Controller;
import cn.olange.vboot.annotation.Error;
import cn.olange.vboot.annotation.HttpMethodMapping;
import cn.olange.vboot.context.VerticleApplicationContext;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.processor.ExecutableMethodProcessor;
import io.micronaut.core.util.ArrayUtils;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.ExecutableMethod;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.web.Router;

import javax.inject.Singleton;
import javax.ws.rs.*;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Consumer;

@Singleton
public class VerticleAnnotatedMethodRouteBuilder implements ExecutableMethodProcessor<Controller> {
    private static final Logger logger = LoggerFactory.getLogger(VerticleAnnotatedMethodRouteBuilder.class);
    private static final String[] DEFAULT_MEDIA_TYPES = new String[]{"application/json"};
    private final Map<Class, Consumer<VerticleAnnotatedMethodRouteBuilder.RouteDefinition>> httpMethodsHandlers = new LinkedHashMap<>();
    private VerticleApplicationContext context;
    private Router router;

    public VerticleAnnotatedMethodRouteBuilder(ApplicationContext context) {
        this.context = (VerticleApplicationContext) context;
        router = Router.router(this.context.getVertx());
        httpMethodsHandlers.put(GET.class, (VerticleAnnotatedMethodRouteBuilder.RouteDefinition definition) -> {
            final BeanDefinition bean = definition.beanDefinition;
            final ExecutableMethod method = definition.executableMethod;
            Set<String> uris = CollectionUtils.setOf(method.stringValues(GET.class, "uris"));
            for (String uri : uris) {
                String[] produces = resolveProduces(method);
                router.route(HttpMethod.GET, uri)
                        .produces(String.join(";", produces));
                if (logger.isDebugEnabled()) {
                    logger.debug("Created Route: " + uri);
                }
            }
        });

        httpMethodsHandlers.put(POST.class, (VerticleAnnotatedMethodRouteBuilder.RouteDefinition definition) -> {
            final ExecutableMethod method = definition.executableMethod;
            final BeanDefinition bean = definition.beanDefinition;
            Set<String> uris = CollectionUtils.setOf(method.stringValues(POST.class, "uris"));
            for (String uri : uris) {
                String[] consumes = resolveConsumes(method);
                String[] produces = resolveProduces(method);
                router.route(HttpMethod.POST, uri).produces(String.join(";", produces)).consumes(String.join(";", consumes));
                if (logger.isDebugEnabled()) {
                    logger.debug("Created Route: {}" + uri);
                }
            }
        });


        httpMethodsHandlers.put(PUT.class, (VerticleAnnotatedMethodRouteBuilder.RouteDefinition definition) -> {
            final ExecutableMethod method = definition.executableMethod;
            final BeanDefinition bean = definition.beanDefinition;

            Set<String> uris = CollectionUtils.setOf(method.stringValues(PUT.class, "uris"));
            for (String uri : uris) {
                String[] consumes = resolveConsumes(method);
                String[] produces = resolveProduces(method);
                router.route(HttpMethod.PUT, uri).produces(String.join(";", produces)).consumes(String.join(";", consumes));
                if (logger.isDebugEnabled()) {
                    logger.debug("Created Route: {}" + uri);
                }
            }
        });

        httpMethodsHandlers.put(PATCH.class, (VerticleAnnotatedMethodRouteBuilder.RouteDefinition definition) -> {
            final ExecutableMethod method = definition.executableMethod;
            final BeanDefinition bean = definition.beanDefinition;

            Set<String> uris = CollectionUtils.setOf(method.stringValues(PATCH.class, "uris"));
            for (String uri : uris) {
                String[] consumes = resolveConsumes(method);
                String[] produces = resolveProduces(method);
                router.route(HttpMethod.PATCH, uri).produces(String.join(";", produces)).consumes(String.join(";", consumes));
                if (logger.isDebugEnabled()) {
                    logger.debug("Created Route: {}" + uri);
                }
            }
        });

        httpMethodsHandlers.put(DELETE.class, (VerticleAnnotatedMethodRouteBuilder.RouteDefinition definition) -> {
            final ExecutableMethod method = definition.executableMethod;
            final BeanDefinition bean = definition.beanDefinition;

            Set<String> uris = CollectionUtils.setOf(method.stringValues(DELETE.class, "uris"));
            for (String uri : uris) {
                String[] consumes = resolveConsumes(method);
                String[] produces = resolveProduces(method);
                router.route(HttpMethod.DELETE, uri).produces(String.join(";", produces)).consumes(String.join(";", consumes));
                if (logger.isDebugEnabled()) {
                    logger.debug("Created Route: {}" + uri);
                }
            }
        });


        httpMethodsHandlers.put(HEAD.class, (VerticleAnnotatedMethodRouteBuilder.RouteDefinition definition) -> {
            final ExecutableMethod method = definition.executableMethod;
            final BeanDefinition bean = definition.beanDefinition;

            Set<String> uris = CollectionUtils.setOf(method.stringValues(HEAD.class, "uris"));
            for (String uri : uris) {
                String[] consumes = resolveConsumes(method);
                String[] produces = resolveProduces(method);
                router.route(HttpMethod.DELETE, uri).produces(String.join(";", produces)).consumes(String.join(";", consumes));
                if (logger.isDebugEnabled()) {
                    logger.debug("Created Route: {}" + uri);
                }
            }
        });

        httpMethodsHandlers.put(OPTIONS.class, (VerticleAnnotatedMethodRouteBuilder.RouteDefinition definition) -> {
            final ExecutableMethod method = definition.executableMethod;
            final BeanDefinition bean = definition.beanDefinition;

            Set<String> uris = CollectionUtils.setOf(method.stringValues(OPTIONS.class, "uris"));
            for (String uri : uris) {
                String[] consumes = resolveConsumes(method);
                String[] produces = resolveProduces(method);
                router.route(HttpMethod.DELETE, uri).produces(String.join(";", produces)).consumes(String.join(";", consumes));
                if (logger.isDebugEnabled()) {
                    logger.debug("Created Route: {}" + uri);
                }
            }
        });

        httpMethodsHandlers.put(Error.class, (VerticleAnnotatedMethodRouteBuilder.RouteDefinition definition) -> {
                final ExecutableMethod method = definition.executableMethod;
                final BeanDefinition bean = definition.beanDefinition;

                boolean isGlobal = method.isTrue(Error.class, "global");
                Class declaringType = bean.getBeanType();
                OptionalInt status = method.intValue(Error.class, "status");
                Optional<Class> annotationValue = method.classValue(Error.class);
                Class exceptionType = null;
                if (annotationValue.isPresent() && Throwable.class.isAssignableFrom(annotationValue.get())) {
                    exceptionType = annotationValue.get();
                }
                if (exceptionType == null) {
                    exceptionType = Arrays.stream(method.getArgumentTypes())
                            .filter(Throwable.class::isAssignableFrom)
                            .findFirst()
                            .orElse(Throwable.class);
                }
                router.errorHandler(status.orElse(500), handler -> {
                    handler.end("error");
                });
            }
        );
    }

    private String[] resolveConsumes(ExecutableMethod method) {
        String[] consumes = method.stringValues(Consumes.class);
        if (ArrayUtils.isEmpty(consumes)) {
            consumes = DEFAULT_MEDIA_TYPES;
        }
        return consumes;
    }

    private String[] resolveProduces(ExecutableMethod method) {
        String[] produces = method.stringValues(Produces.class);
        if (ArrayUtils.isEmpty(produces)) {
            produces = DEFAULT_MEDIA_TYPES;
        }
        return produces;
    }

    @Override
    public void process(BeanDefinition<?> beanDefinition, ExecutableMethod<?, ?> method) {
        Optional<Class<? extends Annotation>> actionAnn = method.getAnnotationTypeByStereotype(HttpMethodMapping.class);
        actionAnn.ifPresent(annotationClass -> {
                    Consumer<VerticleAnnotatedMethodRouteBuilder.RouteDefinition> handler = httpMethodsHandlers.get(annotationClass);
                    if (handler != null) {
                        final int port = beanDefinition.intValue(Controller.class, "port").orElse(-1);
                        handler.accept(new VerticleAnnotatedMethodRouteBuilder.RouteDefinition(beanDefinition, method, port));
                    }
                }
        );
    }

    /**
     * state class for defining routes.
     */
    private final class RouteDefinition {
        private final BeanDefinition beanDefinition;
        private final ExecutableMethod executableMethod;
        private final int port;

        public RouteDefinition(BeanDefinition beanDefinition, ExecutableMethod executableMethod, int port) {
            this.beanDefinition = beanDefinition;
            this.executableMethod = executableMethod;
            this.port = port;
        }
    }

    public Router getRouter() {
        return router;
    }

    public void setRouter(Router router) {
        this.router = router;
    }
}
