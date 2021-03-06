package cn.vtohru;

import cn.vtohru.context.VerticleApplicationContext;
import io.micronaut.context.ApplicationContext;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.runtime.ApplicationConfiguration;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.runtime.exceptions.ApplicationStartupException;
import io.vertx.core.*;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

@Singleton
public class VerticleApplication implements EmbeddedApplication<VerticleApplication> {
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
        Boolean clustered = applicationContext.get(VerticleApplicationContext.VTOHRU + ".clustered", Boolean.class).orElse(false);
        Vertx vertx;
        if (!clustered) {
            vertx = Vertx.vertx();
        } else {
            try {
                ClusterManager mgr = new HazelcastClusterManager();
                VertxOptions options = new VertxOptions();
                options.setClusterManager(mgr);
                vertx =  Vertx.clusteredVertx(options).toCompletionStage().toCompletableFuture().get();
            } catch (InterruptedException | ExecutionException e) {
                logger.error(e);
                throw new ApplicationStartupException("init vertx fail:" + e.getMessage(), e);
            }
        }
        applicationContext.setVertx(vertx);
        applicationContext.registerSingleton(Vertx.class, vertx);
        Collection<AbstractVerticle> abstractVerticles = applicationContext.getBeansOfType(AbstractVerticle.class);
        Future<String> publishFuture = null;
        for (AbstractVerticle abstractVerticle : abstractVerticles) {
            BeanDefinition<? extends AbstractVerticle> beanDefinition = applicationContext.getBeanDefinition(abstractVerticle.getClass());
            JsonObject map = applicationContext.getVConfig(beanDefinition);
            DeploymentOptions deploymentOptions = new DeploymentOptions();
            deploymentOptions.setConfig(map);
            if (publishFuture == null) {
                publishFuture = applicationContext.getVertx().deployVerticle(abstractVerticle, deploymentOptions);
            } else {
                publishFuture = publishFuture.compose(x->{
                    return applicationContext.getVertx().deployVerticle(abstractVerticle, deploymentOptions);
                },e->{
                    return applicationContext.getVertx().deployVerticle(abstractVerticle, deploymentOptions);
                });
            }
        }
        if (publishFuture != null) {
            publishFuture.onFailure(e -> {
                e.printStackTrace();
                logger.error(e);
            }).onSuccess(x -> {
                logger.info("all verticle publish success");
            });
        }
        return this;
    }

    @Override
    public VerticleApplication stop() {
        VerticleApplicationContext applicationContext = getApplicationContext();
        if (applicationContext != null && applicationContext.isRunning()) {
            for (String deploymentID : applicationContext.getVertx().deploymentIDs()) {
                applicationContext.getVertx().undeploy(deploymentID).onSuccess(x -> {
                    logger.info("undeploy Verticle : " + deploymentID + " success");
                }).onFailure(e -> {
                    logger.error("undeploy Verticle : " + deploymentID + " fail", e);
                });
            }
            applicationContext.stop();
        }
        return this;
    }

}
