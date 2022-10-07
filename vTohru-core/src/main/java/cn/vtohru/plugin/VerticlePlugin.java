package cn.vtohru.plugin;

import cn.vtohru.VerticleEvent;
import cn.vtohru.context.VerticleApplicationContext;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Decebal Suiu
 */
public abstract class VerticlePlugin extends Plugin implements Verticle {
    protected Vertx vertx;
    protected Context context;
    private VerticleInfo verticleInfo;

    private PluginApplicationContext pluginApplicationContext;

    public VerticlePlugin(PluginWrapper wrapper, PluginApplicationContext pluginApplicationContext) {
        super(wrapper);
        this.pluginApplicationContext = pluginApplicationContext;
    }

    public final PluginApplicationContext getApplicationContext() {
        return pluginApplicationContext;
    }

    public Vertx getVertx() {
        return this.vertx;
    }

    public void init(Vertx vertx, Context context) {
        this.vertx = vertx;
        this.context = context;
        this.verticleInfo = new VerticleInfo();
        this.verticleInfo.setPluginWrapper(getWrapper());
        this.verticleInfo.setType(this.getClass());
        context.put(VerticleApplicationContext.SCOPE_VERTICLE_NAME, getWrapper().getPluginId());
    }

    public String deploymentID() {
        return this.context.deploymentID();
    }

    public JsonObject config() {
        return this.context.config();
    }

    public List<String> processArgs() {
        return this.context.processArgs();
    }

    public void start(Promise<Void> startPromise) throws Exception {
        PluginApplicationContext applicationContext = getApplicationContext();
        applicationContext.start();

        vertx.executeBlocking(c -> {
            Collection<VerticleEvent> verticleEvents = applicationContext.getBeansOfType(VerticleEvent.class);
            List<Future> futures = new ArrayList<>();
            System.out.println("thread:" + Thread.currentThread().getName());
            for (VerticleEvent verticleEvent : verticleEvents) {
                futures.add(verticleEvent.start(verticleInfo));
            }
            CompositeFuture.all(futures).onSuccess(c::complete).onFailure(c::fail);
        }).onSuccess(x->{
            this.start();
            startPromise.complete();
        });

    }

    public void stop(Promise<Void> stopPromise) throws Exception {
        PluginApplicationContext applicationContext = getApplicationContext();
        Future<Void> future = null;
        Collection<VerticleEvent> verticleEvents = applicationContext.getBeansOfType(VerticleEvent.class);
        for (VerticleEvent verticleEvent : verticleEvents) {
            if (future == null) {
                future = verticleEvent.stop(verticleInfo);
            } else {
                future.compose(x -> verticleEvent.stop(verticleInfo), e -> verticleEvent.stop(verticleInfo));
            }
        }
        if (future != null) {
            future.toCompletionStage().toCompletableFuture().get();
        }
        this.stop();
        applicationContext.stop();
        stopPromise.complete();
    }
}
