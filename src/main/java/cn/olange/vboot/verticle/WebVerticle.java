package cn.olange.vboot.verticle;

import cn.olange.vboot.annotation.WebServer;
import io.vertx.core.AbstractVerticle;

import javax.inject.Singleton;

@Singleton
@WebServer(port = 9099)
public class WebVerticle extends AbstractVerticle {

}
