package cn.vtohru.orm;

import cn.vtohru.annotation.VerticleContaner;
import cn.vtohru.web.annotation.WebAutoConfigure;
import cn.vtohru.web.annotation.WebService;
import io.vertx.core.AbstractVerticle;

@VerticleContaner(usePackage = "cn.vtohru.orm")
@WebAutoConfigure(service = @WebService(name = "test"),port = 8083)
public class OrmVerticle extends AbstractVerticle {

}
