package cn.vtohru.orm;

import cn.vtohru.orm.builder.JpqlBuilder;
import cn.vtohru.orm.data.PageData;
import io.vertx.core.Future;

import java.util.Collection;
import java.util.List;

public interface Query<T> extends LamdaQuery<Query<T>,Query<T>> {

    Query<T> from(Class<T> table);

    Query<T> where(String column, Object params);

    Query<T> where(SFunction<T, ?> function, Object params);

    Query<T> and();

    Query<T> or();

    Query<T> select(String... params);

    Query<T> eq(String column, Object param);

    Query<T> eq(SFunction<T,?> function, Object param);

    Query<T> ne(String column, Object param);

    Query<T> ne(SFunction<T, ?> function, Object param);

    Query<T> le(String column, Object param);

    Query<T> le(SFunction<T, ?> function, Object param);

    Query<T> lt(String column, Object param);

    Query<T> lt(SFunction<T, ?> function, Object param);

    Query<T> in(String column, Collection<Object> params);

    Query<T> in(SFunction<T, ?> function, Collection<Object> params);

    Query<T> ge(String column, Object param);

    Query<T> ge(SFunction<T, ?> function, Object param);

    Query<T> gt(String column, Object param);

    Query<T> gt(SFunction<T, ?> function, Object param);

    Query<T> like(String column, Object param);

    Query<T> like(SFunction<T, ?> function, Object param);

    Query<T> appendCondition(boolean and, String column, String condition, Object value);

    Query<T> orderBy(String column, boolean reverse);

    Query<T> orderBy(SFunction<T, ?> function, boolean reverse);

    Query<T> orderBy(String column);

    Query<T> orderBy(SFunction<T, ?> function);

    List<Object> getParams();

    JpqlBuilder geBuilder();

    Future<T> first();

    Future<T> first(boolean errorOnNull);

    Future<Long> count();

    Future<Void> delete();

    Future<List<T>> all();

    Future<PageData<T>> pagination(int offset, int rowCount);

}
