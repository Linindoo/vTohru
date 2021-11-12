package cn.vtohru.context.env;

import io.micronaut.context.ApplicationContextConfiguration;
import io.micronaut.context.env.PropertySource;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.convert.ConversionService;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import io.micronaut.core.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class VBootstrapEnvironment extends VDefaultEnvironment {

    private List<PropertySource> propertySourceList;

    public VBootstrapEnvironment(ClassPathResourceLoader resourceLoader, ConversionService conversionService, ApplicationContextConfiguration configuration, String... activeEnvironments) {
        super(new ApplicationContextConfiguration() {
            @Override
            public Optional<Boolean> getDeduceEnvironments() {
                return Optional.of(false);
            }

            @NonNull
            @Override
            public ClassLoader getClassLoader() {
                return resourceLoader.getClassLoader();
            }

            @NonNull
            @Override
            public List<String> getEnvironments() {
                return Arrays.asList(activeEnvironments);
            }

            @Override
            public boolean isEnvironmentPropertySource() {
                return configuration.isEnvironmentPropertySource();
            }

            @Nullable
            @Override
            public List<String> getEnvironmentVariableIncludes() {
                return configuration.getEnvironmentVariableIncludes();
            }

            @Nullable
            @Override
            public List<String> getEnvironmentVariableExcludes() {
                return configuration.getEnvironmentVariableExcludes();
            }

            @NonNull
            @Override
            public ConversionService<?> getConversionService() {
                return conversionService;
            }

            @NonNull
            @Override
            public ClassPathResourceLoader getResourceLoader() {
                return resourceLoader;
            }
        });
    }

    @Override
    protected String getPropertySourceRootName() {
        String bootstrapName = System.getProperty(BOOTSTRAP_NAME_PROPERTY);
        return StringUtils.isNotEmpty(bootstrapName) ? bootstrapName : BOOTSTRAP_NAME;
    }

    @Override
    protected boolean shouldDeduceEnvironments() {
        return false;
    }

    /**
     * @return The refreshable property sources
     */
    public List<PropertySource> getRefreshablePropertySources() {
        return refreshablePropertySources;
    }

    @Override
    protected List<PropertySource> readPropertySourceList(String name) {
        if (propertySourceList == null) {
            propertySourceList = super.readPropertySourceList(name)
                    .stream()
                    .map(VBootstrapPropertySource::new)
                    .collect(Collectors.toList());
        }
        return propertySourceList;
    }
}