package cn.vtohru.demo;

import cn.vtohru.message.annotation.MessageAutoConfigure;
import cn.vtohru.microservice.annotation.ServiceAutoConfigure;
import cn.vtohru.plugin.PluginApplicationContext;
import cn.vtohru.plugin.VerticlePlugin;
import cn.vtohru.web.annotation.WebAutoConfigure;
import org.pf4j.PluginWrapper;

@WebAutoConfigure(port = 7777)
@ServiceAutoConfigure
@MessageAutoConfigure
public class DemoPlugin extends VerticlePlugin {
    public DemoPlugin(PluginWrapper wrapper, PluginApplicationContext pluginApplicationContext) {
        super(wrapper, pluginApplicationContext);
    }

}
