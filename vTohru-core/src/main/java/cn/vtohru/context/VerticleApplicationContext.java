package cn.vtohru.context;

import cn.vtohru.annotation.GlobalScope;
import cn.vtohru.annotation.VerticleContaner;
import cn.vtohru.context.env.VBootstrapApplicationContext;
import cn.vtohru.context.env.VBootstrapEnvironment;
import cn.vtohru.context.env.VDefaultEnvironment;
import cn.vtohru.runtime.VTohru;
import io.micronaut.aop.InterceptedProxy;
import io.micronaut.context.*;
import io.micronaut.context.env.BootstrapPropertySourceLocator;
import io.micronaut.context.env.Environment;
import io.micronaut.context.env.PropertySource;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.convert.ConversionContext;
import io.micronaut.core.convert.ConversionService;
import io.micronaut.core.convert.TypeConverter;
import io.micronaut.core.convert.TypeConverterRegistrar;
import io.micronaut.core.type.Argument;
import io.micronaut.core.util.StringUtils;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.BeanDefinitionReference;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class VerticleApplicationContext extends DefaultApplicationContext {
    private Environment environment;
    private static final Logger logger = LoggerFactory.getLogger(VerticleApplicationContext.class);
    public static final String SCOPE_PACKAGE = "VTOHRU_VERTICLE_SCOPE_PACKAGE";
    public static final String SCOPE_VERTICLE_NAME = "VTOHRU_SCOPE_VERTICLE_NAME";
    public static final String VTOHRU = "vtohru";
    private Vertx vertx;
    public VerticleApplicationContext(ApplicationContextConfiguration configuration) {
        super(configuration);
    }


    protected @NonNull Environment createEnvironment(@NonNull ApplicationContextConfiguration configuration) {
        return new VRuntimeConfiguredEnvironment(configuration, isBootstrapEnabled(configuration));
    }

    private boolean isBootstrapEnabled(ApplicationContextConfiguration configuration) {
        String bootstrapContextProp = System.getProperty(Environment.BOOTSTRAP_CONTEXT_PROPERTY);
        if (bootstrapContextProp != null) {
            return Boolean.parseBoolean(bootstrapContextProp);
        }
        Boolean configBootstrapEnabled = configuration.isBootstrapEnvironmentEnabled();
        if (configBootstrapEnabled != null) {
            return configBootstrapEnabled;
        }
        return isBootstrapPropertySourceLocatorPresent();
    }
    private boolean isBootstrapPropertySourceLocatorPresent() {
        for (BeanDefinitionReference beanDefinitionReference : resolveBeanDefinitionReferences()) {
            if (BootstrapPropertySourceLocator.class.isAssignableFrom(beanDefinitionReference.getBeanType())) {
                return true;
            }
        }
        return false;
    }


    /**
     * Creates the default conversion service.
     *
     * @return The conversion service
     */
    protected @NonNull ConversionService createConversionService() {
        return ConversionService.SHARED;
    }


    @Override
    public @NonNull Environment getEnvironment() {
        if (environment == null) {
            environment = createEnvironment((ApplicationContextConfiguration) getContextConfiguration());
        }
        return environment;
    }

    @Override
    public synchronized @NonNull ApplicationContext start() {
        return super.start();
    }

    @Override
    public synchronized @NonNull ApplicationContext stop() {
        return super.stop();
    }

    @Override
    protected void initializeContext(List<BeanDefinitionReference> contextScopeBeans, List<BeanDefinitionReference> processedBeans, List<BeanDefinitionReference> parallelBeans) {
        initializeTypeConverters(this);
        super.initializeContext(contextScopeBeans, processedBeans, parallelBeans);
    }


    /**
     * @param beanContext The bean context
     */
    protected void initializeTypeConverters(BeanContext beanContext) {
        Collection<BeanRegistration<TypeConverter>> typeConverters = beanContext.getBeanRegistrations(TypeConverter.class);
        for (BeanRegistration<TypeConverter> typeConverterRegistration : typeConverters) {
            TypeConverter typeConverter = typeConverterRegistration.getBean();
            List<Argument<?>> typeArguments = typeConverterRegistration.getBeanDefinition().getTypeArguments(TypeConverter.class);
            if (typeArguments.size() == 2) {
                Class source = typeArguments.get(0).getType();
                Class target = typeArguments.get(1).getType();
                if (source != null && target != null && !(source == Object.class && target == Object.class)) {
                    getConversionService().addConverter(source, target, typeConverter);
                }
            }
        }
        Collection<TypeConverterRegistrar> registrars = beanContext.getBeansOfType(TypeConverterRegistrar.class);
        for (TypeConverterRegistrar registrar : registrars) {
            registrar.register(getConversionService());
        }
    }


    public Vertx getVertx() {
        return vertx;
    }

    public void setVertx(Vertx vertx) {
        this.vertx = vertx;
    }


    @NonNull
    public static VerticleApplicationContext run() {
        return new VTohru().start();
    }


    public boolean isScoped(BeanDefinition<?> beanDefinition) {
        if (beanDefinition.hasAnnotation(GlobalScope.class)) {
            return true;
        }
        Context context = vertx.getOrCreateContext();
        String[] packages = context.get(SCOPE_PACKAGE);
        if (packages == null || packages.length == 0) {
            return true;
        }
        String packageName = beanDefinition.getBeanType().getPackage().getName();
        return Arrays.stream(packages).anyMatch(packageName::startsWith);
    }


    private void savePackage(String[] packages) {
        Context context = vertx.getOrCreateContext();
        context.put(SCOPE_PACKAGE, packages);
    }

    public String getScopeName() {
        Context context = vertx.getOrCreateContext();
        return context.get(SCOPE_VERTICLE_NAME);
    }

    private void setVerticleName(String verticleName) {
        Context context = vertx.getOrCreateContext();
        context.put(SCOPE_VERTICLE_NAME, verticleName);
    }

    public void saveVerticleInfo(BeanDefinition<?> beanDefinition) {
        AnnotationValue<VerticleContaner> annotation = beanDefinition.getAnnotation(VerticleContaner.class);
        String[] packages = annotation.get("usePackage", String[].class).orElse(new String[]{});
        savePackage(packages);
        String verticleName = getVerticleName(beanDefinition);
        setVerticleName(verticleName);
    }
    public String getVerticleName(BeanDefinition<?> beanDefinition) {
        AnnotationValue<VerticleContaner> annotation = beanDefinition.getAnnotation(VerticleContaner.class);
        String verticleName = "";
        if (annotation != null) {
            verticleName = annotation.stringValue().orElse("");
        }
        if (StringUtils.isEmpty(verticleName)) {
            String rawClassName = getRawClassName(beanDefinition.getName());
            verticleName = rawClassName.substring(rawClassName.lastIndexOf(".") +1);
        }
        return verticleName;
    }

    private String getRawClassName(String className) {
        return className.replace("Definition$Intercepted", "").replace("$", "");
    }

    public JsonObject getVConfig(BeanDefinition<?> beanDefinition) {
        String verticleName = this.getVerticleName(beanDefinition);
        String verticleConfigKey = "vtohru." + verticleName.toLowerCase();
        return getEnvironment().get(verticleConfigKey, JsonObject.class).orElse(new JsonObject());
    }

    public <T> Optional<T> getVProperty(String name, Class<T> requiredType) {
        String scopeName = getScopeName();
        String scopeKey = VTOHRU + "." + name;
        if (StringUtils.isEmpty(scopeName) || getEnvironment().containsProperties(scopeKey)) {
            return getEnvironment().get(scopeKey, ConversionContext.of(Argument.of(requiredType)));
        }
        scopeKey = VTOHRU + "." + scopeName.toLowerCase() + "." + name;
        return  getEnvironment().get(scopeKey, ConversionContext.of(Argument.of(requiredType)));
    }

    public boolean isNull(Object bean) {
        if (bean == null) {
            return true;
        }
        if (bean instanceof InterceptedProxy) {
            InterceptedProxy proxy = (InterceptedProxy) bean;
            Object target = proxy.interceptedTarget();
            return target == null;
        }
        return false;
    }

    private class VRuntimeConfiguredEnvironment extends VDefaultEnvironment {
        protected final Logger LOG = LoggerFactory.getLogger(VRuntimeConfiguredEnvironment.class);
        private final ApplicationContextConfiguration configuration;
        private BootstrapPropertySourceLocator bootstrapPropertySourceLocator;
        private VBootstrapEnvironment bootstrapEnvironment;
        private final boolean bootstrapEnabled;

        public VRuntimeConfiguredEnvironment(ApplicationContextConfiguration configuration, boolean bootstrapEnabled) {
            super(configuration);
            this.configuration = configuration;
            this.bootstrapEnabled = bootstrapEnabled;
        }

        boolean isRuntimeConfigured() {
            return bootstrapEnabled;
        }

        @Override
        public Environment stop() {
            if (bootstrapEnvironment != null) {
                bootstrapEnvironment.stop();
            }
            return super.stop();
        }

        @Override
        public Environment start() {
            if (bootstrapEnvironment == null && isRuntimeConfigured()) {
                bootstrapEnvironment = createBootstrapEnvironment(getActiveNames().toArray(new String[0]));
                startBootstrapEnvironment();
            }
            return super.start();
        }

        @Override
        protected synchronized List<PropertySource> readPropertySourceList(String name) {

            if (bootstrapEnvironment != null) {
                LOG.info("Reading bootstrap environment configuration");

                refreshablePropertySources.addAll(bootstrapEnvironment.getRefreshablePropertySources());

                String[] environmentNamesArray = getActiveNames().toArray(new String[0]);
                BootstrapPropertySourceLocator bootstrapPropertySourceLocator = resolveBootstrapPropertySourceLocator(environmentNamesArray);

                for (PropertySource propertySource : bootstrapPropertySourceLocator.findPropertySources(bootstrapEnvironment)) {
                    addPropertySource(propertySource);
                    refreshablePropertySources.add(propertySource);
                }

                Collection<PropertySource> bootstrapPropertySources = bootstrapEnvironment.getPropertySources();
                for (PropertySource bootstrapPropertySource : bootstrapPropertySources) {
                    addPropertySource(bootstrapPropertySource);
                }

            }
            return super.readPropertySourceList(name);
        }

        private BootstrapPropertySourceLocator resolveBootstrapPropertySourceLocator(String... environmentNames) {
            if (this.bootstrapPropertySourceLocator == null) {
                VBootstrapApplicationContext bootstrapContext = new VBootstrapApplicationContext(resourceLoader, bootstrapEnvironment, environmentNames);
                bootstrapContext.start();
                if (bootstrapContext.containsBean(BootstrapPropertySourceLocator.class)) {
                    initializeTypeConverters(bootstrapContext);
                    bootstrapPropertySourceLocator = bootstrapContext.getBean(BootstrapPropertySourceLocator.class);
                } else {
                    bootstrapPropertySourceLocator = BootstrapPropertySourceLocator.EMPTY_LOCATOR;
                }
            }
            return this.bootstrapPropertySourceLocator;
        }

        private VBootstrapEnvironment createBootstrapEnvironment(String... environmentNames) {
            return new VBootstrapEnvironment(
                    resourceLoader,
                    conversionService,
                    configuration,
                    environmentNames);
        }

        private void startBootstrapEnvironment() {
            for (PropertySource source : propertySources.values()) {
                bootstrapEnvironment.addPropertySource(source);
            }
            bootstrapEnvironment.start();
            for (String pkg : bootstrapEnvironment.getPackages()) {
                addPackage(pkg);
            }
        }
    }
}
