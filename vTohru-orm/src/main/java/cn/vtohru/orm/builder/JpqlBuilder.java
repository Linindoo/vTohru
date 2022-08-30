package cn.vtohru.orm.builder;

import java.util.List;

public interface JpqlBuilder {

    void setTable(String table);

    List<Object> getParams();

    String toCountJpql();

    String toJpql();

    String toSegment();

    void append(String condition,String expression, Object... params);

    void select(String... columns);

    void limit(Integer offset, Integer rowCount);

    void orderBy(String orderBy);
}
