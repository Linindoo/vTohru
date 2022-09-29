package cn.vtohru.mongo.dao;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "class")
public class ClassDao {
    @Id
    @Column(name = "_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @Column
    private String name;
    @Column
    private int studentNum;
    @Column
    private long max;
    @Column
    private Long createTime;
    @Column
    private boolean enable;
    @Column
    private double number;
    @Column
    private double min;
    @Column
    private String status;

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

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
