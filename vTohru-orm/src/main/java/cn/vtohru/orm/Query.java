package cn.vtohru.orm;

import cn.vtohru.orm.builder.JpqlBuilder;
import cn.vtohru.orm.data.PageData;
import io.vertx.core.Future;

import java.util.Collection;
import java.util.List;

public interface Query<T> extends LamdaQuery<Query<T>,Query<T>> {

    Query<T> from(Class<T> table);

    Query<T> where(String column, Object params);


    Query<T> and();

    Query<T> or();

    Query<T> select(String... params);

    Query<T> eq(String column, Object param);

    Query<T> ne(String column, Object param);

    Query<T> le(String column, Object param);

    Query<T> lt(String column, Object param);

    Query<T> in(String column, Collection<Object> params);

    Query<T> ge(String column, Object param);

    Query<T> gt(String column, Object param);

    Query<T> like(String column, Object param);

    Query<T> appendCondition(boolean and, String column, String condition, Object value);

    Query<T> orderBy(String orderBy);

    List<Object> getParams();

    JpqlBuilder geBuilder();

    Future<T> first();

    Future<T> first(boolean errorOnNull);

    Future<Long> count();

    Future<Void> delete();

    Future<List<T>> all();

    Future<PageData<T>> pagination(int offset, int rowCount);

}
