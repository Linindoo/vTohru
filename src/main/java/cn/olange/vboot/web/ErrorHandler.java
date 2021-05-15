package cn.olange.vboot.web;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public abstract class ErrorHandler  implements Handler<RoutingContext> {
    public abstract int getCode();
}
