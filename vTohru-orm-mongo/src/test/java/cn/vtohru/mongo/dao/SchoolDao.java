package cn.vtohru.mongo.dao;

import javax.persistence.*;

@Entity
public class SchoolDao {
    @Column
    private String name;
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
