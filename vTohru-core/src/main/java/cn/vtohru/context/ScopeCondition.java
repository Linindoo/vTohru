package cn.vtohru.context;

import cn.vtohru.annotation.ScopeRequires;
import io.micronaut.context.BeanContext;
import io.micronaut.context.condition.Condition;
import io.micronaut.context.condition.ConditionContext;
import io.micronaut.core.annotation.AnnotationMetadata;
import io.micronaut.core.annotation.AnnotationMetadataProvider;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.util.StringUtils;

@Introspected
public class ScopeCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context) {
        AnnotationMetadataProvider component = context.getComponent();
        AnnotationMetadata annotationMetadata = component.getAnnotationMetadata();
        if (annotationMetadata.hasDeclaredAnnotation(ScopeRequires.class)) {
            AnnotationValue<ScopeRequires> annotation = annotationMetadata.getAnnotation(ScopeRequires.class);
            String property = annotation.get("property", String.class).orElse("");
            String notEquals = annotation.get("notEquals", String.class).orElse("");
            String equals = annotation.get("equals", String.class).orElse("");
            BeanContext beanContext = context.getBeanContext();
            if (beanContext instanceof VerticleApplicationContext) {
                VerticleApplicationContext verticleApplicationContext = (VerticleApplicationContext) beanContext;
                if (StringUtils.isNotEmpty(property)) {
                    if (notEquals.equals(verticleApplicationContext.getVProperty(property, String.class).orElse(""))) {
                        return false;
                    }
                    if (StringUtils.isNotEmpty(equals) && !equals.equals(verticleApplicationContext.getVProperty(property, String.class).orElse(""))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
