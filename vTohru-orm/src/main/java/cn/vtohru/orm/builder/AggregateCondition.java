package cn.vtohru.orm.builder;

import cn.vtohru.orm.Condition;

import java.util.List;

public class AggregateCondition extends Condition {
    protected List<SingleCondition> conditions;

    public AggregateCondition(boolean and, List<SingleCondition> conditions) {
        super(and);
        this.conditions = conditions;
    }

    public List<SingleCondition> getConditions() {
        return conditions;
    }

    public void setConditions(List<SingleCondition> conditions) {
        this.conditions = conditions;
    }
}
