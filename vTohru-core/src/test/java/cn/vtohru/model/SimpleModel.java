package cn.vtohru.model;

import io.vertx.core.shareddata.Shareable;

public class SimpleModel implements Shareable {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
