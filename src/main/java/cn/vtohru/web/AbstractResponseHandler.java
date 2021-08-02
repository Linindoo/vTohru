package cn.vtohru.web;

import io.vertx.ext.web.RoutingContext;

public abstract class AbstractResponseHandler implements ResponseHandler {

    public abstract void successHandler(RoutingContext context, Object result);


    public abstract void exceptionHandler(RoutingContext context, Throwable e);

}
