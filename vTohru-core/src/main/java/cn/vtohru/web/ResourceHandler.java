package cn.vtohru.web;

import io.micronaut.core.annotation.Indexed;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

@Indexed(ResourceHandler.class)
public abstract class ResourceHandler implements Handler<RoutingContext> {

    public abstract String[] produces();

    public abstract String[] consumes();

    public abstract String path();
}
