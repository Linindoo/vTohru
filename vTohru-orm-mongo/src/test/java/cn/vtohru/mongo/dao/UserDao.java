package cn.vtohru.mongo.dao;

import javax.persistence.*;

@Entity(name = "user")
public class UserDao {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public String id;
    @Column
    private String name;
    @Column
    private String gender;
    @Column
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
