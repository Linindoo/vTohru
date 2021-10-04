package cn.vtohru.resource;

import cn.vtohru.annotation.Verticle;
import cn.vtohru.web.ResourceHandler;
import io.vertx.ext.web.RoutingContext;

@Verticle
public class IndexResourceHandler extends ResourceHandler {

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
        return "/hello.jpg";
    }

    @Override
    public void handle(RoutingContext context) {
        context.end("hello resource");
    }
}
