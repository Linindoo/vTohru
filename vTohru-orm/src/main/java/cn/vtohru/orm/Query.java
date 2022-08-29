package cn.vtohru.orm;

import cn.vtohru.orm.impl.PageData;
import io.vertx.core.Future;

import java.util.List;

public interface Query<T> extends LamdaQuery<Query,Query> {

    String toCountJpql();

    Query<T> from(Class<T> table);

    Query<T> where(String column, Object params);


    Query<T> and();

    Query<T> or();

    Query<T> select(String... params);

    Query<T> eq(String column, Object param);

    Query<T> ne(String column, String param);

    Query<T> le(String column, String param);

    Query<T> lt(String column, String param);

    Query<T> ge(String column, String param);

    Query<T> gt(String column, String param);

    Query<T> like(String column, String param);

    Query<T> appendCondition(String condition,String expression, Object... params);

    String getJpql();

    String getSegment();

    List<Object> getParams();

    Future<T> first();

    Future<List<T>> all();

    Future<PageData<T>> pagination(int offset, int rowCount);



}
