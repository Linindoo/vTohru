package cn.vtohru.web.resource;

import cn.vtohru.annotation.GlobalScope;
import cn.vtohru.annotation.ScopeRequires;
import cn.vtohru.annotation.Verticle;
import cn.vtohru.context.VerticleApplicationContext;
import cn.vtohru.web.ResourceHandler;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.annotation.Order;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

@Verticle
@Order(2)
@GlobalScope
public class BodyResourceHandler extends ResourceHandler {
    private VerticleApplicationContext context;
    public BodyResourceHandler(ApplicationContext context) {
        this.context = (VerticleApplicationContext) context;
    }

    private BodyHandler bodyHandler = BodyHandler.create().setDeleteUploadedFilesOnEnd(true);

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
        bodyHandler.handle(context);
    }

    @Override
    public boolean enable() {
        return "true".equals(this.context.getVerticleEnv("web.body.enable", String.class).orElse("true"));
    }

}
