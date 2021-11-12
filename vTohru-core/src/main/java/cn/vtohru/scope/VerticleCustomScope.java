package cn.vtohru.scope;

import cn.vtohru.annotation.Verticle;
import cn.vtohru.context.VerticleApplicationContext;
import cn.vtohru.event.VerticleTerminatedEvent;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.LifeCycle;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.exceptions.BeanDestructionException;
import io.micronaut.context.scope.BeanCreationContext;
import io.micronaut.context.scope.CreatedBean;
import io.micronaut.context.scope.CustomScope;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.inject.BeanIdentifier;
import io.vertx.core.Context;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Singleton
public class VerticleCustomScope implements CustomScope<Verticle>, LifeCycle<VerticleCustomScope>, ApplicationEventListener<VerticleTerminatedEvent> {
    private static final Logger logger = LoggerFactory.getLogger(VerticleCustomScope.class);
    public static final String SCOPED_BEANS_ATTRIBUTE = "CN.VTOHRU.SCOPED_BEANS";
    private VerticleApplicationContext beanContext;

    public VerticleCustomScope(ApplicationContext beanContext) {
        this.beanContext = (VerticleApplicationContext) beanContext;
    }

    protected <T> CreatedBean<T> doCreate(@NonNull BeanCreationContext<T> creationContext) {
        CreatedBean<T> createdBean = creationContext.create();
        return createdBean;
    }

    @Override
    public Class<Verticle> annotationType() {
        return Verticle.class;
    }

    @Override
    public <T> T getOrCreate(BeanCreationContext<T> creationContext) {
        if (!beanContext.isScoped(creationContext.definition())) {
            return null;
        }
        final Map<BeanIdentifier, CreatedBean<?>> scopeMap = getScopeMap(true);
        final BeanIdentifier id = creationContext.id();
        CreatedBean<?> createdBean = scopeMap.get(id);
        if (createdBean != null) {
            return (T) createdBean.bean();
        } else {
            createdBean = scopeMap.get(id);
            if (createdBean != null) {
                return (T) createdBean.bean();
            } else {
                createdBean = doCreate(creationContext);
                scopeMap.put(id, createdBean);
                return (T) createdBean.bean();
            }
        }
    }

    @Override
    public <T> Optional<T> remove(BeanIdentifier identifier) {
        if (identifier == null) {
            return Optional.empty();
        }
        Map<BeanIdentifier, CreatedBean<?>> scopeMap;
        try {
            scopeMap = getScopeMap(false);
        } catch (IllegalStateException e) {
            return Optional.empty();
        }
        if (CollectionUtils.isNotEmpty(scopeMap)) {
            final CreatedBean<?> createdBean = scopeMap.get(identifier);
            if (createdBean != null) {
                try {
                    createdBean.close();
                } catch (BeanDestructionException e) {
                    handleDestructionException(e);
                }
                return (Optional<T>) Optional.ofNullable(createdBean.bean());
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }
    private void handleDestructionException(BeanDestructionException e) {
        logger.error("Error occurred destroying bean of scope @" + this.annotationType().getSimpleName() + ": " + e.getMessage(), e);
    }


    protected Map<BeanIdentifier, CreatedBean<?>> getScopeMap(boolean forCreation) {
        Context context = beanContext.getVertx().getOrCreateContext();
        return getRequestScopedBeans(context, forCreation);
    }
    @Override
    public VerticleCustomScope stop() {
        Context context = beanContext.getVertx().getOrCreateContext();
        this.destroyBeans(context);
        return this;
    }

    public boolean isRunning() {
        return true;
    }

    private void destroyBeans(Context context) {
        Map<BeanIdentifier, CreatedBean<?>> requestScopedBeans =
                getRequestScopedBeans(context, false);
        if (requestScopedBeans != null) {
            destroyScope(requestScopedBeans);
        }
    }
    protected void destroyScope(@Nullable Map<BeanIdentifier, CreatedBean<?>> scopeMap) {
        if (CollectionUtils.isNotEmpty(scopeMap)) {
            for (CreatedBean<?> createdBean : scopeMap.values()) {
                    createdBean.close();
            }
            scopeMap.clear();
        }
    }

    private synchronized <T> Map<BeanIdentifier, CreatedBean<?>> getRequestScopedBeans(Context context, boolean create) {
        return this.getRequestAttributeMap(context, SCOPED_BEANS_ATTRIBUTE, create);
    }
    private <T> Map<BeanIdentifier, CreatedBean<?>> getRequestAttributeMap(Context context, String attribute, boolean create) {
        HashMap<BeanIdentifier, CreatedBean<?>> local = context.get(attribute);
        if (local != null) {
            return local;
        }
        if (create) {
            Map<BeanIdentifier, CreatedBean<?>> scopedBeans = new HashMap<>(5);
            context.put(attribute, scopedBeans);
            return scopedBeans;
        }
        return null;
    }


    @Override
    public void onApplicationEvent(VerticleTerminatedEvent event) {
        this.destroyBeans(event.getSource());
    }

}
