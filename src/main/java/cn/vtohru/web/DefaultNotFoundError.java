package cn.vtohru.web;

import cn.vtohru.annotation.Verticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

@Verticle
public class DefaultNotFoundError extends ErrorHandler{
    @Override
    public int getCode() {
        return 404;
    }

    @Override
    public void handle(RoutingContext context) {
        context.response().putHeader("content-type", "application/json;charset=utf-8");
        JsonObject data = new JsonObject();
        data.put("code", 0);
        data.put("msg", "无效的资源");
        data.put("data", "");
        context.response().end(data.toBuffer());
    }
}
