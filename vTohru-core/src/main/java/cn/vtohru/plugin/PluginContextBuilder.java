package cn.vtohru.plugin;

import cn.vtohru.context.VerticleApplicationContext;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.DefaultApplicationContextBuilder;
import io.micronaut.core.annotation.NonNull;

public class PluginContextBuilder extends DefaultApplicationContextBuilder {


    public VerticleApplicationContext getRootApplicationContext() {
        return rootApplicationContext;
    }

    public void setRootApplicationContext(VerticleApplicationContext rootApplicationContext) {
        this.rootApplicationContext = rootApplicationContext;
    }

    private VerticleApplicationContext rootApplicationContext;

    public PluginContextBuilder(VerticleApplicationContext rootApplicationContext) {

        this.rootApplicationContext = rootApplicationContext;
    }

    protected ApplicationContext newApplicationContext() {
        return new PluginApplicationContext(this, rootApplicationContext);
    }

    @Override
    public @NonNull
    PluginApplicationContext build() {
        ApplicationContext applicationContext = super.build();
        return (PluginApplicationContext) applicationContext;
    }

    @Override
    public @NonNull PluginContextBuilder classLoader(ClassLoader classLoader) {
        return (PluginContextBuilder)super.classLoader(classLoader);
    }
}
