package cn.vtohru.plugin;

import org.pf4j.PluginWrapper;

public class VerticleInfo {
    private Class<?> type;
    private String deploymentID;
    private PluginWrapper pluginWrapper;

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public String getDeploymentID() {
        return deploymentID;
    }

    public void setDeploymentID(String deploymentID) {
        this.deploymentID = deploymentID;
    }

    public PluginWrapper getPluginWrapper() {
        return pluginWrapper;
    }

    public void setPluginWrapper(PluginWrapper pluginWrapper) {
        this.pluginWrapper = pluginWrapper;
    }
}
