package cn.vtohru.orm.builder;


import cn.vtohru.orm.Condition;

import java.util.List;

public interface ChildBuilder{
    void appendChild(boolean and, ChildBuilder builder);
    List<Condition> getCondition();
}
