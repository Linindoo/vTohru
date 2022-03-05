package cn.vtohru.orm.sql.dao;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "news")
public class News {
    @Id
    private Long Id;
    private String title;
    private String content;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
