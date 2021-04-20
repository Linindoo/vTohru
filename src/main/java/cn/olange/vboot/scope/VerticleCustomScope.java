package cn.olange.vboot.scope;

import cn.olange.vboot.annotation.Verticle;
import cn.olange.vboot.context.VerticleApplicationContext;
import cn.olange.vboot.event.VerticleTerminatedEvent;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.BeanContext;
import io.micronaut.context.BeanResolutionContext;
import io.micronaut.context.LifeCycle;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.scope.CustomScope;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.util.ArgumentUtils;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.BeanIdentifier;
import io.micronaut.inject.DisposableBeanDefinition;
import io.vertx.core.Context;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class VerticleCustomScope implements CustomScope<Verticle>, LifeCycle<VerticleCustomScope>, ApplicationEventListener<VerticleTerminatedEvent> {
    private static final Logger logger = LoggerFactory.getLogger(VerticleCustomScope.class);
    public static final String SCOPED_BEANS_ATTRIBUTE = "cn.olange.vboot.SCOPED_BEANS";
    private static final String SCOPED_BEAN_DEFINITIONS = "cn.olange.vboot.SCOPED_BEAN_DEFINITIONS";
    private VerticleApplicationContext beanContext;

    public VerticleCustomScope(BeanContext beanContext) {
        this.beanContext = (VerticleApplicationContext) beanContext;
    }

    @Override
    public Class<Verticle> annotationType() {
        return Verticle.class;
    }

    public <T> T get(BeanResolutionContext resolutionContext, BeanDefinition<T> beanDefinition, BeanIdentifier identifier, Provider<T> provider) {
        Context context = beanContext.getVertx().getOrCreateContext();
        ConcurrentHashMap scopedBeanMap = this.getRequestScopedBeans(context);
        ConcurrentHashMap scopedBeanDefinitionMap = this.getRequestScopedBeanDefinitions(context);
        T bean = (T) scopedBeanMap.get(identifier);
        if (bean == null) {
            synchronized(this) {
                bean = (T) scopedBeanMap.get(identifier);
                if (bean == null) {
                    bean = provider.get();
//                    if (bean instanceof RequestAware) {
//                        ((RequestAware)bean).setRequest(httpRequest);
//                    }
                    scopedBeanMap.put(identifier, bean);
                    scopedBeanDefinitionMap.put(identifier, beanDefinition);
                }
            }
        }
        return bean;
    }

    public <T> Optional<T> remove(BeanIdentifier identifier) {
        Context context = beanContext.getVertx().getOrCreateContext();
        T bean = (T) this.getRequestScopedBeans(context).remove(identifier);
        BeanDefinition<T> beanDefinition = (BeanDefinition)this.getRequestScopedBeanDefinitions(context).remove(identifier);
        this.destroyRequestScopedBean(bean, beanDefinition);
        return Optional.ofNullable(bean);
    }

    private <T> void destroyRequestScopedBean(@Nullable T bean, @Nullable BeanDefinition<T> beanDefinition) {
        if (bean != null && beanDefinition instanceof DisposableBeanDefinition) {
            try {
                ((DisposableBeanDefinition)beanDefinition).dispose(this.beanContext, bean);
            } catch (Exception var4) {
                logger.error("Error disposing of request scoped bean: " + bean, var4);
            }
        }

    }

    @NonNull
    public VerticleCustomScope stop() {
        Context context = beanContext.getVertx().getOrCreateContext();
        this.destroyBeans(context);
        return this;
    }

    public boolean isRunning() {
        return true;
    }

    private void destroyBeans(Context request) {
        ArgumentUtils.requireNonNull("request", request);
        Map<?, ?> beans = this.getRequestScopedBeans(request);
        Map<?, ?> beanDefinitions = this.getRequestScopedBeanDefinitions(request);
        Iterator var4 = beans.keySet().iterator();

        while(var4.hasNext()) {
            Object key = var4.next();
            if (key instanceof BeanIdentifier) {
                Object bean = beans.remove(key);
                Object beanDefinition = beanDefinitions.remove(key);
                if (beanDefinition instanceof BeanDefinition) {
                    this.destroyRequestScopedBean(bean, (BeanDefinition)beanDefinition);
                }
            }
        }

    }

    private synchronized <T> ConcurrentHashMap<?, ?> getRequestScopedBeans(Context httpRequest) {
        return this.getRequestAttributeMap(httpRequest, SCOPED_BEANS_ATTRIBUTE);
    }

    private synchronized <T> ConcurrentHashMap<?, ?> getRequestScopedBeanDefinitions(Context httpRequest) {
        return this.getRequestAttributeMap(httpRequest, SCOPED_BEAN_DEFINITIONS);
    }

    private synchronized <T> ConcurrentHashMap<?, ?> getRequestAttributeMap(Context httpRequest, String attribute) {
        ConcurrentHashMap local = httpRequest.get(attribute);
        return Optional.ofNullable(local).flatMap((o) -> {
            return o instanceof ConcurrentHashMap ? Optional.of((ConcurrentHashMap)o) : Optional.empty();
        }).orElseGet(() -> {
            ConcurrentHashMap scopedBeans = new ConcurrentHashMap(5);
            httpRequest.put(attribute, scopedBeans);
            return scopedBeans;
        });
    }

    @Override
    public void onApplicationEvent(VerticleTerminatedEvent event) {
        this.destroyBeans(event.getSource());
    }
}
