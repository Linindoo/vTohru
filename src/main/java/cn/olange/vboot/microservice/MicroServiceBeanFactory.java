//package cn.olange.vboot.microservice;
//
//import io.micronaut.context.annotation.Factory;
//import io.micronaut.context.annotation.Prototype;
//import io.micronaut.core.annotation.Nullable;
//import io.micronaut.inject.FieldInjectionPoint;
//import io.micronaut.inject.InjectionPoint;
//import io.vertx.core.json.JsonObject;
//
//
//
//@Factory
//public class MicroServiceBeanFactory {
//
//    @Prototype
//    AsyncService asyncService(@Nullable InjectionPoint<?> injectionPoint, MicroServiceDiscovery microServiceDiscovery) {
//        FieldInjectionPoint fieldInjectionPoint = (FieldInjectionPoint) injectionPoint;
//        Client annotation = fieldInjectionPoint.getField().getAnnotation(Service.class);
//        JsonObject config = new JsonObject();
//        if (annotation != null) {
////            config.put("type", annotation.type());
//            config.put("name", annotation.value());
//        }
//        return AsyncService.create(microServiceDiscovery, config);
//    }
//
//
//}
