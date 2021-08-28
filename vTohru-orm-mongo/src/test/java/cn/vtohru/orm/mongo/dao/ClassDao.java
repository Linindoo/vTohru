package cn.vtohru.orm.mongo.dao;

import cn.vtohru.orm.annotation.Entity;
import cn.vtohru.orm.annotation.field.Id;

@Entity(name = "demo")
public class ClassDao {
    @Id
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
