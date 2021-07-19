package cn.vtohru.message;

import cn.vtohru.context.VerticleApplicationContext;
import cn.vtohru.message.annotation.MessageType;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.type.Argument;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.ExecutableMethod;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.HelperUtils;

import javax.ws.rs.QueryParam;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class EventBusMessageHandler<T> implements Handler<Message<JsonObject>> {
    protected MessageConsumer<JsonObject> consumer;
    private VerticleApplicationContext applicationContext;
    private BeanDefinition<T> beanDefinition;
    private ExecutableMethod<T, Object> executableMethod;
    private MessageType.Type msgType;
    private boolean includeDebugInfo = false;


    public EventBusMessageHandler(VerticleApplicationContext applicationContext, BeanDefinition<T> beanDefinition, ExecutableMethod<T, Object> executableMethod, MessageType.Type msgType) {
        this.applicationContext = applicationContext;
        this.beanDefinition = beanDefinition;
        this.executableMethod = executableMethod;
        this.msgType = msgType;
    }

    @Override
    public void handle(Message<JsonObject> message) {
        JsonObject body = message.body();
        Argument[] arguments = this.executableMethod.getArguments();
        Object[] params = new Object[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            Argument argument = arguments[i];
            if (argument.getType().isAssignableFrom(Handler.class)) {
                params[i] = HelperUtils.createHandler(message, this.includeDebugInfo);
            } else {
                AnnotationValue<QueryParam> annotation = argument.getAnnotation(QueryParam.class);
                String paramName = annotation.stringValue().orElse(argument.getName());
                params[i] = body.getMap().get(paramName);
            }
        }
        this.executableMethod.invoke(applicationContext.getBean(beanDefinition), params);
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