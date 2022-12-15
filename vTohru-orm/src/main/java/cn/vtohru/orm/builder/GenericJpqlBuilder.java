package cn.vtohru.orm.builder;

import cn.vtohru.orm.Condition;
import cn.vtohru.orm.OrderCondition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GenericJpqlBuilder implements JpqlBuilder {
    protected String table;
    protected List<String> columns = new ArrayList<>();
    protected List<Condition> conditions = new ArrayList<>();
    protected List<OrderCondition> orderBys = new ArrayList<>();


    @Override
    public void setTable(String table) {
        this.table = table;
    }

    @Override
    public void append(boolean and, String column, String condition, Object value) {
        this.conditions.add(new SingleCondition(and, column, condition, value));
    }

    @Override
    public void select(String... columns) {
        this.columns.addAll(Arrays.asList(columns));
    }

    @Override
    public void orderBy(String orderBy, boolean reverse) {
        this.orderBys.add(new OrderCondition(orderBy, reverse));
    }



    @Override
    public void appendChild(boolean and, ChildBuilder builder) {
        List<SingleCondition> singleConditions = builder.getCondition().stream().filter(x -> x instanceof SingleCondition).map(x -> (SingleCondition) x).collect(Collectors.toList());
        Condition condition = new AggregateCondition(and, singleConditions);
        this.conditions.add(condition);
    }

    @Override
    public List<String> columns() {
        return this.columns;
    }

    @Override
    public List<Condition> getCondition() {
        return this.conditions;
    }

    @Override
    public List<OrderCondition> orders() {
        return this.orderBys;
    }
}
