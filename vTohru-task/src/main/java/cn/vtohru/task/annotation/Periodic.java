package cn.vtohru.task.annotation;

@Task
public @interface Periodic {
    long delay();
}
