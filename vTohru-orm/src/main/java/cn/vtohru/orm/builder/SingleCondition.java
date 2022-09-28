package cn.vtohru.orm.builder;

import cn.vtohru.orm.Condition;

public class SingleCondition extends Condition {
    private String column;
    private String condition;
    private Object value;

    public SingleCondition(boolean and, String column, String condition, Object value) {
        super(and);
        this.column = column;
        this.condition = condition;
        this.value = value;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
