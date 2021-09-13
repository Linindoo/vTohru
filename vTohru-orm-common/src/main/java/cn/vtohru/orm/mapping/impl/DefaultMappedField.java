package cn.vtohru.orm.mapping.impl;

import cn.vtohru.orm.annotation.field.Ignore;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.beans.BeanProperty;
import io.vertx.core.Future;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;

public class DefaultMappedField<T> extends AbstractProperty<T> {
    private static final Logger logger = LoggerFactory.getLogger(DefaultMappedField.class);
    private BeanProperty<T, Object> beanProperty;
    private boolean ignore = false;

    public DefaultMappedField(BeanProperty<T, Object> beanProperty, AbstractMapper mapper) {
        super(mapper);
        this.beanProperty = beanProperty;
        init();
    }

    protected void init() {
        computeAnnotations();
    }
    protected void computeAnnotations() {
        if (beanProperty.hasAnnotation(Ignore.class)) {
            this.ignore = true;
        }
    }

    @Override
    public String getName() {
        return beanProperty.getName();
    }

    @Override
    public String getFullName() {
        return beanProperty.getDeclaringType().getName() + "." + beanProperty.getName();
    }

    @Override
    public AnnotationValue getAnnotation(Class<? extends Annotation> annotationClass) {
        return beanProperty.getAnnotation(annotationClass);
    }

    @Override
    public boolean hasAnnotation(Class<? extends Annotation> annotationClass) {
        return beanProperty.hasAnnotation(annotationClass);
    }

    @Override
    public boolean isMap() {
        return Map.class.isAssignableFrom(beanProperty.getDeclaringType());
    }

    @Override
    public Class<?> getType() {
        return beanProperty.getType();
    }

    @Override
    public boolean isArray() {
        return beanProperty.getType().isArray();
    }

    @Override
    public boolean isCollection() {
        return Collection.class.isAssignableFrom(beanProperty.getDeclaringType());
    }

    @Override
    public boolean isIgnore() {
        return this.ignore;
    }


    @Override
    public Future<Void> fromStoreObject(T entity, AbstractStoreObject storeObject) {
        try {
            Object fieldValue = storeObject.get(this);
            if (fieldValue != null) {
                this.beanProperty.convertAndSet(entity, fieldValue);
            }
            return Future.succeededFuture();
        } catch (Exception e) {
            return Future.failedFuture(e);
        }
    }

    @Override
    public Future<Void> intoStoreObject(T entity, AbstractStoreObject storeObject) {
        Object value = beanProperty.get(entity);
        if (value != null) {
            storeObject.put(this, value);
        }
        return Future.succeededFuture();
    }

    @Override
    public Future<Object> writeData(T record, Object data) {
        try {
            this.beanProperty.set(record, data);
            return Future.succeededFuture();
        } catch (Exception e) {
            return Future.failedFuture(e);
        }
    }

    @Override
    public Future<Object> readData(T entity) {
        try {
            Object data = beanProperty.get(entity);
            return Future.succeededFuture(data);
        } catch (Exception e) {
            return Future.failedFuture(e);
        }
    }
}
