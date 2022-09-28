package cn.vtohru.orm;

import cn.vtohru.orm.builder.JpqlBuilder;
import cn.vtohru.orm.data.PageData;
import io.vertx.core.Future;

import java.util.List;

public interface Query<T> extends LamdaQuery<Query<T>,Query<T>> {

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

    Query<T> appendCondition(boolean and, String column, String condition, Object value);

    Query<T> orderBy(String orderBy);

    List<Object> getParams();

    JpqlBuilder geBuilder();

    Future<T> first();

    Future<List<T>> all();

    Future<PageData<T>> pagination(int offset, int rowCount);

}
