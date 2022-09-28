package cn.vtohru.orm.builder;

import cn.vtohru.orm.Condition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GenericJpqlBuilder implements JpqlBuilder {
    protected String table;
    protected List<String> columns = new ArrayList<>();
    protected List<Condition> conditions = new ArrayList<>();
    protected List<String> orderBys = new ArrayList<>();
    protected Integer offset;
    protected Integer rowCount;


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
    public void limit(Integer offset, Integer rowCount) {
        this.offset = offset;
        this.rowCount = rowCount;
    }

    @Override
    public void orderBy(String orderBy) {
        this.orderBys.add(orderBy);
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
    public List<String> orders() {
        return this.orderBys;
    }
}
