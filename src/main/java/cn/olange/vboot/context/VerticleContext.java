package cn.olange.vboot.context;

import io.vertx.core.Vertx;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class VerticleContext {

    private static final ConcurrentHashMap<String, Vertx> map = new ConcurrentHashMap();


    public static void set(String name, Vertx vertx) {
        map.put(name, vertx);
    }

    public static <T> Optional<Vertx> get(String name) {
        return Optional.ofNullable(map.get(name));
    }

    public static Map<String, Vertx> getMap() {
        return map;
    }

}
