package cn.vtohru.plugin;

import cn.vtohru.context.VerticleApplicationContext;
import io.micronaut.context.BeanContextConfiguration;
import io.micronaut.context.DefaultBeanContext;
import org.pf4j.Plugin;
import org.pf4j.PluginFactory;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

public class VerticlePluginFactory implements PluginFactory {
    private static final Logger log = LoggerFactory.getLogger(VerticlePluginFactory.class);
    private VerticleApplicationContext applicationContext;

    public VerticlePluginFactory(VerticleApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Creates a plugin instance. If an error occurs than that error is logged and the method returns null.
     * @param pluginWrapper
     * @return
     */
    @Override
    public Plugin create(final PluginWrapper pluginWrapper) {
        String pluginClassName = pluginWrapper.getDescriptor().getPluginClass();
        log.debug("Create instance for plugin '{}'", pluginClassName);

        Class<?> pluginClass;
        try {
            pluginClass = pluginWrapper.getPluginClassLoader().loadClass(pluginClassName);
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            return null;
        }

        // once we have the class, we can do some checks on it to ensure
        // that it is a valid implementation of a plugin.
        int modifiers = pluginClass.getModifiers();
        if (Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers)
                || (!Plugin.class.isAssignableFrom(pluginClass))) {
            log.error("The plugin class '{}' is not valid", pluginClassName);
            return null;
        }

        // create the plugin instance
        try {
            Constructor<?> constructor = pluginClass.getConstructor(PluginWrapper.class,PluginApplicationContext.class);
            PluginApplicationContext pluginApplicationContext = new PluginContextBuilder(applicationContext).classLoader(pluginWrapper.getPluginClassLoader()).build();
            applicationContext.addPluginContext(pluginWrapper.getPluginId(), pluginApplicationContext);
            return (Plugin) constructor.newInstance(pluginWrapper, pluginApplicationContext);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }
}
