package cn.vtohru.orm.mongo.dao;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "demo")
public class ClassDao {

    public ClassDao() {
    }

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
