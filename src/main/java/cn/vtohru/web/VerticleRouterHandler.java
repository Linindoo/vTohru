package cn.vtohru.web;

import cn.vtohru.annotation.Verticle;
import cn.vtohru.context.VerticleApplicationContext;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.type.Argument;
import io.micronaut.core.util.ArrayUtils;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.core.util.StringUtils;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.ExecutableMethod;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
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
import java.util.Arrays;
import java.util.Map;

@Verticle
public class VerticleRouterHandler {
    private static final Logger logger = LoggerFactory.getLogger(VerticleRouterHandler.class);
    private static final String[] DEFAULT_MEDIA_TYPES = new String[]{"application/json"};
    private VerticleApplicationContext context;
    private VerticleAnnotatedMethodRouteBuilder routeBuilder;
    private ErrorHandlerRegister errorHandlerRegister;
    private ResponseHandlerRegister responseHandlerRegister;
    private Router router;
    private HttpServer httpServer;

    public VerticleRouterHandler(ApplicationContext context, VerticleAnnotatedMethodRouteBuilder routeBuilder, ErrorHandlerRegister errorHandlerRegister, ResponseHandlerRegister responseHandlerRegister) {
        this.context = (VerticleApplicationContext) context;
        this.routeBuilder = routeBuilder;
        this.errorHandlerRegister = errorHandlerRegister;
        this.responseHandlerRegister = responseHandlerRegister;
        this.router = Router.router(this.context.getVertx());
    }

    public Future<Void> registerRouter(String host, int port) {
        buildRouter();
        return context.getVertx().createHttpServer().requestHandler(this.router).listen(port, host).compose(x -> {
            this.httpServer = x;
            logger.info("start http server success");
            return Future.succeededFuture();
        }, e -> {
            logger.error(e);
            return Future.failedFuture(e);
        });
    }

    private void buildRouter() {
        for (VerticleAnnotatedMethodRouteBuilder.RouteDefinition routeDefinition : routeBuilder.getRouteDefinitions()) {
            if (context.isScoped(routeDefinition.getBeanDefinition())) {
                HttpMethod methodType = getMethodType(routeDefinition.getExecutableMethod());
                if (methodType == null) {
                    continue;
                }
                BeanDefinition bean = routeDefinition.getBeanDefinition();
                ExecutableMethod method = routeDefinition.getExecutableMethod();
                String beanPath = getBeanPath(bean);
                String uri = method.stringValue(Path.class).orElse("");
                String[] produces = resolveProduces(method);
                String[] consumes = resolveConsumes(method);
                MediaType mediaType = Arrays.stream(produces).findFirst().map(MediaType::valueOf).orElse(MediaType.APPLICATION_JSON_TYPE);
                Route route = router.route(methodType, beanPath + uri);
                route.produces(String.join(";", produces)).consumes(String.join(";", consumes));
                route.handler(invokeHandler(bean, method, mediaType));
                if (logger.isDebugEnabled()) {
                    logger.debug("Created Route: " + uri);
                }
            }
        }
        if (errorHandlerRegister != null) {
            for (Map.Entry<Integer, ErrorHandler> errorHandlerEntry : errorHandlerRegister.getErrorHanderMap().entrySet()) {
                router.errorHandler(errorHandlerEntry.getKey(), errorHandlerEntry.getValue());
            }
        }

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

    public Future<Void> stopServer() {
        return httpServer.close();
    }
}
