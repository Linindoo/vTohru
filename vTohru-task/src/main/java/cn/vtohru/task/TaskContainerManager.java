package cn.vtohru.task;

import cn.vtohru.VerticleEvent;
import cn.vtohru.context.VerticleApplicationContext;
import cn.vtohru.plugin.VerticleInfo;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.annotation.Indexed;
import io.vertx.core.Future;

import javax.inject.Singleton;

@Singleton
@Indexed(VerticleEvent.class)
public class TaskContainerManager extends VerticleEvent{
    private VerticleApplicationContext applicationContext;
    private TaskHandlerRegister taskHandlerRegister;
    public TaskContainerManager(ApplicationContext applicationContext,TaskHandlerRegister taskHandlerRegister) {
        this.applicationContext = (VerticleApplicationContext) applicationContext;
        this.taskHandlerRegister = taskHandlerRegister;
    }

    @Override
    public Future<Void> start(VerticleInfo verticleInfo) {
        return taskHandlerRegister.register();
    }

    @Override
    public Future<Void> stop(VerticleInfo beanDefinition) {
        return Future.succeededFuture();
    }

}
