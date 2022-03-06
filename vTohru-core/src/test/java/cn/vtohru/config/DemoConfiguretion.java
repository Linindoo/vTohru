package cn.vtohru.config;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("vtohru.cdemo")
public class DemoConfiguretion {
    private String name;
    private String path;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
