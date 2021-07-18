package com.olange.verticle;

import cn.olange.vboot.annotation.VerticleContaner;
import cn.olange.vboot.annotation.WebAutoConfigure;
import cn.olange.vboot.message.MessageAutoConfigure;
import cn.olange.vboot.microservice.ServiceAutoConfigure;
import io.vertx.core.AbstractVerticle;

import javax.inject.Singleton;

@Singleton
@VerticleContaner(usePackage = "com.message")
@WebAutoConfigure(port = 9099)
@ServiceAutoConfigure
@MessageAutoConfigure
public class TestVerticle extends AbstractVerticle {

}
