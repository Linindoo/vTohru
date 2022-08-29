package cn.vtohru.mysql;

import cn.vtohru.orm.AbstractQuery;
import cn.vtohru.orm.JpqlBuilder;
import cn.vtohru.orm.Query;
import cn.vtohru.orm.entity.EntityManager;
import cn.vtohru.orm.impl.BaseQuery;
import cn.vtohru.orm.impl.IDataProxy;
import io.micronaut.core.util.StringUtils;


public class MysqlQuery extends BaseQuery {
    protected String preCondition;

    public MysqlQuery(IDataProxy dataProxy, EntityManager entityManager) {
        this(dataProxy, entityManager, new MysqlBuilder());
    }
    public MysqlQuery(IDataProxy dataProxy, EntityManager entityManager, JpqlBuilder jpqlBuilder) {
        super(dataProxy, entityManager, jpqlBuilder);
    }

    @Override
    public String toCountJpql() {
        return this.jpqlBuilder.toCountJpql();
    }

    @Override
    public Query where(String column, Object params) {
        return appendCondition("and", column + "=?", params);
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
    public Query eq(String column, Object param) {
        return appendCondition("and", column + "=?", param);
    }

    @Override
    public Query ne(String column, String param) {
        return appendCondition("and", column + "!=?", param);
    }

    @Override
    public Query le(String column, String param) {
        return appendCondition("and", column + ">=?", param);
    }

    @Override
    public Query lt(String column, String param) {
        return appendCondition("and", column + ">?", param);
    }

    @Override
    public Query ge(String column, String param) {
        return appendCondition("and", column + "<=?", param);
    }

    @Override
    public Query gt(String column, String param) {
        return appendCondition("and", column + "<", param);
    }

    @Override
    public Query like(String column, String param) {
        return appendCondition("and", column + " like ", param);
    }


    @Override
    public String getJpql() {
        return this.jpqlBuilder.toJpql();
    }

    @Override
    public Query appendCondition(String condition, String expression, Object... params) {
        if (StringUtils.isNotEmpty(this.preCondition)) {
            this.jpqlBuilder.append(this.preCondition, expression, params);
        } else {
            this.jpqlBuilder.append(condition, expression, params);
        }
        this.preCondition = "";
        return this;
    }

    @Override
    public AbstractQuery instance() {
        return new MysqlQuery(this.dataProxy, this.entityManager, this.jpqlBuilder);
    }


}
