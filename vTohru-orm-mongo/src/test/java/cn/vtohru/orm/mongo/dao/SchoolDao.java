package cn.vtohru.orm.mongo.dao;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SchoolDao {
    private String name;
    @Id
    private String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
