package cn.olange.vboot.web;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import javax.inject.Singleton;

@Singleton
public class JsonResponseHandler extends AbstractResponseHandler{
    @Override
    public void successHandler(RoutingContext context, Object result) {
        JsonObject ret = new JsonObject();
        ret.put("code", 0);
        ret.put("data", result);
        ret.put("msg", "操作成功");
        context.response().end(ret.toBuffer());
    }

    @Override
    public void exceptionHandler(RoutingContext context, Object result) {
        JsonObject ret = new JsonObject();
        ret.put("code", -1);
        ret.put("data", result);
        ret.put("msg", "操作失败");
        context.response().end(ret.toBuffer());
    }
}
