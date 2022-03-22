package cn.vtohru.message;

import cn.vtohru.context.VerticleApplicationContext;
import cn.vtohru.message.annotation.MessageType;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.type.Argument;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.ExecutableMethod;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.HelperUtils;

import javax.ws.rs.QueryParam;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class EventBusMessageHandler<T> implements Handler<Message<JsonObject>> {
    protected MessageConsumer<JsonObject> consumer;
    private VerticleApplicationContext applicationContext;
    private BeanDefinition<T> beanDefinition;
    private ExecutableMethod<T, Object> executableMethod;
    private MessageType.Type msgType;
    private boolean includeDebugInfo = false;


    public EventBusMessageHandler(ApplicationContext applicationContext, BeanDefinition<T> beanDefinition, ExecutableMethod<T, Object> executableMethod, MessageType.Type msgType) {
        this.applicationContext = (VerticleApplicationContext) applicationContext;
        this.beanDefinition = beanDefinition;
        this.executableMethod = executableMethod;
        this.msgType = msgType;
    }

    @Override
    public void handle(Message<JsonObject> message) {
        JsonObject body = message.body();
        Argument[] arguments = this.executableMethod.getArguments();
        Object[] params = new Object[arguments.length];
        boolean futureUse = Future.class.isAssignableFrom(this.executableMethod.getReturnType().getType());
        for (int i = 0; i < arguments.length; i++) {
            Argument argument = arguments[i];
            if (argument.getType().isAssignableFrom(Handler.class)) {
                params[i] = HelperUtils.createHandler(message, this.includeDebugInfo);
                futureUse = false;
            } else {
                AnnotationValue<QueryParam> annotation = argument.getAnnotation(QueryParam.class);
                String paramName = Optional.ofNullable(annotation).map(x->x.stringValue().orElse(argument.getName())).orElse(argument.getName());
                Object value = body.getMap().get(paramName);
                if (value != null) {
                    if (argument.isInstance(value)) {
                        params[i] = body.getMap().get(paramName);
                    } else if (argument.getType().isAssignableFrom(JsonObject.class)) {
                        params[i] = body.getJsonObject(paramName);
                    } else if (argument.getType().isAssignableFrom(JsonArray.class)) {
                        params[i] = body.getJsonArray(paramName);
                    } else {
                        JsonObject entries = body.getJsonObject(paramName);
                        params[i] = entries.mapTo(argument.getType());
                    }
                } else {
                    params[i] = null;
                }
            }
        }
        Object invoke = this.executableMethod.invoke(applicationContext.getBean(beanDefinition), params);
        if (futureUse) {
            Future<Object> future = (Future<Object>) invoke;
            future.onComplete(HelperUtils.createHandler(message, this.includeDebugInfo));
        }
    }

    public MessageConsumer<JsonObject> register(EventBus eventBus, String address) {
        return this.register(eventBus, address, (List)null);
    }


    public MessageConsumer<JsonObject> register(EventBus eventBus, String address, List<Function<Message<JsonObject>, Future<Message<JsonObject>>>> interceptors) {
        Handler<Message<JsonObject>> handler = this.configureHandler(interceptors);
        this.consumer = eventBus.consumer(address, handler);
        return this.consumer;
    }

    private Handler<Message<JsonObject>> configureHandler(List<Function<Message<JsonObject>, Future<Message<JsonObject>>>> interceptors) {
        Handler<Message<JsonObject>> handler = this;
        Function interceptor = null;
        if (interceptors != null) {
            Handler<Message<JsonObject>> finalHandler = handler;
            Function finalInterceptor = interceptor;
            for(Iterator var3 = interceptors.iterator(); var3.hasNext(); handler = (msg) -> {
                Future<Message<JsonObject>> fut = (Future) finalInterceptor.apply(msg);
                fut.onComplete((ar) -> {
                    if (ar.succeeded()) {
                        finalHandler.handle(msg);
                    } else {
                        ReplyException exception = (ReplyException)ar.cause();
                        msg.fail(exception.failureCode(), exception.getMessage());
                    }
                });
            }) {
                interceptor = (Function)var3.next();
            }
        }
        return (Handler)handler;
    }

    public Future<Void> unregister() {
        return consumer.unregister();
    }
}
