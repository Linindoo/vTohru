package cn.olange.vboot.microservice;

import cn.olange.vboot.annotation.Verticle;
import cn.olange.vboot.context.VerticleApplicationContext;
import io.micronaut.context.ApplicationContext;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceReference;

@Verticle
public class MicroServiceDiscovery {
    private VerticleApplicationContext verticleApplicationContext;
    private io.vertx.servicediscovery.ServiceDiscovery serviceDiscovery;

    public MicroServiceDiscovery(ApplicationContext applicationContext) {
        this.verticleApplicationContext = (VerticleApplicationContext) applicationContext;
        this.serviceDiscovery = io.vertx.servicediscovery.ServiceDiscovery.create(this.verticleApplicationContext.getVertx());
    }

    public Future<Record> publish(Record serviceInfo) {
        return this.serviceDiscovery.publish(serviceInfo);
    }

    public Future<ServiceReference> getRecord(JsonObject config) {
        return this.serviceDiscovery.getRecord(config).compose(record -> Future.succeededFuture(this.serviceDiscovery.getReference(record)));
    }
}
