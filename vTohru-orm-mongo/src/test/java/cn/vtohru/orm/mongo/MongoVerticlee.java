package cn.vtohru.orm.mongo;

import cn.vtohru.annotation.VerticleContaner;
import cn.vtohru.web.annotation.WebAutoConfigure;
import io.vertx.core.AbstractVerticle;

@VerticleContaner
@WebAutoConfigure(port = 7777)
public class MongoVerticlee extends AbstractVerticle {

}
