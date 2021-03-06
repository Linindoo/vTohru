package cn.vtohru.web;

import cn.vtohru.annotation.GlobalScope;
import cn.vtohru.annotation.Verticle;
import cn.vtohru.context.VerticleApplicationContext;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.Indexed;
import io.micronaut.core.type.Argument;
import io.micronaut.core.util.ArrayUtils;
import io.micronaut.core.util.StringUtils;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.ExecutableMethod;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
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

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Verticle
@GlobalScope
@Indexed(VerticleRouterHandler.class)
public class VerticleRouterHandler {
    private static Pattern pathPattern = Pattern.compile("\\{(.*?)\\}");
    private static final Logger logger = LoggerFactory.getLogger(VerticleRouterHandler.class);
    private static final String[] DEFAULT_MEDIA_TYPES = new String[]{"application/json"};
    private VerticleApplicationContext context;
    private VerticleAnnotatedMethodRouteBuilder routeBuilder;
    private ErrorHandlerRegister errorHandlerRegister;
    private ResponseHandlerRegister responseHandlerRegister;
    private List<Interceptor> interceptorList;
    private List<ResourceHandler> resourceHandlers;

    public VerticleRouterHandler(ApplicationContext context, VerticleAnnotatedMethodRouteBuilder routeBuilder, ErrorHandlerRegister errorHandlerRegister, ResponseHandlerRegister responseHandlerRegister, List<Interceptor> interceptorList, List<ResourceHandler> resourceHandlers) {
        this.context = (VerticleApplicationContext) context;
        this.routeBuilder = routeBuilder;
        this.errorHandlerRegister = errorHandlerRegister;
        this.responseHandlerRegister = responseHandlerRegister;
        this.interceptorList = interceptorList;
        this.resourceHandlers = resourceHandlers;
    }

    public Router buildRouter() {
        Router router = Router.router(this.context.getVertx());
        for (ResourceHandler resourceHandler : this.resourceHandlers) {
            if (!context.isNull(resourceHandler)) {
                Route route = StringUtils.isEmpty(resourceHandler.path()) ? router.route() : router.route(converter(resourceHandler.path()));
                if (resourceHandler.consumes() != null && resourceHandler.consumes().length > 0) {
                    route.consumes(String.join(";", resourceHandler.consumes()));
                }
                if (resourceHandler.produces() != null && resourceHandler.produces().length > 0) {
                    route.produces(String.join(";", resourceHandler.produces()));
                }
                route.handler(resourceHandler);
                logger.info(context.getScopeName() + ":register resourceHandler-" + resourceHandler.getClass().getName());
            }
        }

        for (Map.Entry<BeanDefinition<?>, List<ExecutableMethod<?, ?>>> beanDefinitionListEntry : routeBuilder.getRouterMap().entrySet()) {
            BeanDefinition<?> beanDefinition = beanDefinitionListEntry.getKey();
            if (context.isScoped(beanDefinition)) {
                Object bean= context.getBean(beanDefinition);
                for (ExecutableMethod executableMethod : beanDefinitionListEntry.getValue()) {
                    HttpMethod methodType = getMethodType(executableMethod);
                    if (methodType == null) {
                        continue;
                    }
                    String uri = executableMethod.stringValue(Path.class).orElse("");
                    String[] produces = resolveProduces(executableMethod);
                    String[] consumes = resolveConsumes(executableMethod);
                    MediaType mediaType = Arrays.stream(produces).findFirst().map(MediaType::valueOf).orElse(MediaType.APPLICATION_JSON_TYPE);
                    String beanPath = getBeanPath(beanDefinition);
                    String path = converter(beanPath + uri);
                    Route route = router.route(methodType, path);
                    if (produces.length > 0) {
                        route.produces(String.join(";", produces));
                    }
                    if (consumes.length > 0) {
                        route.consumes(String.join(";", consumes));
                    }
                    route.handler(invokeInterceptor(bean, beanDefinition, executableMethod, mediaType));
                    if (logger.isDebugEnabled()) {
                        logger.debug("Created Route: " + uri);
                    }
                    logger.info(context.getScopeName() + ":register routerHandler:" + uri);
                }
            }
        }
        if (errorHandlerRegister != null) {
            for (Map.Entry<Integer, ErrorHandler> errorHandlerEntry : errorHandlerRegister.getErrorHanderMap().entrySet()) {
                router.errorHandler(errorHandlerEntry.getKey(), errorHandlerEntry.getValue());
            }
        }
        return router;
    }

    private String getBeanPath(BeanDefinition beanDefinition) {
        AnnotationValue<Path> annotation = beanDefinition.getAnnotation(Path.class);
        if (annotation == null) {
            return "";
        }
        return annotation.stringValue().orElse("");
    }



