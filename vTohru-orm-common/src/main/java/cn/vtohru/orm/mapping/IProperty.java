package cn.vtohru.orm.mapping;

import cn.vtohru.orm.mapping.datastore.IColumnInfo;
import cn.vtohru.orm.mapping.impl.AbstractStoreObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;

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

  Annotation getAnnotation(Class<? extends Annotation> annotationClass);

  boolean hasAnnotation(Class<? extends Annotation> annotationClass);

  Annotation getEmbedRef();

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

  void fromStoreObject(T tmpObject, AbstractStoreObject abstractStoreObject, Promise<Void> f);

  Object readData(T record);

  void intoStoreObject(T entity, AbstractStoreObject tfAbstractStoreObject, Handler<AsyncResult<Void>> handler);

  void writeData(T record, Object data) ;

  void readForStore(T mapper,   Handler<AsyncResult<Object>> handler);

  void fromObjectReference(Object entity, IObjectReference reference, Handler<AsyncResult<Void>> handler);

}
