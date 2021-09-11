package cn.vtohru.orm.mongo.mapper;

import cn.vtohru.orm.mapping.IMapper;
import cn.vtohru.orm.mapping.IPropertyMapperFactory;
import cn.vtohru.orm.mapping.impl.AbstractMapperFactory;
import cn.vtohru.orm.mongo.MongoDataStore;

public class MongoMapperFactory extends AbstractMapperFactory {

  /**
   * @param dataStore
   */
  public MongoMapperFactory(MongoDataStore dataStore) {
    super(dataStore);
  }

  @Override
  protected <T> IMapper<T> createMapper(Class<T> mapperClass) {
    return new MongoMapper<>(mapperClass, this);
  }

  @Override
  public IPropertyMapperFactory getPropertyMapperFactory() {
    throw new UnsupportedOperationException("JacksonMapperFactory is not allowed to use IPropertyMapper");
  }
}
