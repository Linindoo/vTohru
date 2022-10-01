/*
 * Copyright (C) 2012-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.vtohru.plugin;

import cn.vtohru.context.VerticleApplicationContext;
import io.micronaut.context.ApplicationContext;
import org.pf4j.DefaultPluginManager;
import org.pf4j.ExtensionFactory;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.util.List;

/**
 * @author Decebal Suiu
 */
public class VTohruPluginManager extends DefaultPluginManager {

    private VerticleApplicationContext applicationContext;

    public VTohruPluginManager(VerticleApplicationContext applicationContext) {
        super();
        this.applicationContext = applicationContext;
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
    @PostConstruct
    public void init() {
        loadPlugins();
        startPlugins();
        ExtensionsInjector extensionsInjector = new ExtensionsInjector(this);
        extensionsInjector.injectExtensions();
    }

}
