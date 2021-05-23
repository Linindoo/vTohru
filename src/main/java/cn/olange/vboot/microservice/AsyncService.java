package cn.olange.vboot.microservice;

import com.mongodb.lang.NonNull;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.ServiceDiscovery;


public abstract class AsyncService<T> {
    private String registration;

    public abstract Promise<T> get();

    public static <T> AsyncService<T> create(MicroServiceDiscovery discovery, JsonObject config) {
        return new AsyncServiceImpl<T>(discovery, config);
    }


    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

}
