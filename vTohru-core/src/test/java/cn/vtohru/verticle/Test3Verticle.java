package cn.vtohru.verticle;

import cn.vtohru.annotation.VerticleContaner;
import cn.vtohru.message.annotation.MessageAutoConfigure;
import cn.vtohru.microservice.annotation.ServiceAutoConfigure;
import cn.vtohru.web.annotation.WebAutoConfigure;
import cn.vtohru.web.annotation.WebService;
import io.vertx.core.AbstractVerticle;

@VerticleContaner
@WebAutoConfigure(service = @WebService(name = "test3"))
public class Test3Verticle extends AbstractVerticle {

}
