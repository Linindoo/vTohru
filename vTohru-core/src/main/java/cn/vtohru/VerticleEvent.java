package cn.vtohru;

import cn.vtohru.plugin.VerticleInfo;
import io.vertx.core.Future;


public abstract class VerticleEvent {
    public abstract Future<Void> start(VerticleInfo beanDefinition);
    public abstract Future<Void> stop(VerticleInfo beanDefinition);
}
