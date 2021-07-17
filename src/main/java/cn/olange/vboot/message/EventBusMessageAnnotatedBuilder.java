package cn.olange.vboot.message;

import cn.olange.vboot.annotation.Verticle;
import cn.olange.vboot.context.VerticleApplicationContext;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.processor.ExecutableMethodProcessor;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.ExecutableMethod;
import io.vertx.core.eventbus.EventBus;

import java.util.*;

@Verticle
public class EventBusMessageAnnotatedBuilder implements ExecutableMethodProcessor<MessageListener> {
    private List<BeanDefinition<?>> messageListener = new ArrayList<>();
    private VerticleApplicationContext applicationContext;
    private Map<BeanDefinition<?>,List<ExecutableMethod<?, ?>>> listenerMethodMap = new HashMap<>();


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
                    if (msgType == MessageType.Type.PUBLISH) {
                        EventBusMessageHandler eventBusMessageHandler = new EventBusMessageHandler(applicationContext, beanDefinition, executableMethod);
                        eventBusMessageHandler.register(eventBus, method_address);
                    }
                }
            }
        }
    }

    public void unregister() {

    }
}
