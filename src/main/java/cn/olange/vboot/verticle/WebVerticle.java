package cn.olange.vboot.verticle;

import cn.olange.vboot.annotation.WebServer;
import cn.olange.vboot.context.VerticleApplicationContext;
import io.micronaut.context.ApplicationContext;
import io.micronaut.web.router.RouteBuilder;
import io.vertx.core.AbstractVerticle;

import javax.inject.Singleton;
import java.util.Collection;

@Singleton
@WebServer
public class WebVerticle extends AbstractVerticle {
    private VerticleApplicationContext applicationContext;
    private Collection<RouteBuilder> builders;

    public WebVerticle(ApplicationContext applicationContext, Collection<RouteBuilder> builders) {
        this.applicationContext = (VerticleApplicationContext) applicationContext;
        this.builders = builders;
    }

    @Override
    public void start() throws Exception {
        super.start();

    }

}
