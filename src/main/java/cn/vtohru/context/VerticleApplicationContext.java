package cn.vtohru.context;

import cn.vtohru.runtime.VTohru;
import io.micronaut.context.ApplicationContextConfiguration;
import io.micronaut.context.DefaultApplicationContext;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.inject.BeanDefinition;
import io.vertx.core.Context;
import io.vertx.core.Vertx;

import java.util.Arrays;

public class VerticleApplicationContext extends DefaultApplicationContext {
    private static final String SCOPE_PACKAGE = "VTOHRU_VERTICLE_SCOPE_PACKAGE";

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
        String[] packages = context.getLocal(SCOPE_PACKAGE);
        if (packages == null || packages.length == 0) {
            return true;
        }
        String packageName = beanDefinition.getBeanType().getPackage().getName();
        return Arrays.stream(packages).anyMatch(x -> x.equalsIgnoreCase(packageName));
    }


    public void savePackage(String[] packages) {
        Context context = vertx.getOrCreateContext();
        context.putLocal(SCOPE_PACKAGE, packages);
    }
}
