package cn.vtohru.context.env;

import io.micronaut.context.ApplicationContextConfiguration;
import io.micronaut.context.DefaultApplicationContext;
import io.micronaut.context.annotation.BootstrapContextCompatible;
import io.micronaut.context.env.Environment;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import io.micronaut.inject.BeanConfiguration;
import io.micronaut.inject.BeanDefinitionReference;

import java.util.ArrayList;
import java.util.List;

public class VBootstrapApplicationContext extends DefaultApplicationContext {
    private final VBootstrapEnvironment bootstrapEnvironment;

    public VBootstrapApplicationContext(ClassPathResourceLoader resourceLoader, VBootstrapEnvironment bootstrapEnvironment, String... activeEnvironments) {
        super(resourceLoader, activeEnvironments);
        this.bootstrapEnvironment = bootstrapEnvironment;
    }

    @Override
    public @NonNull
    Environment getEnvironment() {
        return bootstrapEnvironment;
    }

    @NonNull
    @Override
    protected VBootstrapEnvironment createEnvironment(@NonNull ApplicationContextConfiguration configuration) {
        return bootstrapEnvironment;
    }

    @Override
    protected @NonNull
    List<BeanDefinitionReference> resolveBeanDefinitionReferences() {
        List<BeanDefinitionReference> refs = super.resolveBeanDefinitionReferences();
        List<BeanDefinitionReference> beanDefinitionReferences = new ArrayList<>(100);
        for (BeanDefinitionReference reference : refs) {
            if (reference.isAnnotationPresent(BootstrapContextCompatible.class)) {
                beanDefinitionReferences.add(reference);
            }
        }
        return beanDefinitionReferences;
    }

    @Override
    protected @NonNull Iterable<BeanConfiguration> resolveBeanConfigurations() {
        return super.resolveBeanConfigurations();
    }

    @Override
    protected void startEnvironment() {
        registerSingleton(Environment.class, bootstrapEnvironment);
    }

    @Override
    protected void initializeEventListeners() {
        // no-op .. Bootstrap context disallows bean event listeners
    }

    @Override
    protected void initializeContext(List<BeanDefinitionReference> contextScopeBeans, List<BeanDefinitionReference> processedBeans, List<BeanDefinitionReference> parallelBeans) {
        // no-op .. @Context scope beans are not started for bootstrap
    }

    @Override
    protected void processParallelBeans(List<BeanDefinitionReference> parallelBeans) {
        // no-op
    }

    @Override
    public void publishEvent(@NonNull Object event) {
        // no-op .. the bootstrap context shouldn't publish events
    }

}