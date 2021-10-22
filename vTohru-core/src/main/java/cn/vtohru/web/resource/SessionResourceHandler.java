package cn.vtohru.web.resource;

import cn.vtohru.annotation.GlobalScope;
import cn.vtohru.annotation.Verticle;
import cn.vtohru.context.VerticleApplicationContext;
import cn.vtohru.web.ResourceHandler;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.annotation.Order;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;

@Verticle
@Order(1)
@GlobalScope
public class SessionResourceHandler extends ResourceHandler {
    private SessionHandler sessionHandler;
    private VerticleApplicationContext context;
    public SessionResourceHandler(ApplicationContext context) {
        this.context = (VerticleApplicationContext) context;
        this.sessionHandler = SessionHandler.create(LocalSessionStore.create(((VerticleApplicationContext) context).getVertx(), "vtohru.session"));
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

    @Override
    public boolean enable() {
        return "true".equals(this.context.getVerticleEnv("web.session.enable", String.class).orElse("true"));
    }
}
