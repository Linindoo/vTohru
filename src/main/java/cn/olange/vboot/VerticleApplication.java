package cn.olange.vboot;

import cn.olange.vboot.context.VerticleContext;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.ApplicationContextLifeCycle;
import io.micronaut.context.annotation.Requires;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.runtime.ApplicationConfiguration;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.runtime.event.ApplicationShutdownEvent;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.Map;

@Singleton
@Requires(missingBeans = EmbeddedApplication.class)
public class VerticleApplication implements EmbeddedApplication {

    private final ApplicationContext applicationContext;
    private final ApplicationConfiguration configuration;

    public VerticleApplication(ApplicationContext applicationContext, ApplicationConfiguration configuration) {
        this.applicationContext = applicationContext;
        this.configuration = configuration;
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public ApplicationConfiguration getApplicationConfiguration() {
        return configuration;
    }

    @Override
    public boolean isRunning() {
        return applicationContext.isRunning();
    }

    @Override
    public boolean isServer() {
        return true;
    }

    @Override
    public VerticleApplication start() {
        Collection<BeanDefinition<AbstractVerticle>> beanDefinitions = applicationContext.getBeanDefinitions(AbstractVerticle.class);
        for (BeanDefinition<AbstractVerticle> beanDefinition : beanDefinitions) {
            Vertx vertx = Vertx.vertx();
            AbstractVerticle bean = applicationContext.getBean(beanDefinition.getBeanType());
            vertx.deployVerticle(bean).onSuccess(System.out::println).onFailure(Throwable::printStackTrace);
            String name = bean.getClass().getName();
            VerticleContext.set(name, vertx);
        }
        return this;
    }

    @Override
    public ApplicationContextLifeCycle stop() {
        ApplicationContext applicationContext = getApplicationContext();
        if (applicationContext != null && applicationContext.isRunning()) {
            Map<String, Vertx> map = VerticleContext.getMap();
            for (Map.Entry<String, Vertx> entry : map.entrySet()) {
                entry.getValue().undeploy(entry.getKey()).onSuccess(System.out::println).onFailure(Throwable::printStackTrace);
            }
            applicationContext.stop();
            applicationContext.publishEvent(new ApplicationShutdownEvent(this));
        }
        return this;
    }
}
