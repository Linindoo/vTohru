package cn.vtohru.context.env;

import io.micronaut.context.env.PropertySource;

import java.util.Iterator;

public class VBootstrapPropertySource implements PropertySource {
    private final PropertySource delegate;

    VBootstrapPropertySource(PropertySource bootstrapPropertySource) {
        this.delegate = bootstrapPropertySource;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public PropertyConvention getConvention() {
        return delegate.getConvention();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public Object get(String key) {
        return delegate.get(key);
    }

    @Override
    public Iterator<String> iterator() {
        return delegate.iterator();
    }

    @Override
    public int getOrder() {
        // lower priority than application property sources
        return delegate.getOrder() + 10;
    }
}
