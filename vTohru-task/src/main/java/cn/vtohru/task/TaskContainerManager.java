package cn.vtohru.task;

import cn.vtohru.VerticleEvent;
import cn.vtohru.context.VerticleApplicationContext;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.annotation.Indexed;
import io.micronaut.inject.BeanDefinition;
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
    public Future<Void> start(BeanDefinition<?> beanDefinition) {
        return taskHandlerRegister.register();
    }

    @Override
    public Future<Void> stop(BeanDefinition<?> beanDefinition) {
        return Future.succeededFuture();
    }

}
