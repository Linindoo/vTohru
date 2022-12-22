package cn.vtohru.orm.builder;

import cn.vtohru.orm.Condition;
import cn.vtohru.orm.Query;
import cn.vtohru.orm.SFunction;
import cn.vtohru.orm.entity.EntityField;
import cn.vtohru.orm.entity.EntityInfo;
import cn.vtohru.orm.entity.EntityManager;
import cn.vtohru.orm.data.IDataProxy;
import cn.vtohru.orm.exception.OrmException;
import io.micronaut.core.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class BaseQuery<T> extends AbstractQuery<T> {
    protected IDataProxy dataProxy;
    protected EntityManager entityManager;
    protected JpqlBuilder jpqlBuilder;
    protected Class<T> entityClass;
    private boolean useColumn = false;
    protected String preCondition;

    public BaseQuery(IDataProxy dataProxy, EntityManager entityManager, JpqlBuilder jpqlBuilder) {
        this.dataProxy = dataProxy;
        this.entityManager = entityManager;
        this.jpqlBuilder = jpqlBuilder;
    }

    @Override
    public Query<T> from(Class<T> entityClass) {
        this.entityClass = entityClass;
        EntityInfo entity = entityManager.getEntity(entityClass);
        this.jpqlBuilder.setTable(entity.getTableName());
        return this;
    }

    public String getTableName() {
        EntityInfo entity = entityManager.getEntity(entityClass);
        return entity.getTableName();
    }


    @Override
    public Query<T> select(String... params) {
        this.jpqlBuilder.select(params);
        this.useColumn = true;
        return this;
    }

    protected void checkColumns() {
        if (!this.useColumn) {
            EntityInfo entity = entityManager.getEntity(this.entityClass);
            List<String> fields = entity.getFieldMap().values().stream().map(EntityField::getFieldName).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
            this.jpqlBuilder.select(fields.toArray(new String[]{}));
        }
    }

    public Query appendChild(Consumer consumer) {
        Query instance = instance();
        consumer.accept(instance);
        if ("or".equalsIgnoreCase(this.preCondition)) {
            this.jpqlBuilder.appendChild(false, instance.geBuilder());
        } else {
            this.jpqlBuilder.appendChild(true, instance.geBuilder());
        }
        return this;
    }

    @Override
    public Query appendCondition(boolean and, String column, String condition, Object value) {
        if ("or".equalsIgnoreCase(this.preCondition)) {
            this.jpqlBuilder.append(false, column, condition, value);
        } else {
            this.jpqlBuilder.append(and, column, condition, value);
        }
        this.preCondition = "";
        return this;
    }

    @Override
    public List<Object> getParams() {
        List<Object> params = new ArrayList<>();
        for (Condition condition : this.jpqlBuilder.getCondition()) {
            if (condition instanceof SingleCondition) {
                params.add(((SingleCondition) condition).getValue());
            } else if (condition instanceof AggregateCondition) {
                for (SingleCondition singleCondition : ((AggregateCondition) condition).getConditions()) {
                    params.add(singleCondition.getValue());
                }
            }
        }
        return params;
    }

    @Override
    public JpqlBuilder geBuilder() {
        return this.jpqlBuilder;
    }

    @Override
    public Query<T> orderBy(String orderBy) {
        this.jpqlBuilder.orderBy(orderBy, false);
        return this;
    }

    @Override
    public Query<T> orderBy(String orderBy, boolean reverse) {
        this.jpqlBuilder.orderBy(orderBy, true);
        return this;
    }

    @Override
    public Query and() {
        this.preCondition = "and";
        return this;
    }

    @Override
    public Query or() {
        this.preCondition = "or";
        return this;
    }

    @Override
    public Query<T> eq(SFunction<T, ?> function, Object param) {
        EntityField entityField = entityManager.getLambdaField(function, this.entityClass);
        if (entityField == null) {
            throw new OrmException("获取lambda字段失败");
        }
        return eq(entityField.getFieldName(), param);
    }

    @Override
    public Query<T> where(SFunction<T, ?> function, Object params) {
        EntityField entityField = entityManager.getLambdaField(function, this.entityClass);
        if (entityField == null) {
            throw new OrmException("获取lambda字段失败");
        }
        return where(entityField.getFieldName(), params);
    }

    @Override
    public Query<T> ne(SFunction<T, ?> function, Object param) {
        EntityField entityField = entityManager.getLambdaField(function, this.entityClass);
        if (entityField == null) {
            throw new OrmException("获取lambda字段失败");
        }
        return ne(entityField.getFieldName(), param);
    }

    @Override
    public Query<T> le(SFunction<T, ?> function, Object param) {
        EntityField entityField = entityManager.getLambdaField(function, this.entityClass);
        if (entityField == null) {
            throw new OrmException("获取lambda字段失败");
        }
        return le(entityField.getFieldName(), param);
    }

    @Override
    public Query<T> lt(SFunction<T, ?> function, Object param) {
        EntityField entityField = entityManager.getLambdaField(function, this.entityClass);
        if (entityField == null) {
            throw new OrmException("获取lambda字段失败");
        }
        return lt(entityField.getFieldName(), param);
    }

    @Override
    public Query<T> in(SFunction<T, ?> function, Collection<Object> params) {
        EntityField entityField = entityManager.getLambdaField(function, this.entityClass);
        if (entityField == null) {
            throw new OrmException("获取lambda字段失败");
        }
        return in(entityField.getFieldName(), params);
    }

    @Override
    public Query<T> ge(SFunction<T, ?> function, Object param) {
        EntityField entityField = entityManager.getLambdaField(function, this.entityClass);
        if (entityField == null) {
            throw new OrmException("获取lambda字段失败");
        }
        return ge(entityField.getFieldName(), param);
    }

    @Override
    public Query<T> gt(SFunction<T, ?> function, Object param) {
        EntityField entityField = entityManager.getLambdaField(function, this.entityClass);
        if (entityField == null) {
            throw new OrmException("获取lambda字段失败");
        }
        return gt(entityField.getFieldName(), param);
    }

    @Override
    public Query<T> like(SFunction<T, ?> function, Object param) {
        EntityField entityField = entityManager.getLambdaField(function, this.entityClass);
        if (entityField == null) {
            throw new OrmException("获取lambda字段失败");
        }
        return like(entityField.getFieldName(), param);
    }

    @Override
    public Query<T> orderBy(SFunction<T, ?> function, boolean reverse) {
        EntityField entityField = entityManager.getLambdaField(function, this.entityClass);
        if (entityField == null) {
            throw new OrmException("获取lambda字段失败");
        }
        return orderBy(entityField.getFieldName(), reverse);
    }

    @Override
    public Query<T> orderBy(SFunction<T, ?> function) {
        EntityField entityField = entityManager.getLambdaField(function, this.entityClass);
        if (entityField == null) {
            throw new OrmException("获取lambda字段失败");
        }
        return orderBy(entityField.getFieldName());
    }

}
