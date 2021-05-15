package cn.olange.vboot.web;


import io.vertx.ext.web.RoutingContext;

import javax.ws.rs.core.MediaType;
import java.util.List;

public interface ResponseHandler {

    List<MediaType> getMediaTypes();

    void handler(RoutingContext context, Object result);

}
