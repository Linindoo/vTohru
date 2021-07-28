package cn.vtohru.microservice;

import cn.vtohru.annotation.Verticle;
import cn.vtohru.microservice.annotation.Service;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Prototype;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.inject.FieldInjectionPoint;
import io.micronaut.inject.InjectionPoint;
import io.vertx.core.json.JsonObject;

@Factory
public class MicroServiceBeanFactory {

    @Verticle
    AsyncService asyncService(InjectionPoint<?> injectionPoint, ApplicationContext context) {
        FieldInjectionPoint fieldInjectionPoint = (FieldInjectionPoint) injectionPoint;
        JsonObject config = new JsonObject();
//        if (annotation != null) {
////            config.put("type", annotation.type());
//            config.put("name", fieldInjectionPoint.getName());
//        }
        MicroServiceDiscovery microServiceDiscovery = context.getBean(MicroServiceDiscovery.class);
        return AsyncService.create(microServiceDiscovery, config);
    }
}
