package cn.olange.vboot.web;


import io.vertx.ext.web.RoutingContext;

public interface ResponseHandler {

    void handler(RoutingContext context, Object result);

}
