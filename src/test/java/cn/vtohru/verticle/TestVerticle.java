package cn.vtohru.verticle;

import cn.vtohru.annotation.VerticleContaner;
import cn.vtohru.web.annotation.WebAutoConfigure;
import cn.vtohru.message.annotation.MessageAutoConfigure;
import cn.vtohru.microservice.annotation.ServiceAutoConfigure;
import io.vertx.core.AbstractVerticle;

import javax.inject.Singleton;

@Singleton
@VerticleContaner(usePackage = "com.message")
@WebAutoConfigure(port = 9099)
@ServiceAutoConfigure
@MessageAutoConfigure
public class TestVerticle extends AbstractVerticle {

}
