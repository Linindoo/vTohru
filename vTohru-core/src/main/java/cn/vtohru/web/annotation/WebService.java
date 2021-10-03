package cn.vtohru.web.annotation;

public @interface WebService {
    String name() default "";
    String root() default "";
}
