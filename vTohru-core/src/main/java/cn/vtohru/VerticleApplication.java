package cn.vtohru;

import cn.vtohru.context.VerticleApplicationContext;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Requires;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.runtime.ApplicationConfiguration;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.runtime.event.ApplicationShutdownEvent;
import io.vertx.core.*;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Singleton
@Requires(missingBeans = EmbeddedApplication.class)
public class VerticleApplication implements EmbeddedApplication<VerticleApplication> {
    private static final Logger logger = LoggerFactory.getLogger(VerticleApplication.class);

    private final VerticleApplicationContext applicationContext;
    private final ApplicationConfiguration configuration;
    private List<String> publishVerticles;

    public VerticleApplication(ApplicationContext applicationContext, ApplicationConfiguration configuration) {
        this.applicationContext = (VerticleApplicationContext) applicationContext;
        this.configuration = configuration;
        this.publishVerticles = new ArrayList<>();
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
        List<Future> startFutures = new ArrayList<>();
        for (BeanDefinition<AbstractVerticle> beanDefinition : beanDefinitions) {
            Map<String, Object> map = applicationContext.getScopeMap(beanDefinition);
            DeploymentOptions deploymentOptions = new DeploymentOptions();
            deploymentOptions.setConfig(new JsonObject(map));
            AbstractVerticle bean = applicationContext.getBean(beanDefinition.getBeanType());
            Promise<Void> promise = Promise.promise();
            applicationContext.getVertx().deployVerticle(bean, deploymentOptions).onSuccess(x -> {
                logger.info("deploy Verticle : " + bean.getClass().getName() + " success as " + x);
                publishVerticles.add(x);
                promise.complete();
            }).onFailure(e -> {
                logger.error("deploy Verticle : " + bean.getClass().getName() + " fail", e);
                promise.fail(e);
            });
            startFutures.add(promise.future());
        }
        try {
            CompositeFuture.all(startFutures).toCompletionStage().toCompletableFuture().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            logger.error(e);
        }
        return this;
    }

    @Override
    public VerticleApplication stop() {
        VerticleApplicationContext applicationContext = getApplicationContext();
        if (applicationContext != null && applicationContext.isRunning()) {
            List<Future> endFutures = new ArrayList<>();
            for (String deplyID : publishVerticles) {
                Promise<Void> promise = Promise.promise();
                applicationContext.getVertx().undeploy(deplyID).onSuccess(x -> {
                    logger.info("undeploy Verticle : " + deplyID + " success");
                    promise.complete();
                }).onFailure(e -> {
                    logger.error("undeploy Verticle : " + deplyID + " fail", e);
                    promise.fail(e);
                });
                endFutures.add(promise.future());
            }
            try {
                CompositeFuture.all(endFutures).toCompletionStage().toCompletableFuture().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                logger.error(e);
            }
        }
        return this;
    }

}
