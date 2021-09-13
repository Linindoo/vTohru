package cn.vtohru.orm.mapping;

import cn.vtohru.orm.mapping.datastore.IColumnInfo;
import cn.vtohru.orm.mapping.impl.AbstractStoreObject;
import io.micronaut.core.annotation.AnnotationValue;
import io.vertx.core.Future;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;

/**
 * Describes a property of an {@link IMapper}
 * 
 * @author Michael Remme
 * 
 */
public interface IProperty<T> {

  String getName();

  String getFullName();

  AnnotationValue getAnnotation(Class<? extends Annotation> annotationClass);

  boolean hasAnnotation(Class<? extends Annotation> annotationClass);

  IMapper getMapper();

  /**
   * Is this field a {@link Map}?
   * 
   * @return true, if field is an instance of {@link Map}
   */
  boolean isMap();

  /**
   * returns the type of the underlying java field
   * 
   * @return the type class
   */
  Class<?> getType();

  /**
   * Get the information whether the field defines an array
   * 
   * @return the isArray
   */
  boolean isArray();

  /**
   * Get the information whether the field defines a {@link Collection}
   * 
   * @return the isCollection
   */
  boolean isCollection();

  /**
   * Get the {@link IColumnInfo} which is connected to the current field
   * 
   * @return
   */
  IColumnInfo getColumnInfo();

  /**
   * Returns true, if the current field is a field annotated with {@link javax.persistence.Id}
   * 
   * @return
   */
  boolean isIdField();

  /**
   * returns true if this property shall be ignored
   * 
   * @return
   */
  boolean isIgnore();

  /**
   * 将storeObject中的值转存到实体中
   * @param tmpObject
   * @param storeObject
   * @return
   */
  Future<Void> fromStoreObject(T tmpObject, AbstractStoreObject storeObject);

  /**
   * 将实体中的值转存到storeObject中
   * @param entity
   * @param storeObject
   * @return
   */
  Future<Void> intoStoreObject(T entity, AbstractStoreObject storeObject);

  /**
   * 将值写入到实体对象中
   * @param entity
   * @param data
   * @return
   */
  Future<Object> writeData(T entity, Object data) ;

  /**
   * 从实体中获取值
   * @param entity
   * @return
   */
  Future<Object> readData(T entity);

}
