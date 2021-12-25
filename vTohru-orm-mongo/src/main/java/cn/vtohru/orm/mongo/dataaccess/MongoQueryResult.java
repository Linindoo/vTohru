package cn.vtohru.orm.mongo.dataaccess;

import cn.vtohru.orm.dataaccess.query.IQueryResult;
import cn.vtohru.orm.dataaccess.query.impl.AbstractQueryResult;
import cn.vtohru.orm.mongo.MongoDataStore;
import cn.vtohru.orm.mongo.mapper.MongoMapper;

import java.util.List;
import java.util.Optional;

/**
 * An implementation of {@link IQueryResult} for Mongo
 * 
 * @author Michael Remme
 * @param <T>
 *          the type of the underlaying mapper
 */
public class MongoQueryResult<T> extends AbstractQueryResult<T> {
  /**
   * Contains the original result from mongo
   */
  private List<T> jsonResult;

  
  public MongoQueryResult(List<T> result, MongoDataStore store, MongoMapper mapper,
      MongoQueryExpression queryExpression) {
    super(store, mapper, queryExpression);
    this.jsonResult = result;
  }


  @Override
  public List<T> result() {
    return jsonResult;
  }

  @Override
  public Optional<T> first() {
    return Optional.ofNullable(jsonResult).filter(x -> x.size() > 0).map(x -> x.get(0));
  }

  @Override
  public int size() {
    return jsonResult != null ? jsonResult.size() : 0;
  }
}
