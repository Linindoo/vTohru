package cn.vtohru.microservice;

import cn.vtohru.context.VerticleApplicationContext;
import io.micronaut.context.ApplicationContext;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.AbstractServiceReference;
import io.vertx.servicediscovery.utils.ClassLoaderUtils;

import java.util.Objects;

public class EventBusServiceReference<T> extends AbstractServiceReference<T> {

    private final DeliveryOptions deliveryOptions;
    private final String serviceInterface;
    private VerticleApplicationContext verticleApplicationContext;

    EventBusServiceReference(ApplicationContext verticleApplicationContext, ServiceDiscovery discovery, Record record, JsonObject conf) {
        super(((VerticleApplicationContext)verticleApplicationContext).getVertx(), discovery, record);
        this.verticleApplicationContext = (VerticleApplicationContext) verticleApplicationContext;
        this.serviceInterface = record.getMetadata().getString("service.interface");
        if (conf != null) {
            this.deliveryOptions = new DeliveryOptions(conf);
        } else {
            this.deliveryOptions = null;
        }

        Objects.requireNonNull(this.serviceInterface);
    }

    public synchronized T retrieve() {
        Class<T> itf = ClassLoaderUtils.load(this.serviceInterface, this.getClass().getClassLoader());
        if (itf == null) {
            throw new IllegalStateException("Cannot load class " + this.serviceInterface);
        } else {
            return this.verticleApplicationContext.createBean(itf);
        }
    }
}
