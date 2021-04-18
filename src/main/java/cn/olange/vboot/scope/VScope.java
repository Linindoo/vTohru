package cn.olange.vboot.scope;

import cn.olange.vboot.annotation.Verticle;
import cn.olange.vboot.context.VerticleContext;
import cn.olange.vboot.event.VerticleTerminatedEvent;
import io.micronaut.context.BeanContext;
import io.micronaut.context.BeanResolutionContext;
import io.micronaut.context.LifeCycle;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.exceptions.NoSuchBeanException;
import io.micronaut.context.scope.CustomScope;
import io.micronaut.core.annotation.AnnotationClassValue;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.BeanIdentifier;
import io.micronaut.inject.DisposableBeanDefinition;
import io.micronaut.inject.qualifiers.Qualifiers;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class VScope implements CustomScope<Verticle>, LifeCycle<VScope>, ApplicationEventListener<VerticleTerminatedEvent> {
    private static final Logger logger = LoggerFactory.getLogger(VScope.class);
    private static final ConcurrentHashMap<BeanIdentifier, BeanDefinition> beanDefinitionConcurrentHashMap = new ConcurrentHashMap();
    public static final String SCOPED_BEANS_ATTRIBUTE = "cn.olange.vboot.SCOPED_BEANS";
    private BeanContext beanContext;

    public VScope(BeanContext beanContext) {
        this.beanContext = beanContext;
    }

    @Override
    public Class<Verticle> annotationType() {
        return Verticle.class;
    }

    @Override
    public <T> Optional<T> remove(BeanIdentifier identifier) {
        BeanDefinition<T> beanDefinition = beanDefinitionConcurrentHashMap.get(identifier);
        AnnotationValue<Verticle> annotation = beanDefinition.getAnnotation(Verticle.class);
        Class vertxValue = annotation.getValue(Verticle.class).get().bind();
        Optional<Vertx> curentVertx = VerticleContext.get(vertxValue.getName());
        if (curentVertx.isPresent()) {
            T bean = null;
            Map scopedBeanMap = curentVertx.get().getOrCreateContext().getLocal(SCOPED_BEANS_ATTRIBUTE);
            if (scopedBeanMap != null) {
                bean = (T)scopedBeanMap.remove(identifier);
                destroyRequestScopedBean(bean, beanDefinition);
            }
            return Optional.ofNullable(bean);
        } else {
            return Optional.empty();
        }
    }

    private <T> void destroyRequestScopedBean(@Nullable T bean, @Nullable BeanDefinition<T> beanDefinition) {
        if (bean != null && beanDefinition instanceof DisposableBeanDefinition) {
            try {
                ((DisposableBeanDefinition<T>) beanDefinition).dispose(
                        beanContext, bean
                );
            } catch (Exception e) {
                logger.error("Error disposing of request scoped bean: " + bean, e);
            }
        }
    }

    @Override
    public <T> T get(BeanResolutionContext resolutionContext, BeanDefinition<T> beanDefinition,
                 BeanIdentifier identifier, Provider<T> provider) {
        AnnotationValue<Verticle> annotation = beanDefinition.getAnnotation(Verticle.class);
        Optional<AnnotationClassValue<?>> annotationClassValue = annotation.annotationClassValue("value");
        if (annotationClassValue.isEmpty()) {
            throw new NoSuchBeanException(beanDefinition.getBeanType(), Qualifiers.byStereotype(Verticle.class));
        }
        Optional<Vertx> curentVertx = VerticleContext.get(annotationClassValue.get().getName());
        Vertx vertx;
        if (curentVertx.isEmpty()) {
            vertx = Vertx.vertx();
            VerticleContext.set(annotationClassValue.get().getName(), vertx);
        } else {
            vertx = curentVertx.get();
        }
        Map scopedBeanMap = vertx.getOrCreateContext().getLocal(SCOPED_BEANS_ATTRIBUTE);
        if (scopedBeanMap == null) {
            scopedBeanMap = new HashMap();
            vertx.getOrCreateContext().putLocal(SCOPED_BEANS_ATTRIBUTE, scopedBeanMap);
        }
        T bean = (T) scopedBeanMap.get(identifier);
        if (bean == null) {
            synchronized (this) { // double check
                bean = (T) scopedBeanMap.get(identifier);
                if (bean == null) {
                    bean = provider.get();
                    scopedBeanMap.put(identifier, bean);
                    beanDefinitionConcurrentHashMap.put(identifier, beanDefinition);
                }
            }
        }
        return bean;
    }

    @Override
    public boolean isRunning() {
        return true;
    }

    @Override
    public void onApplicationEvent(VerticleTerminatedEvent event) {
        destroyBeans(event.getSource());

    }
    private void destroyBeans(Verticle verticle) {
        Class vertxValue = verticle.bind();

        Optional<Vertx> curentVertx = VerticleContext.get(vertxValue.getName());
        if (curentVertx.isPresent()) {
            Map scopedBeanMap = curentVertx.get().getOrCreateContext().getLocal(SCOPED_BEANS_ATTRIBUTE);
            for (Object key: scopedBeanMap.keySet()) {
                if (key instanceof BeanIdentifier) {
                    Object bean = scopedBeanMap.remove(key);
                    BeanDefinition beanDefinition = beanDefinitionConcurrentHashMap.remove(key);
                    destroyRequestScopedBean(bean, beanDefinition);
                }
            }
        }

    }

}
