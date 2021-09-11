package cn.vtohru.orm.mapping.impl;

import cn.vtohru.orm.annotation.field.Embedded;
import cn.vtohru.orm.annotation.field.Referenced;
import cn.vtohru.orm.mapping.IObjectReference;
import cn.vtohru.orm.mapping.IProperty;
import io.micronaut.core.beans.BeanProperty;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;

public class DefaultMappedField<T> extends AbstractProperty<T> {
    private static final Logger logger = LoggerFactory.getLogger(DefaultMappedField.class);
    private BeanProperty<T, Object> beanProperty;

    public DefaultMappedField(BeanProperty<T, Object> beanProperty, AbstractMapper mapper) {
        super(mapper);
        this.beanProperty = beanProperty;
        init();
    }

    protected void init() {
        computeAnnotations();
    }
    protected void computeAnnotations() {
        if (hasAnnotation(Referenced.class)) {
            embedRef = getAnnotation(Referenced.class);
        } else if (hasAnnotation(Embedded.class)) {
            embedRef = getAnnotation(Embedded.class);
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
    public Annotation getAnnotation(Class<? extends Annotation> annotationClass) {
        return beanProperty.getDeclaringType().getAnnotation(annotationClass);
    }

    @Override
    public boolean hasAnnotation(Class<? extends Annotation> annotationClass) {
        return beanProperty.isAnnotationPresent(annotationClass);
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
        return false;
    }


    @Override
    public void fromStoreObject(T entity, AbstractStoreObject storeObject, Promise<Void> handler) {
        try {
            Object fieldValue = storeObject.get(this);
            if (fieldValue instanceof IObjectReference) {
                storeObject.getObjectReferences().add((IObjectReference) fieldValue);
            } else {
                this.beanProperty.set(entity, fieldValue);
            }
            handler.handle(Future.succeededFuture());
        } catch (Exception e) {
            handler.fail(e);
        }
    }

    @Override
    public Object readData(T record) {
        return beanProperty.get(record);
    }

    @Override
    public void intoStoreObject(T entity, AbstractStoreObject storeObject, Handler<AsyncResult<Void>> handler) {
        Object value = this.readData(entity);
        if (value != null) {
            storeObject.put(this, value);
        }
        handler.handle(Future.succeededFuture());
    }

    @Override
    public void writeData(T record, Object data) {
        this.beanProperty.set(record, data);
    }

    @Override
    public void readForStore(T record, Handler<AsyncResult<Object>> handler) {
        Object data = readData(record);
        handler.handle(Future.succeededFuture(data));
    }

    @Override
    public void fromObjectReference(Object entity, IObjectReference reference, Handler<AsyncResult<Void>> handler) {

    }
}
