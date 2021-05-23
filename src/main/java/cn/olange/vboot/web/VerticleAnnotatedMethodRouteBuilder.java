package cn.olange.vboot.web;

import cn.olange.vboot.annotation.Controller;
import cn.olange.vboot.context.VerticleApplicationContext;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.processor.ExecutableMethodProcessor;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.type.Argument;
import io.micronaut.core.util.ArrayUtils;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.core.util.StringUtils;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.ExecutableMethod;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Consumer;

@Singleton
public class VerticleAnnotatedMethodRouteBuilder implements ExecutableMethodProcessor<Controller> {
    private static final Logger logger = LoggerFactory.getLogger(VerticleAnnotatedMethodRouteBuilder.class);
    private static final String[] DEFAULT_MEDIA_TYPES = new String[]{"application/json"};
    private final Map<Class, Consumer<VerticleAnnotatedMethodRouteBuilder.RouteDefinition>> httpMethodsHandlers = new LinkedHashMap<>();
    private VerticleApplicationContext context;
    private ResponseHandlerRegister responseHandlerRegister;
    private ErrorHandlerRegister errorHandlerRegister;
    private Router router;

    public VerticleAnnotatedMethodRouteBuilder(ApplicationContext context, @NonNull ResponseHandlerRegister responseHandlerRegister,
                                               ErrorHandlerRegister errorHandlerRegister) {
        this.context = (VerticleApplicationContext) context;
        this.responseHandlerRegister = responseHandlerRegister;
        this.errorHandlerRegister = errorHandlerRegister;
        router = Router.router(this.context.getVertx());
        httpMethodsHandlers.put(GET.class, (VerticleAnnotatedMethodRouteBuilder.RouteDefinition definition) -> {
            final BeanDefinition bean = definition.beanDefinition;
            final ExecutableMethod method = definition.executableMethod;
            Set<String> uris = CollectionUtils.setOf(method.stringValues(Path.class));
            String[] produces = resolveProduces(method);
            MediaType mediaType = Arrays.stream(produces).findFirst().map(MediaType::valueOf).orElse(MediaType.APPLICATION_JSON_TYPE);
            for (String uri : uris) {
                Route route = router.route(HttpMethod.GET, uri);
                route.produces(String.join(";", produces));
                route.handler(invokeHandler(bean, method, mediaType));
                if (logger.isDebugEnabled()) {
                    logger.debug("Created Route: " + uri);
                }
            }
        });

        httpMethodsHandlers.put(POST.class, (VerticleAnnotatedMethodRouteBuilder.RouteDefinition definition) -> {
            final ExecutableMethod method = definition.executableMethod;
            final BeanDefinition bean = definition.beanDefinition;
            Set<String> uris = CollectionUtils.setOf(method.stringValues(Path.class));
            String[] consumes = resolveConsumes(method);
            String[] produces = resolveProduces(method);
            MediaType mediaType = Arrays.stream(produces).findFirst().map(MediaType::valueOf).orElse(MediaType.APPLICATION_JSON_TYPE);
            for (String uri : uris) {
                router.route(HttpMethod.POST, uri).produces(String.join(";", produces)).consumes(String.join(";", consumes))
                        .handler(invokeHandler(bean, method, mediaType));
                if (logger.isDebugEnabled()) {
                    logger.debug("Created Route: {}" + uri);
                }
            }
        });


        httpMethodsHandlers.put(PUT.class, (VerticleAnnotatedMethodRouteBuilder.RouteDefinition definition) -> {
            final ExecutableMethod method = definition.executableMethod;
            final BeanDefinition bean = definition.beanDefinition;

            Set<String> uris = CollectionUtils.setOf(method.stringValues(PUT.class, "uris"));
            String[] consumes = resolveConsumes(method);
            String[] produces = resolveProduces(method);
            MediaType mediaType = Arrays.stream(produces).findFirst().map(MediaType::valueOf).orElse(MediaType.APPLICATION_JSON_TYPE);
            for (String uri : uris) {
                router.route(HttpMethod.PUT, uri).produces(String.join(";", produces)).consumes(String.join(";", consumes))
                        .handler(invokeHandler(bean, method, mediaType));
                if (logger.isDebugEnabled()) {
                    logger.debug("Created Route: {}" + uri);
                }
            }
        });

        httpMethodsHandlers.put(PATCH.class, (VerticleAnnotatedMethodRouteBuilder.RouteDefinition definition) -> {
            ExecutableMethod method = definition.executableMethod;
            BeanDefinition bean = definition.beanDefinition;
            String[] consumes = resolveConsumes(method);
            String[] produces = resolveProduces(method);
            MediaType mediaType = Arrays.stream(produces).findFirst().map(MediaType::valueOf).orElse(MediaType.APPLICATION_JSON_TYPE);
            Set<String> uris = CollectionUtils.setOf(method.stringValues(PATCH.class, "uris"));
            for (String uri : uris) {

                router.route(HttpMethod.PATCH, uri).produces(String.join(";", produces)).consumes(String.join(";", consumes))
                        .handler(invokeHandler(bean, method, mediaType));
                if (logger.isDebugEnabled()) {
                    logger.debug("Created Route: {}" + uri);
                }
            }
        });

        httpMethodsHandlers.put(DELETE.class, (VerticleAnnotatedMethodRouteBuilder.RouteDefinition definition) -> {
            ExecutableMethod method = definition.executableMethod;
            BeanDefinition bean = definition.beanDefinition;
            String[] consumes = resolveConsumes(method);
            String[] produces = resolveProduces(method);
            MediaType mediaType = Arrays.stream(produces).findFirst().map(MediaType::valueOf).orElse(MediaType.APPLICATION_JSON_TYPE);
            Set<String> uris = CollectionUtils.setOf(method.stringValues(DELETE.class, "uris"));
            for (String uri : uris) {

                router.route(HttpMethod.DELETE, uri).produces(String.join(";", produces)).consumes(String.join(";", consumes))
                        .handler(invokeHandler(bean, method, mediaType));
                if (logger.isDebugEnabled()) {
                    logger.debug("Created Route: {}" + uri);
                }
            }
        });

        httpMethodsHandlers.put(HEAD.class, (VerticleAnnotatedMethodRouteBuilder.RouteDefinition definition) -> {
            final ExecutableMethod method = definition.executableMethod;
            final BeanDefinition bean = definition.beanDefinition;
            String[] consumes = resolveConsumes(method);
            String[] produces = resolveProduces(method);
            MediaType mediaType = Arrays.stream(produces).findFirst().map(MediaType::valueOf).orElse(MediaType.APPLICATION_JSON_TYPE);
            Set<String> uris = CollectionUtils.setOf(method.stringValues(HEAD.class, "uris"));
            for (String uri : uris) {
                router.route(HttpMethod.HEAD, uri).produces(String.join(";", produces)).consumes(String.join(";", consumes))
                        .handler(invokeHandler(bean, method, mediaType));
                if (logger.isDebugEnabled()) {
                    logger.debug("Created Route: {}" + uri);
                }
            }
        });

        httpMethodsHandlers.put(OPTIONS.class, (VerticleAnnotatedMethodRouteBuilder.RouteDefinition definition) -> {
            final ExecutableMethod method = definition.executableMethod;
            final BeanDefinition bean = definition.beanDefinition;
            String[] consumes = resolveConsumes(method);
            String[] produces = resolveProduces(method);
            MediaType mediaType = Arrays.stream(produces).findFirst().map(MediaType::valueOf).orElse(MediaType.APPLICATION_JSON_TYPE);
            Set<String> uris = CollectionUtils.setOf(method.stringValues(OPTIONS.class, "uris"));
            for (String uri : uris) {
                router.route(HttpMethod.OPTIONS, uri).produces(String.join(";", produces)).consumes(String.join(";", consumes))
                        .handler(invokeHandler(bean, method, mediaType));
                if (logger.isDebugEnabled()) {
                    logger.debug("Created Route: {}" + uri);
                }
            }
        });
        for (Map.Entry<Integer, ErrorHandler> errorHandlerEntry : this.errorHandlerRegister.getErrorHanderMap().entrySet()) {
            router.errorHandler(errorHandlerEntry.getKey(), errorHandlerEntry.getValue());
        }
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
        Optional<Class<? extends Annotation>> actionAnn = method.getAnnotationTypeByStereotype(Controller.class);
        actionAnn.ifPresent(annotationClass -> {
            Consumer<VerticleAnnotatedMethodRouteBuilder.RouteDefinition> handler = httpMethodsHandlers.get(getMethdType(method));
            if (handler != null) {
                final int port = beanDefinition.intValue(Controller.class, "port").orElse(-1);
                handler.accept(new VerticleAnnotatedMethodRouteBuilder.RouteDefinition(beanDefinition, method, port));
            }
        });
    }