    private String[] resolveConsumes(ExecutableMethod method) {
        return method.stringValues(Consumes.class);
    }

    private String[] resolveProduces(ExecutableMethod method) {
        String[] produces = method.stringValues(Produces.class);
        if (ArrayUtils.isEmpty(produces)) {
            produces = DEFAULT_MEDIA_TYPES;
        }
        return produces;
    }

    private HttpMethod getMethodType(ExecutableMethod<?, ?> method) {
        if (method.hasAnnotation(GET.class)) {
            return HttpMethod.GET;
        } else if (method.hasAnnotation(POST.class)) {
            return HttpMethod.POST;
        } else if (method.hasAnnotation(DELETE.class)) {
            return HttpMethod.DELETE;
        } else if (method.hasAnnotation(OPTIONS.class)) {
            return HttpMethod.OPTIONS;
        } else if (method.hasAnnotation(HEAD.class)) {
            return HttpMethod.HEAD;
        } else if (method.hasAnnotation(PUT.class)) {
            return HttpMethod.PUT;
        } else if (method.hasAnnotation(PATCH.class)) {
            return HttpMethod.PATCH;
        }
        return null;
    }

    private Handler<RoutingContext> invokeInterceptor(Object bean, BeanDefinition<?> beanDefinition, ExecutableMethod<Object, ?> method, MediaType mediaType) {
        return routingContext -> {
            Promise<Object> promise = Promise.promise();
            if (interceptorList == null || interceptorList.size() == 0) {
                invokeHandler(routingContext, bean, method, mediaType).onSuccess(promise::complete).onFailure(promise::fail);
            } else {
                Future<Void> interceptorFuture = null;
                List<Interceptor> revertInterceptors = new ArrayList<>();
                for (Interceptor interceptor : interceptorList) {
                    if (context.isNull(interceptor)) {
                        continue;
                    }
                    if (interceptorFuture == null) {
                        interceptorFuture = interceptor.preHandler(beanDefinition, method, routingContext);
                        revertInterceptors.add(interceptor);
                    } else {
                        interceptorFuture = interceptorFuture.compose(x -> {
                            revertInterceptors.add(interceptor);
                            return interceptor.preHandler(beanDefinition, method, routingContext);
                        }, Future::failedFuture);
                    }
                }
                interceptorFuture.compose(x -> invokeHandler(routingContext, bean, method, mediaType), Future::failedFuture).onComplete(x -> {
                    Future<Void> afterFuture = null;
                    for (int i = revertInterceptors.size() - 1; i >= 0; i--) {
                        Interceptor interceptor = revertInterceptors.get(i);
                        if (afterFuture == null) {
                            afterFuture = interceptor.afterHandler(beanDefinition, method, routingContext, x);
                        } else {
                            afterFuture = afterFuture.compose(y -> interceptor.afterHandler(beanDefinition, method, routingContext, x), e -> interceptor.afterHandler(beanDefinition, method, routingContext, x));
                        }
                    }
                    if (afterFuture == null) {
                        promise.handle(x);
                    } else {
                        afterFuture.onComplete(y -> promise.handle(x));
                    }
                });
            }
            AbstractResponseHandler responseHandler = responseHandlerRegister.findResponseHandler(mediaType).orElse(this.context.getBean(JsonResponseHandler.class));
            promise.future().onComplete(x -> {
                if (routingContext.response().ended()) {
                    return;
                }
                if (x.succeeded()) {
                    responseHandler.successHandler(routingContext, x.result());
                } else {
                    responseHandler.exceptionHandler(routingContext, x.cause());
                }
            });
        };
    }

    private Future<Object> invokeHandler(RoutingContext routingContext, Object bean, ExecutableMethod<Object, ?> method, MediaType mediaType) {
        Object[] args = getArgs(routingContext, method);
        Object result = method.invoke(bean, args);
        Promise<Object> promise = Promise.promise();
        if (result instanceof Promise) {
            promise = (Promise<Object>) result;
        } else if (result instanceof Future) {
            Future<Object> future = (Future) result;
            future.onSuccess(promise::complete).onFailure(promise::fail);
        } else if (result instanceof Throwable) {
            promise.fail((Throwable) result);
        }
        return promise.future();
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

    private String converter(String path){
        if (path==null||path.length()==0){
            return path;
        }
        path = path.replace("//", "/");
        Matcher matcher = pathPattern.matcher(path);
        while (matcher.find()){

            String p = matcher.group(0);
            if (p.length()>0){
                p = p.replace("{", "").replace("}", "");
                path = path.replace(matcher.group(0), ":" + p);
            }
        }
        return path;

    }
}
