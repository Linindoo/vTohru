package cn.vtohru.plugin;

import cn.vtohru.context.VerticleApplicationContext;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import org.pf4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Decebal Suiu
 */
public class VTohruPluginManager extends DefaultPluginManager {
    private static final Logger log = LoggerFactory.getLogger(VTohruPluginManager.class);

    private VerticleApplicationContext applicationContext;

    public VTohruPluginManager(VerticleApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        initialize();
    }

    public VTohruPluginManager(Path... pluginsRoots) {
        super(pluginsRoots);
    }

    public VTohruPluginManager(List<Path> pluginsRoots) {
        super(pluginsRoots);
    }

    @Override
    protected ExtensionFactory createExtensionFactory() {
        return new VTohruExtensionFactory(this);
    }

    public void setApplicationContext(VerticleApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public VerticleApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * This method load, start plugins and inject extensions in Spring
     */
    public void init() {
        loadPlugins();
        startPlugins();
        ExtensionsInjector extensionsInjector = new ExtensionsInjector(this);
        extensionsInjector.injectExtensions();
    }

    @Override
    public void startPlugins() {
        for (PluginWrapper pluginWrapper : resolvedPlugins) {
            PluginState pluginState = pluginWrapper.getPluginState();
            if ((PluginState.DISABLED != pluginState) && (PluginState.STARTED != pluginState)) {
                try {
                    log.info("Start plugin '{}'", getPluginLabel(pluginWrapper.getDescriptor()));
                    DeploymentOptions deploymentOptions = new DeploymentOptions();
                    JsonObject map = applicationContext.getVConfig(pluginWrapper.getPluginId());
                    deploymentOptions.setConfig(map);
                    VerticlePlugin plugin = (VerticlePlugin) pluginWrapper.getPlugin();
                    applicationContext.getVertx().deployVerticle(plugin, deploymentOptions).onSuccess(x -> {
                        pluginWrapper.setPluginState(PluginState.STARTED);
                        pluginWrapper.setFailedException(null);
                        startedPlugins.add(pluginWrapper);
                        firePluginStateEvent(new PluginStateEvent(this, pluginWrapper, pluginState));
                    }).onFailure(e -> {
                        pluginWrapper.setPluginState(PluginState.FAILED);
                        pluginWrapper.setFailedException(e);
                        firePluginStateEvent(new PluginStateEvent(this, pluginWrapper, pluginState));
                    });
                } catch (Exception | LinkageError e) {
                    pluginWrapper.setPluginState(PluginState.FAILED);
                    pluginWrapper.setFailedException(e);
                    log.error("Unable to start plugin '{}'", getPluginLabel(pluginWrapper.getDescriptor()), e);
                }
            }
        }
    }

    @Override
    public void stopPlugins() {
        Collections.reverse(startedPlugins);
        Iterator<PluginWrapper> itr = startedPlugins.iterator();
        while (itr.hasNext()) {
            PluginWrapper pluginWrapper = itr.next();
            PluginState pluginState = pluginWrapper.getPluginState();
            if (PluginState.STARTED == pluginState) {
                try {
                    log.info("Stop plugin '{}'", getPluginLabel(pluginWrapper.getDescriptor()));
                    VerticlePlugin plugin = (VerticlePlugin) pluginWrapper.getPlugin();
                    applicationContext.getVertx().undeploy(plugin.deploymentID()).onSuccess(x -> {
                        pluginWrapper.setPluginState(PluginState.STOPPED);
                        itr.remove();
                        firePluginStateEvent(new PluginStateEvent(this, pluginWrapper, pluginState));
                    }).onFailure(e -> {
                        log.error(e.getMessage(), e);
                        firePluginStateEvent(new PluginStateEvent(this, pluginWrapper, pluginState));
                    });
                } catch (PluginRuntimeException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    protected PluginFactory createPluginFactory() {
        return new VerticlePluginFactory(this.applicationContext);
    }

}
