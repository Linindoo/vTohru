package cn.vtohru.orm;


import java.util.function.Consumer;

public abstract class AbstractQuery<T> implements Query<T>{

    public Query<T> and(Consumer<Query> consumer) {
        return and().appendChild(consumer);
    }

    public Query or(Consumer<Query> consumer) {
        return or().appendChild(consumer);
    }

    public Query appendChild(Consumer<Query> consumer) {
        Query instance = instance();
        consumer.accept(instance);
        this.appendCondition("", instance.getSegment(), instance.getParams());
        return this;
    }

    public abstract Query appendCondition(String condition, String expression, Object... params);


    public abstract Query instance();

}
