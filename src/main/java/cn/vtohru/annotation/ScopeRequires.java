package cn.vtohru.annotation;

import cn.vtohru.context.ScopeCondition;
import io.micronaut.context.annotation.Requires;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Requires(condition = ScopeCondition.class)
public @interface ScopeRequires {
    String[] requireVerticle() default "";
    String[] notRequireVerticle() default "";
}
