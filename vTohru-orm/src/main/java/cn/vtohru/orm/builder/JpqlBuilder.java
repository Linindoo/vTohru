package cn.vtohru.orm.builder;

import java.util.List;

public interface JpqlBuilder extends ChildBuilder {

    void setTable(String table);

    void append(boolean and, String column, String condition, Object value);

    void select(String... columns);

    void limit(Integer offset, Integer rowCount);

    void orderBy(String orderBy);

    void appendChild(boolean and, ChildBuilder builder);

    List<String> columns();

    List<String> orders();

}