    private Class getMethdType(ExecutableMethod<?, ?> method) {
        if (method.hasAnnotation(GET.class)) {
            return GET.class;
        } else if (method.hasAnnotation(POST.class)) {
            return POST.class;
        } else if (method.hasAnnotation(DELETE.class)) {
            return DELETE.class;
        } else if (method.hasAnnotation(OPTIONS.class)) {
            return OPTIONS.class;
        } else if (method.hasAnnotation(HEAD.class)) {
            return HEAD.class;
        } else if (method.hasAnnotation(PUT.class)) {
            return PUT.class;
        } else if (method.hasAnnotation(PATCH.class)) {
            return PATCH.class;
        }
        return null;
    }

    private Handler<RoutingContext> invokeHandler(BeanDefinition<?> beanDefinition, ExecutableMethod<Object, ?> method, MediaType mediaType) {
        return routingContext -> {
            Object[] args = getArgs(routingContext, method);
            Object bean = this.context.getBean(beanDefinition);
            Object result = method.invoke(bean, args);
            ResponseHandler responseHandler = responseHandlerRegister.findResponseHandler(mediaType).orElse(this.context.getBean(JsonResponseHandler.class));
            responseHandler.handler(routingContext, result);
        };
    }

    private Object[] getArgs(RoutingContext routingContext,ExecutableMethod<?, ?> method){
        Argument[] arguments = method.getArguments();
        Object[] objects = new Object[arguments.length];
        int i =0;
        for (Argument argInfo : arguments) {
            if (argInfo.isAnnotationPresent(Context.class)) {
                objects[i] = getContext(routingContext, argInfo);
            } else if (argInfo.isAnnotationPresent(QueryParam.class)) {
                objects[i] = getQueryParamArg(routingContext, argInfo);
            } else if (argInfo.isAnnotationPresent(FormParam.class)) {
                objects[i] = getFromParamArg(routingContext, argInfo);
            } else if (argInfo.isAnnotationPresent(PathParam.class)) {
                objects[i] = getPathParamArg(routingContext, argInfo);
            } else if (argInfo.isAnnotationPresent(BeanParam.class)) {
                objects[i] = getBeanParamArg(routingContext, argInfo);
            } else {
                objects[i] = null;
            }
            i++;
        }
        return objects;
    }
    private Object getPathParamArg(RoutingContext routingContext, Argument argInfo){
        AnnotationValue<PathParam> annotation = argInfo.getAnnotation(PathParam.class);
        if (annotation != null) {
            String paramName = annotation.stringValue().orElse("");
            String q = routingContext.request().getParam(paramName);
            return covertType(argInfo.getType(), q);
        }
        return null;

    }

