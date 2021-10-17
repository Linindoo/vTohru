package cn.vtohru.resource;

import cn.vtohru.annotation.Verticle;
import cn.vtohru.web.AbstractResponseHandler;
import cn.vtohru.web.JsonResponseHandler;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.core.annotation.Indexed;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Verticle
@Replaces(JsonResponseHandler.class)
@Indexed(AbstractResponseHandler.class)
public class ResponseBodyHander extends AbstractResponseHandler {
    @Override
    public void successHandler(RoutingContext context, Object result) {
        JsonObject ret = new JsonObject();
        context.response().putHeader("content-type", "application/json;charset=utf-8");
        ret.put("code", 1);
        ret.put("data", result);
        ret.put("msg", "操作成功");
        context.response().end(ret.toBuffer());
    }

    @Override
    public void exceptionHandler(RoutingContext context, Throwable e) {
        context.response().putHeader("content-type", "application/json;charset=utf-8");
        JsonObject ret = new JsonObject();
        ret.put("code", 0);
        ret.put("msg", e.getMessage());
        context.response().end(ret.toBuffer());
    }

    @Override
    public List<MediaType> getMediaTypes() {
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON_TYPE);
        return mediaTypes;
    }
}
