package cn.vtohru.sql;

import cn.vtohru.annotation.VerticleContaner;
import cn.vtohru.web.annotation.WebAutoConfigure;
import io.vertx.core.AbstractVerticle;
@VerticleContaner
@WebAutoConfigure(port = 8888)
public class SqlVerticle extends AbstractVerticle {

}
