package cn.vtohru;

import cn.vtohru.context.VerticleApplicationContext;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.ApplicationContextLifeCycle;
import io.micronaut.context.annotation.Requires;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.runtime.ApplicationConfiguration;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.runtime.event.ApplicationShutdownEvent;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import javax.inject.Singleton;
import java.util.Collection;

@Singleton
@Requires(missingBeans = EmbeddedApplication.class)
public class VerticleApplication implements EmbeddedApplication {
    private static final Logger logger = LoggerFactory.getLogger(VerticleApplication.class);

    private final VerticleApplicationContext applicationContext;
    private final ApplicationConfiguration configuration;

    public VerticleApplication(ApplicationContext applicationContext, ApplicationConfiguration configuration) {
        this.applicationContext = (VerticleApplicationContext) applicationContext;
        this.configuration = configuration;
    }

    @Override
    public VerticleApplicationContext getApplicationContext() {
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
            AbstractVerticle bean = applicationContext.getBean(beanDefinition.getBeanType());
            applicationContext.getVertx().deployVerticle(bean).onSuccess(x->{
                logger.info("deploy Verticle : " + bean.getClass().getName() + " success as " + x);
            }).onFailure(e->{
                logger.error("deploy Verticle : " + bean.getClass().getName() + " fail",  e);
            });
        }
        return this;
    }

    @Override
    public ApplicationContextLifeCycle stop() {
        VerticleApplicationContext applicationContext = getApplicationContext();
        if (applicationContext != null && applicationContext.isRunning()) {
            Collection<BeanDefinition<AbstractVerticle>> beanDefinitions = applicationContext.getBeanDefinitions(AbstractVerticle.class);
            for (BeanDefinition<AbstractVerticle> beanDefinition : beanDefinitions) {
                AbstractVerticle bean = applicationContext.getBean(beanDefinition.getBeanType());
                applicationContext.getVertx().undeploy(bean.deploymentID()).onSuccess(x -> {
                    logger.info("undeploy Verticle : " + bean.getClass().getName() + " success");
                }).onFailure(e -> {
                    logger.error("undeploy Verticle : " + bean.getClass().getName() + " fail", e);
                });
            }
            applicationContext.stop();
            applicationContext.publishEvent(new ApplicationShutdownEvent(this));
        }
        return this;
    }

}
