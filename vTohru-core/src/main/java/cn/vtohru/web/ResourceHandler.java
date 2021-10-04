package cn.vtohru.web;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public abstract class ResourceHandler implements Handler<RoutingContext> {

    public abstract String[] produces();

    public abstract String[] consumes();

    public abstract String path();
}
