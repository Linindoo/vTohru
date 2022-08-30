package cn.vtohru.orm.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class GenericJpqlBuilder implements JpqlBuilder {
    protected String table;
    protected List<String> columns = new ArrayList<>();
    protected List<Condition> conditions = new ArrayList<>();
    protected List<String> orderBys = new ArrayList<>();
    protected List<Object> params = new ArrayList<>();
    protected Integer offset;
    protected Integer rowCount;


    @Override
    public void setTable(String table) {
        this.table = table;
    }

    @Override
    public List<Object> getParams() {
        return params;
    }

    @Override
    public void append(String condition, String expression, Object... params) {
        this.conditions.add(new Condition(condition, expression));
        this.params.addAll(Arrays.asList(params));
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

    public static class Condition {
        private String condition;
        protected String expression;

        public Condition(String condition, String expression) {
            this.condition = condition;
            this.expression = expression;
        }

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }

        public String getExpression() {
            return expression;
        }

        public void setExpression(String expression) {
            this.expression = expression;
        }
    }


}
