package cn.vtohru.orm.entity;

import cn.vtohru.orm.SFunction;
import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.core.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityInfo {
    private String tableName;
    private BeanIntrospection<?> beanIntrospection;
    private Map<String, EntityField> fieldMap = new ConcurrentHashMap<>();
    private List<EntityField> keyFields = new ArrayList<>();
    private Map<Serializable, String> CACHE_FIELD_NAME = new ConcurrentHashMap<>(8);

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Map<String, EntityField> getFieldMap() {
        return fieldMap;
    }

    public void setFieldMap(Map<String, EntityField> fieldMap) {
        this.fieldMap = fieldMap;
    }

    public BeanIntrospection<?> getBeanIntrospection() {
        return beanIntrospection;
    }

    public void setBeanIntrospection(BeanIntrospection<?> beanIntrospection) {
        this.beanIntrospection = beanIntrospection;
    }

    public List<EntityField> getKeyFields() {
        return keyFields;
    }

    public void setKeyFields(List<EntityField> keyFields) {
        this.keyFields = keyFields;
    }

    public <T> EntityField<T> getField(Serializable lambda) {
        String fieldName = CACHE_FIELD_NAME.get(lambda);
        if (StringUtils.isEmpty(fieldName)) {
            return null;
        }
        return fieldMap.get(fieldName);
    }

    public <T> void setLambdaField(Serializable lambda, String fieldName) {
        if (StringUtils.isNotEmpty(fieldName)) {
            CACHE_FIELD_NAME.put(lambda, fieldName);
        }
    }
}
