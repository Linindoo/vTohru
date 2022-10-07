package cn.vtohru.plugin;

import cn.vtohru.context.VerticleApplicationContext;
import io.micronaut.context.ApplicationContextConfiguration;
import io.micronaut.context.DefaultApplicationContextBuilder;
import io.micronaut.context.Qualifier;
import io.micronaut.core.type.Argument;
import io.micronaut.inject.ExecutableMethod;
import io.micronaut.inject.MethodExecutionHandle;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

public class PluginApplicationContext extends VerticleApplicationContext {
    private VerticleApplicationContext rootApplicationContext;

    public PluginApplicationContext(ApplicationContextConfiguration configuration, VerticleApplicationContext rootApplicationContext) {
        super(configuration);
        this.rootApplicationContext = rootApplicationContext;
    }

    public VerticleApplicationContext getRootApplicationContext() {
        return rootApplicationContext;
    }

    public void setRootApplicationContext(VerticleApplicationContext rootApplicationContext) {
        this.rootApplicationContext = rootApplicationContext;
    }

    @Override
    public <T> T getBean(Argument<T> beanType) {
        if (beanType.getType().getClassLoader().equals(getClassLoader())) {
            return super.getBean(beanType);
        } else {
            return rootApplicationContext.getBean(beanType);
        }
    }

    @Override
    public <T> T getBean(Class<T> beanType, Qualifier<T> qualifier) {
        if (beanType.getClassLoader().equals(getClassLoader())) {
            return super.getBean(beanType,qualifier);
        } else {
            return rootApplicationContext.getBean(beanType, qualifier);
        }
    }

    @Override
    public <T> T getBean(Class<T> beanType) {
        if (beanType.getClassLoader().equals(getClassLoader())) {
            return super.getBean(beanType);
        } else {
            return rootApplicationContext.getBean(beanType);
        }
    }

    @Override
    public <T> Collection<T> getBeansOfType(Class<T> beanType) {
        if (beanType.getClassLoader().equals(getClassLoader())) {
            return super.getBeansOfType(beanType);
        } else {
            return rootApplicationContext.getBeansOfType(beanType);
        }
    }

    @Override
    public <T> Collection<T> getBeansOfType(Class<T> beanType, Qualifier<T> qualifier) {
        if (beanType.getClassLoader().equals(getClassLoader())) {
            return super.getBeansOfType(beanType, qualifier);
        } else {
            return rootApplicationContext.getBeansOfType(beanType, qualifier);
        }
    }

    @Override
    public <T> Collection<T> getBeansOfType(Argument<T> beanType) {
        if (beanType.getType().getClassLoader().equals(getClassLoader())) {
            return super.getBeansOfType(beanType);
        } else {
            return rootApplicationContext.getBeansOfType(beanType);
        }
    }

    @Override
    public <T> Collection<T> getBeansOfType(Argument<T> beanType, Qualifier<T> qualifier) {
        if (beanType.getType().getClassLoader().equals(getClassLoader())) {
            return super.getBeansOfType(beanType,qualifier);
        } else {
            return rootApplicationContext.getBeansOfType(beanType, qualifier);
        }
    }

    @Override
    public <T> Optional<T> findBean(Argument<T> beanType) {
        if (beanType.getType().getClassLoader().equals(getClassLoader())) {
            return super.findBean(beanType);
        } else {
            return rootApplicationContext.findBean(beanType);
        }
    }

    @Override
    public <T> Stream<T> streamOfType(Argument<T> beanType) {
        if (beanType.getType().getClassLoader().equals(getClassLoader())) {
            return super.streamOfType(beanType);
        } else {
            return rootApplicationContext.streamOfType(beanType);
        }
    }

    @Override
    public <T> Stream<T> streamOfType(Class<T> beanType) {
        if (beanType.getClassLoader().equals(getClassLoader())) {
            return super.streamOfType(beanType);
        } else {
            return rootApplicationContext.streamOfType(beanType);
        }
    }

    @Override
    public <T> Optional<T> findBean(Class<T> beanType) {
        if (beanType.getClassLoader().equals(getClassLoader())) {
            return super.findBean(beanType);
        } else {
            return rootApplicationContext.findBean(beanType);
        }
    }

    @Override
    public <T> Optional<T> findOrInstantiateBean(Class<T> beanType) {
        if (beanType.getClassLoader().equals(getClassLoader())) {
            return super.findOrInstantiateBean(beanType);
        } else {
            return rootApplicationContext.findOrInstantiateBean(beanType);
        }
    }

    @Override
    public <T, R> ExecutableMethod<T, R> getExecutableMethod(Class<T> beanType, String method, Class... arguments) throws NoSuchMethodException {
        if (beanType.getClassLoader().equals(getClassLoader())) {
            return super.getExecutableMethod(beanType, method, arguments);
        } else {
            return rootApplicationContext.getExecutableMethod(beanType, method, arguments);
        }
    }

    @Override
    public <T, R> ExecutableMethod<T, R> getProxyTargetMethod(Class<T> beanType, String method, Class... arguments) throws NoSuchMethodException {
        if (beanType.getClassLoader().equals(getClassLoader())) {
            return super.getProxyTargetMethod(beanType, method, arguments);
        } else {
            return rootApplicationContext.getProxyTargetMethod(beanType, method, arguments);
        }
    }

    @Override
    public <T, R> ExecutableMethod<T, R> getProxyTargetMethod(Class<T> beanType, Qualifier<T> qualifier, String method, Class... arguments) throws NoSuchMethodException {
        if (beanType.getClassLoader().equals(getClassLoader())) {
            return super.getProxyTargetMethod(beanType, qualifier, method, arguments);
        } else {
            return rootApplicationContext.getProxyTargetMethod(beanType, qualifier, method, arguments);
        }
    }

    @Override
    public <T, R> ExecutableMethod<T, R> getProxyTargetMethod(Argument<T> beanType, Qualifier<T> qualifier, String method, Class... arguments) throws NoSuchMethodException {
        if (beanType.getType().getClassLoader().equals(getClassLoader())) {
            return super.getProxyTargetMethod(beanType, qualifier, method, arguments);
        } else {
            return rootApplicationContext.getProxyTargetMethod(beanType, qualifier, method, arguments);
        }
    }

    @Override
    public <T, R> MethodExecutionHandle<T, R> getExecutionHandle(Class<T> beanType, String method, Class... arguments) throws NoSuchMethodException {
        if (beanType.getClassLoader().equals(getClassLoader())) {
            return super.getExecutionHandle(beanType, method, arguments);
        } else {
            return rootApplicationContext.getExecutionHandle(beanType, method, arguments);
        }
    }

    @Override
    public <T, R> MethodExecutionHandle<T, R> getExecutionHandle(T bean, String method, Class... arguments) throws NoSuchMethodException {
        if (bean.getClass().getClassLoader().equals(getClassLoader())) {
            return super.getExecutionHandle(bean, method, arguments);
        } else {
            return rootApplicationContext.getExecutionHandle(bean, method, arguments);
        }
    }
}
