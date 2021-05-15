package cn.olange.vboot.web;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.ext.web.RoutingContext;

public abstract class AbstractResponseHandler implements ResponseHandler {

    @Override
    public void handler(RoutingContext context, Object result) {
        if (result instanceof Promise) {
            Promise promise = (Promise) result;
            promise.future().onSuccess(x -> successHandler(context, x)).onFailure(e -> exceptionHandler(context, e));
        } else if (result instanceof Future) {
            Future future = (Future) result;
            future.onSuccess(x -> successHandler(context, x)).onFailure(e -> exceptionHandler(context, e));
        } else {
            successHandler(context, result);
        }
    }

    public abstract void successHandler(RoutingContext context, Object result);


    public abstract void exceptionHandler(RoutingContext context, Object result);

}
