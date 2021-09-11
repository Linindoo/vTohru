package cn.vtohru.web;

import cn.vtohru.annotation.Verticle;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

@Verticle
public class DefaultIntervalError extends ErrorHandler{
    private static final Logger logger = LoggerFactory.getLogger(DefaultIntervalError.class);

    @Override
    public int getCode() {
        return 500;
    }

    @Override
    public void handle(RoutingContext context) {
        context.response().putHeader("content-type", "application/json;charset=utf-8");
        context.failure().printStackTrace();
        logger.error(context.failure());
        JsonObject data = new JsonObject();
        data.put("code", -1);
        data.put("msg", "服务器驾崩了");
        data.put("data", "");
        context.response().end(data.toBuffer());
    }
}
