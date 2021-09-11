package cn.vtohru.orm.mapping;

import cn.vtohru.orm.IDataStore;

/**
 * IMapperFactory is responsible to create and store instances of {@link IMapper} for all classes, which shall be
 * persisted into the datastore
 * 
 * @author Michael Remme
 * 
 */
public interface IMapperFactory {

  /**
   * Retrieve the {@link IMapper} for the given class
   * 
   * @param mapperClass
   * @return
   * @throws Exception
   *           any Exception which can occur in the init process
   */
  <T> IMapper<T> getMapper(Class<T> mapperClass);

  /**
   * Returns true, if the given class specifies a mappable class. At a minimum whic method will have to check, wether
   * the class is marked with the {@link Entity} annotation
   * 
   * @param mapperClass
   *          the class to be checkd
   * @return true, if class specifies a mapper, false otherwise
   */
  boolean isMapper(Class<?> mapperClass);

  /**
   * Get the {@link IDataStore} which created the current instance
   * 
   * @return
   */
  IDataStore<?, ?> getDataStore();

  /**
   * Reset all mapping information. Mappings will be recreated with the next request to {@link #getMapper(Class)}
   */
  void reset();

  /**
   * Get the instance of {@link IPropertyMapperFactory} which is used by the current implementation
   * 
   * @return the {@link IPropertyMapperFactory} to retrieve new instances of {@link IPropertyMapper}
   * @deprecated will be removed after complete switch to jackson
   */
  @Deprecated
  IPropertyMapperFactory getPropertyMapperFactory();

}
