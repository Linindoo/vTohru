package cn.vtohru.mysql;

import cn.vtohru.orm.impl.GenericJpqlBuilder;
import io.micronaut.core.util.CollectionUtils;


public class MysqlBuilder extends GenericJpqlBuilder {

    @Override
    public String toCountJpql() {
        StringBuilder pqlBuilder = new StringBuilder();
        pqlBuilder.append("select count(1) from ").append(this.table).append(" where ").append(toSegment());
        return pqlBuilder.toString();
    }

    @Override
    public String toJpql() {
        StringBuilder pqlBuilder = new StringBuilder();
        pqlBuilder.append("select").append(" ").append(String.join(",", this.columns)).append(" from ").append(this.table).append(" where ").append(toSegment());
        if (CollectionUtils.isNotEmpty(this.orderBys)) {
            pqlBuilder.append(String.join(",", this.orderBys));
        }
        return pqlBuilder.toString();
    }

    @Override
    public String toSegment() {
        StringBuilder segment = new StringBuilder();
        for (int i = 0; i < this.conditions.size(); i++) {
            Condition condition = this.conditions.get(i);
            if (i != 0) {
                segment.append(" ").append(condition.getCondition()).append(" ");
            }
            segment.append(" ").append(condition.getExpression()).append(" ");

        }
        return segment.toString();
    }
}
