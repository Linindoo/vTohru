package cn.vtohru.context;

import cn.vtohru.annotation.VerticleContaner;
import cn.vtohru.runtime.VTohru;
import io.micronaut.context.ApplicationContextConfiguration;
import io.micronaut.context.DefaultApplicationContext;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.convert.ConversionContext;
import io.micronaut.core.type.Argument;
import io.micronaut.core.util.StringUtils;
import io.micronaut.inject.BeanDefinition;
import io.vertx.core.Context;
import io.vertx.core.Vertx;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

public class VerticleApplicationContext extends DefaultApplicationContext {
    public static final String SCOPE_PACKAGE = "VTOHRU_VERTICLE_SCOPE_PACKAGE";
    public static final String SCOPE_VERTICLE_NAME = "VTOHRU_SCOPE_VERTICLE_NAME";
    public static final String VTOHRU = "vtohru";
    private Vertx vertx;


    public VerticleApplicationContext(ApplicationContextConfiguration configuration) {
        super(configuration);
        this.vertx = Vertx.vertx();
    }


    @Override
    public synchronized VerticleApplicationContext start() {
        super.start();
        return this;
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
        Context context = vertx.getOrCreateContext();
        String[] packages = context.get(SCOPE_PACKAGE);
        if (packages == null || packages.length == 0) {
            return true;
        }
        String packageName = beanDefinition.getBeanType().getPackage().getName();
        return Arrays.stream(packages).anyMatch(x -> packageName.startsWith(x));
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

    public AbstractMap getScopeMap(BeanDefinition<?> beanDefinition) {
        String verticleName = this.getVerticleName(beanDefinition);
        String verticleConfigKey = "vtohru." + verticleName.toLowerCase();
        return getEnvironment().get(verticleConfigKey, AbstractMap.class).orElse(new HashMap<>());
    }

    public <T> Optional<T> getVerticleEnv(String name, Class<T> requiredType) {
        String scopeKey = VTOHRU + "." + name;
        if (getEnvironment().containsProperties(scopeKey)) {
            return getEnvironment().get(scopeKey, ConversionContext.of(Argument.of(requiredType)));
        }
        scopeKey = VTOHRU + "." + getScopeName().toLowerCase() + "." + name;
        return get(scopeKey, ConversionContext.of(Argument.of(requiredType)));
    }
}
