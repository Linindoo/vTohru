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
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

/**
 * @author Decebal Suiu
 */
public abstract class VTohruPlugin extends Plugin {

    private VerticleApplicationContext applicationContext;

    public VTohruPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    public final VerticleApplicationContext getApplicationContext() {
        if (applicationContext == null) {
            applicationContext = createApplicationContext();
        }

        return applicationContext;
    }

    @Override
    public void stop() {

    }

    protected abstract VerticleApplicationContext createApplicationContext();

}
