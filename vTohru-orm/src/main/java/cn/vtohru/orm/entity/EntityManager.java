package cn.vtohru.orm.entity;

import cn.vtohru.orm.SFunction;
import cn.vtohru.orm.exception.OrmException;
import cn.vtohru.orm.utils.LambdaUtils;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.core.beans.BeanProperty;
import io.micronaut.core.convert.ConversionService;
import io.micronaut.core.util.StringUtils;
import io.vertx.core.json.JsonObject;

import javax.inject.Singleton;
import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class EntityManager {
    private Map<Class<?>, EntityInfo> entityInfoMap = new ConcurrentHashMap<>();
    private Map<Serializable, String> CACHE_FIELD_NAME = new ConcurrentHashMap<>(8);

    public EntityInfo getEntity(Class<?> entityClass) {
        EntityInfo entityInfo = entityInfoMap.get(entityClass);
        if (entityInfo != null) {
            return entityInfo;
        }
        EntityInfo entity = createEntity(entityClass);
        entityInfoMap.put(entityClass, entity);
        return entity;
    }

    protected EntityInfo createEntity(Class<?> entityClass) {
        BeanIntrospection<?> introspection = BeanIntrospection.getIntrospection(entityClass);
        EntityInfo entityInfo = new EntityInfo();
        AnnotationValue<Entity> annotation = introspection.getAnnotation(Entity.class);
        if (annotation == null) {
            throw new OrmException("该class不是有效的table实体映射类");
        }
        String tableName =  annotation.stringValue("name").orElse(entityClass.getSimpleName());
        entityInfo.setTableName(tableName);
        entityInfo.setBeanIntrospection(introspection);
        Map<String, EntityField> fieldMap = entityInfo.getFieldMap();

        List<EntityField> keyFields = new ArrayList<>();
        for (BeanProperty<?, ?> fieldProperty : introspection.getBeanProperties()) {
            AnnotationValue<Column> columnAnnotationValue = fieldProperty.getAnnotation(Column.class);
            if (columnAnnotationValue != null) {
                String fieldName = columnAnnotationValue.stringValue("name").orElse(fieldProperty.getName());
                EntityField entityField = new EntityField();
                entityField.setFieldName(fieldName);
                entityField.setProperty(fieldProperty);
                fieldMap.put(fieldProperty.getName(), entityField);
                AnnotationValue<GeneratedValue> propertyAnnotation = fieldProperty.getAnnotation(GeneratedValue.class);
                if (propertyAnnotation != null) {
                    Optional<GenerationType> strategy = propertyAnnotation.get("strategy", GenerationType.class);
                    if (strategy.isPresent()) {
                        entityField.setGenerationType(strategy.get().name());
                    }
                }
                if (fieldProperty.hasAnnotation(Id.class)) {
                    entityField.setPrimary(true);
                    keyFields.add(entityField);
                } else {
                    entityField.setPrimary(false);
                }
            }
        }
        entityInfo.setKeyFields(keyFields);
        return entityInfo;
    }

    public <T> boolean existPrimary(T model) {
        EntityInfo entity = getEntity(model.getClass());
        for (Map.Entry<String, EntityField> entityFieldEntry : entity.getFieldMap().entrySet()) {
            EntityField entityField = entityFieldEntry.getValue();
            if (entityField.isPrimary() && entityField.getProperty().get(model) == null) {
                return false;
            }
        }
        return true;
    }

    public <T> T convertEntity(JsonObject row, Class<T> entityClass) {
        EntityInfo entity = getEntity(entityClass);
        T bean = (T) entity.getBeanIntrospection().instantiate();
        for (Map.Entry<String, EntityField> fieldEntry : entity.getFieldMap().entrySet()) {
            String fieldName = fieldEntry.getValue().getFieldName();
            Object value = row.getValue(fieldName);
            if (value != null) {
                Optional ret = ConversionService.SHARED.convert(value, fieldEntry.getValue().getProperty().getType());
                if (ret.isPresent()) {
                    fieldEntry.getValue().getProperty().set(bean, ret.get());
                }
            }
        }
        return bean;
    }

    public <T> EntityField<T> getLambdaField(SFunction<T, ?> function, Class<T> eClass) {
        EntityInfo entity = getEntity(eClass);
        EntityField<T> entityField = entity.getField(function);
        if (entityField != null) {
            return entityField;
        }
        String fieldName = LambdaUtils.getLambdaFieldName(function);
        if (StringUtils.isEmpty(fieldName)) {
            return null;
        }
        entityField = entity.getFieldMap().get(fieldName);
        if (entityField != null) {
            entity.setLambdaField(function, fieldName);
        }
        return entityField;
    }

}
