package cn.vtohru.orm;

import java.util.function.Consumer;

public interface LamdaQuery<Param, Children> {
    Children and(Consumer<Param> consumer);

    Children appendChild(Consumer<Param> consumer);
    LamdaQuery<Param, Children> appendCondition(boolean and, String column, String condition, Object value);

    Children or(Consumer<Param> consumer);

}
