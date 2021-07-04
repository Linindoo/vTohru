package com.olange.verticle;

import cn.olange.vboot.annotation.VerticleContaner;
import cn.olange.vboot.annotation.WebServer;
import io.vertx.core.AbstractVerticle;

import javax.inject.Singleton;

@Singleton
@VerticleContaner
@WebServer(port = 9099)
public class TestVerticle extends AbstractVerticle {

}
