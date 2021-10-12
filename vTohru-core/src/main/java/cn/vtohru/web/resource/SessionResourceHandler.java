package cn.vtohru.web.resource;

import cn.vtohru.annotation.Verticle;
import cn.vtohru.context.VerticleApplicationContext;
import cn.vtohru.web.ResourceHandler;
import io.micronaut.core.annotation.Order;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;

@Verticle
@Order
public class SessionResourceHandler extends ResourceHandler {
    private SessionHandler sessionHandler;
    public SessionResourceHandler(VerticleApplicationContext context) {
        this.sessionHandler = SessionHandler.create(LocalSessionStore.create(context.getVertx(), "vtohru.session"));
    }

    @Override
    public String[] produces() {
        return new String[0];
    }

    @Override
    public String[] consumes() {
        return new String[0];
    }

    @Override
    public String path() {
        return "";
    }

    @Override
    public void handle(RoutingContext context) {
        sessionHandler.handle(context);
    }
}
