package cn.vtohru.task;

import cn.vtohru.annotation.GlobalScope;
import cn.vtohru.annotation.Verticle;
import cn.vtohru.context.VerticleApplicationContext;
import cn.vtohru.task.annotation.Delay;
import cn.vtohru.task.annotation.Periodic;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.type.Argument;
import io.micronaut.inject.ExecutableMethod;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import javax.ws.rs.core.Context;

@Verticle
@GlobalScope
public class TaskHandlerRegister {
    private VerticleApplicationContext context;
    private TaskAnnotatedMethodHandler taskAnnotatedMethodHandler;
    private static final Logger logger = LoggerFactory.getLogger(TaskHandlerRegister.class);


    public TaskHandlerRegister(ApplicationContext context, TaskAnnotatedMethodHandler taskAnnotatedMethodHandler) {
        this.context = (VerticleApplicationContext) context;
        this.taskAnnotatedMethodHandler = taskAnnotatedMethodHandler;
    }

    public Future<Void> register() {
        for (TaskAnnotatedMethodHandler.TaskDefinition taskDefinition : taskAnnotatedMethodHandler.getTaskDefinitionList()) {
            if (context.isScoped(taskDefinition.getBeanDefinition())) {
                ExecutableMethod executableMethod = taskDefinition.getExecutableMethod();
                AnnotationValue<Delay> delayAnnotationValue = executableMethod.getAnnotation(Delay.class);
                Object contextBean = context.getBean(taskDefinition.getBeanDefinition());
                if (delayAnnotationValue != null) {
                    long delay = delayAnnotationValue.longValue("delay").orElse(0);
                    if (delay != 0) {
                        context.getVertx().setTimer(delay, x -> {
                            Object[] args = getArgs(x, executableMethod);
                            try {
                                executableMethod.invoke(contextBean, args);
                            } catch (Exception e) {
                                logger.error(e);
                            }
                        });
                    }
                } else {
                    AnnotationValue<Periodic> periodicAnnotationValue = executableMethod.getAnnotation(Periodic.class);
                    if (periodicAnnotationValue != null) {
                        long delay = periodicAnnotationValue.longValue("delay").orElse(0);
                        if (delay != 0) {
                            context.getVertx().setPeriodic(delay,x->{
                                try {
                                    Object invokeResult = executableMethod.invoke(contextBean, getArgs(x, executableMethod));
                                    if (invokeResult instanceof Future) {
                                        ((Future<?>) invokeResult).onFailure(e->{
                                            context.getVertx().cancelTimer(x);
                                        });
                                    }
                                } catch (Exception e) {
                                    logger.error(e);
                                }
                            });
                        }
                    }
                }
            }
        }
        return Future.succeededFuture();
    }

    private Object[] getArgs(long taskId, ExecutableMethod<?, ?> method) {
        Argument<?>[] arguments = method.getArguments();
        Object[] objects = new Object[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            Argument<?> argument = arguments[i];
            if (argument.isAnnotationPresent(Context.class)) {
                if (argument.getType() == Vertx.class) {
                    objects[i] = context.getVertx();
                } else if (argument.getType() == Long.class || argument.getType() == long.class) {
                    objects[i] = taskId;
                }
            }
        }
        return objects;
    }
}
