package cn.vtohru.microservice;

import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;


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
