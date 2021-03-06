package cn.vtohru.aop;

import cn.vtohru.VerticleEvent;
import cn.vtohru.annotation.VerticleContaner;
import cn.vtohru.context.VerticleApplicationContext;
import io.micronaut.aop.InterceptorBean;
import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import io.micronaut.context.ApplicationContext;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.ExecutableMethod;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import javax.inject.Singleton;
import java.lang.reflect.Method;
import java.util.Collection;

@Singleton
@InterceptorBean(VerticleContaner.class)
public class VerticleContainerInterceptor implements MethodInterceptor<Object, Object> {
    private static final Logger logger = LoggerFactory.getLogger(VerticleContainerInterceptor.class);
    private VerticleApplicationContext applicationContext;
    public VerticleContainerInterceptor(ApplicationContext context) {
        this.applicationContext = (VerticleApplicationContext) context;
    }

    @Override
    public Object intercept(MethodInvocationContext<Object, Object> context) {
        ExecutableMethod executableMethod = context.getExecutableMethod();
        Method targetMethod = executableMethod.getTargetMethod();
        Object verticle = context.getTarget();
        if (verticle == null) {
            return null;
        }
        Object result = context.proceed();
        BeanDefinition<?> beanDefinition = applicationContext.getBeanDefinition(verticle.getClass());
        if ("init".equalsIgnoreCase(targetMethod.getName())) {
            applicationContext.saveVerticleInfo(beanDefinition);
        } else if ("start".equalsIgnoreCase(targetMethod.getName()) && targetMethod.getParameterCount() == 1) {
            Collection<VerticleEvent> verticleEvents = applicationContext.getBeansOfType(VerticleEvent.class);
            for (VerticleEvent verticleEvent : verticleEvents) {
                verticleEvent.start(beanDefinition);
            }
        } else if ("stop".equalsIgnoreCase(targetMethod.getName()) && targetMethod.getParameterCount() == 1) {
            Collection<VerticleEvent> verticleEvents = applicationContext.getBeansOfType(VerticleEvent.class);
            for (VerticleEvent verticleEvent : verticleEvents) {
                verticleEvent.stop(beanDefinition);
            }
        }
        return result;
    }

}
