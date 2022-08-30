package cn.vtohru.orm.builder;


import cn.vtohru.orm.Query;

import java.util.function.Consumer;

public abstract class AbstractQuery<T> implements Query<T> {

    public Query<T> and(Consumer<Query<T>> consumer) {
        return and().appendChild(consumer);
    }

    public Query<T> or(Consumer<Query<T>> consumer) {
        return or().appendChild(consumer);
    }

    public abstract Query<T> appendCondition(String condition, String expression, Object... params);


    public abstract Query<T> instance();

}
