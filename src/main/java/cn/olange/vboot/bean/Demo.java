package cn.olange.vboot.bean;

import javax.inject.Singleton;

@Singleton
public class Demo {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
