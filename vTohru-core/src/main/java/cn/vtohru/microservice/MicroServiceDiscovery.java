package cn.vtohru.microservice;

import cn.vtohru.annotation.GlobalScope;
import cn.vtohru.annotation.Verticle;
import cn.vtohru.context.VerticleApplicationContext;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.annotation.Indexed;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.servicediscovery.ServiceReference;
import io.vertx.servicediscovery.impl.DiscoveryImpl;
import io.vertx.servicediscovery.types.EventBusService;

@Verticle
@GlobalScope
@Indexed(MicroServiceDiscovery.class)
public class MicroServiceDiscovery extends DiscoveryImpl {
    private VerticleApplicationContext verticleApplicationContext;

    public MicroServiceDiscovery(ApplicationContext applicationContext) {
        super(((VerticleApplicationContext) applicationContext).getVertx(), new ServiceDiscoveryOptions());
        this.verticleApplicationContext = (VerticleApplicationContext) applicationContext;
    }

    public Future<Record> publishService(Record serviceInfo) {
        return this.publish(serviceInfo);
    }

    public Future<ServiceReference> getService(JsonObject config) {
        return this.getRecord(config).compose(x -> {
            if (x.getType().equalsIgnoreCase(EventBusService.TYPE)) {
                return Future.succeededFuture(new EventBusServiceReference(this.verticleApplicationContext, this, x, new JsonObject()));
            } else {
                return Future.succeededFuture(this.getReference(x));
            }
        });
    }
}
