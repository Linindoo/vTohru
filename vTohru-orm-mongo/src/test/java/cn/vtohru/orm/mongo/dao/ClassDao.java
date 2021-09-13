package cn.vtohru.orm.mongo.dao;

import cn.vtohru.orm.annotation.Index;
import cn.vtohru.orm.annotation.IndexField;
import cn.vtohru.orm.annotation.Indexes;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;
import java.util.List;

@Entity(name = "demo2")
@Indexes(@Index(name = "testIndex", fields = {
        @IndexField(fieldName = "name"),@IndexField(fieldName = "status") }))

public class ClassDao {
    @Id
    private String id;
    private String name;
    private int studentNum;
    private long max;
    private Date createTime;
    private List<String> tags;
    private boolean enable;
    private double number;
    private float min;
    private Status status;

    private SchoolDao schoolDao;

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

    public int getStudentNum() {
        return studentNum;
    }

    public void setStudentNum(int studentNum) {
        this.studentNum = studentNum;
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public double getNumber() {
        return number;
    }

    public void setNumber(double number) {
        this.number = number;
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public SchoolDao getSchoolDao() {
        return schoolDao;
    }

    public void setSchoolDao(SchoolDao schoolDao) {
        this.schoolDao = schoolDao;
    }
}
