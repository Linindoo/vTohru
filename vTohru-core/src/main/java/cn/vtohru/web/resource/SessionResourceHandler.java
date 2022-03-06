package cn.vtohru.web.resource;

import cn.vtohru.annotation.GlobalScope;
import cn.vtohru.annotation.ScopeRequires;
import cn.vtohru.annotation.Verticle;
import cn.vtohru.web.ResourceHandler;
import io.micronaut.core.annotation.Order;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;

@Verticle
@Order(1)
@GlobalScope
@ScopeRequires(property = "vtohru.web.session.enable",notEquals = "false")
public class SessionResourceHandler extends ResourceHandler {
    private SessionHandler sessionHandler;
    public SessionResourceHandler(Vertx vertx) {
        this.sessionHandler = SessionHandler.create(LocalSessionStore.create(vertx, "vtohru.session"));
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
