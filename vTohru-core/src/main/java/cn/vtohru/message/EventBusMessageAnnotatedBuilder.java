package cn.vtohru.message;

import cn.vtohru.message.annotation.MessageListener;
import cn.vtohru.context.VerticleApplicationContext;
import cn.vtohru.message.annotation.MessageAddress;
import cn.vtohru.message.annotation.MessageType;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.processor.ExecutableMethodProcessor;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.ExecutableMethod;
import io.vertx.core.eventbus.EventBus;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class EventBusMessageAnnotatedBuilder implements ExecutableMethodProcessor<MessageListener> {
    private VerticleApplicationContext applicationContext;
    private Map<BeanDefinition<?>,List<ExecutableMethod<?, ?>>> listenerMethodMap = new HashMap<>();
    private List<EventBusMessageHandler<?>> eventBusMessageHandlers = new ArrayList<>();


    public EventBusMessageAnnotatedBuilder(ApplicationContext applicationContext) {
        this.applicationContext = (VerticleApplicationContext) applicationContext;
    }

    @Override
    public void process(BeanDefinition<?> beanDefinition, ExecutableMethod<?, ?> method) {
        if (method.hasAnnotation(MessageAddress.class)) {
            List<ExecutableMethod<?, ?>> executableMethods = listenerMethodMap.computeIfAbsent(beanDefinition, k -> new ArrayList<>());
            if (!executableMethods.contains(method)) {
                executableMethods.add(method);
            }
        }
    }

    public void register() {
        for (Map.Entry<BeanDefinition<?>, List<ExecutableMethod<?, ?>>> entry : listenerMethodMap.entrySet()) {
            BeanDefinition<?> beanDefinition = entry.getKey();
            for (ExecutableMethod<?, ?> executableMethod : entry.getValue()) {
                AnnotationValue<MessageAddress> methodAnnotation = executableMethod.getAnnotation(MessageAddress.class);
                if (methodAnnotation != null) {
                    String method_address = methodAnnotation.stringValue().orElse("");
                    MessageType.Type msgType = methodAnnotation.getValue(MessageType.Type.class).orElse(MessageType.Type.PUBLISH);
                    EventBus eventBus = applicationContext.getVertx().eventBus();
                    EventBusMessageHandler<?> eventBusMessageHandler = new EventBusMessageHandler(applicationContext, beanDefinition, executableMethod,msgType);
                    eventBusMessageHandler.register(eventBus, method_address);
                    eventBusMessageHandlers.add(eventBusMessageHandler);
                }
            }
        }
    }

    public void unregister() {
        for (EventBusMessageHandler<?> eventBusMessageHandler : eventBusMessageHandlers) {
            eventBusMessageHandler.unregister();
        }
    }
}
