package cn.vtohru.orm.builder;

import cn.vtohru.orm.OrderCondition;

import java.util.List;

public interface JpqlBuilder extends ChildBuilder {

    void setTable(String table);

    void append(boolean and, String column, String condition, Object value);

    void select(String... columns);

    void orderBy(String orderBy, boolean reverse);

    void appendChild(boolean and, ChildBuilder builder);

    List<String> columns();

    List<OrderCondition> orders();

}
