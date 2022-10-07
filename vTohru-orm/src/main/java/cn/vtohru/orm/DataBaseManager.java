package cn.vtohru.orm;

import cn.vtohru.VerticleEvent;
import cn.vtohru.context.VerticleApplicationContext;
import cn.vtohru.plugin.VerticleInfo;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.annotation.Indexed;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Singleton
@Indexed(VerticleEvent.class)
public class DataBaseManager extends VerticleEvent {
    private VerticleApplicationContext applicationContext;

    public DataBaseManager(ApplicationContext applicationContext) {
        this.applicationContext = (VerticleApplicationContext) applicationContext;
    }

    @Override
    public Future<Void> start(VerticleInfo beanDefinition) {
        Promise<Void> promise = Promise.promise();
        Collection<DataStore> dataStores = applicationContext.getBeansOfType(DataStore.class);
        List<Future> startFuture = new ArrayList<>();
        for (DataStore dataStore : dataStores) {
            startFuture.add(dataStore.start());
        }
        CompositeFuture.all(startFuture).onSuccess(x -> promise.complete()).onFailure(promise::fail);
        return promise.future();
    }

    @Override
    public Future<Void> stop(VerticleInfo beanDefinition) {
        Promise<Void> promise = Promise.promise();
        Collection<DataStore> dataStores = applicationContext.getBeansOfType(DataStore.class);
        List<Future> endFuture = new ArrayList<>();
        for (DataStore dataStore : dataStores) {
            endFuture.add(dataStore.stop());
        }
        CompositeFuture.all(endFuture).onSuccess(x -> promise.complete()).onFailure(promise::fail);
        return promise.future();
    }
}
