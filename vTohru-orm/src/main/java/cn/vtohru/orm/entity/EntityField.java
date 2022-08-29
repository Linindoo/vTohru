package cn.vtohru.orm.entity;

import io.micronaut.core.beans.BeanProperty;

public class EntityField<B> {
    private String fieldName;
    private boolean isPrimary;
    private BeanProperty<B, ?> property;

    private String generationType;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public BeanProperty<B, ?> getProperty() {
        return property;
    }

    public void setProperty(BeanProperty<B, ?> property) {
        this.property = property;
    }

    public String getGenerationType() {
        return generationType;
    }

    public void setGenerationType(String generationType) {
        this.generationType = generationType;
    }
}