    private Object getBeanParamArg(RoutingContext routingContext,Argument argInfo){
        try {
            String q = routingContext.getBodyAsString();
            if (!StringUtils.isEmpty(q)){
                return covertType(argInfo.getType(), q);
            }
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        return null;
    }

    private Object getFromParamArg(RoutingContext routingContext, Argument argInfo) {
        AnnotationValue<FormParam> annotation = argInfo.getAnnotation(FormParam.class);
        if (annotation != null) {
            String paramName = annotation.stringValue().orElse("");
            String q = routingContext.request().getParam(paramName);
            return covertType(argInfo.getType(), q);
        }
        return null;
    }

    private Object getQueryParamArg(RoutingContext routingContext, Argument argInfo) {
        try {
            AnnotationValue<QueryParam> annotation = argInfo.getAnnotation(QueryParam.class);
            if (annotation != null && annotation.isPresent("value")) {
                String paramName = annotation.stringValue().orElse("");
                String q = routingContext.request().getParam(paramName);
                return covertType(argInfo.getType(), q);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    private Object covertType(Class type,String v) {
        if (v == null) {
            return null;
        }
        String typeName = type.getTypeName();
        if (type == String.class){
            return v;
        }
        if (type == Integer.class||typeName.equals("int")){
            return Integer.parseInt(v);
        }
        if (type == Long.class||typeName.equals("long")){
            return Long.parseLong(v);
        }
        if (type == Float.class||typeName.equals("float")){
            return Float.parseFloat(v);
        }
        if (type == Double.class||typeName.equals("double")){
            return Double.parseDouble(v);
        }
        if (type == JsonArray.class) {
            return new JsonArray(v);
        }
        if (type == JsonObject.class) {
            return new JsonObject(v);
        }
        return null;
    }

    private Object getContext(RoutingContext routingContext,Argument argInfo){
        Class clz = argInfo.getType();
        if (clz ==RoutingContext.class){
            return routingContext;
        }else if (clz == HttpServerRequest.class){
            return routingContext.request();
        }else if (clz == HttpServerResponse.class){
            return routingContext.response();
        }else if (clz == Session.class){
            return routingContext.session();
        }else if (clz == Vertx.class){
            return routingContext.vertx();
        }
        return null;
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
