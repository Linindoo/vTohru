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
@ScopeRequires(property = "web.body.enable",notEquals = "false")
public class BodyResourceHandler extends ResourceHandler {
    private BodyHandler bodyHandler;
    private VerticleApplicationContext context;
    public BodyResourceHandler(ApplicationContext context) {
        this.context = (VerticleApplicationContext) context;
        this.bodyHandler = BodyHandler.create().setDeleteUploadedFilesOnEnd(true);
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
        bodyHandler.handle(context);
    }

}
