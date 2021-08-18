package cn.vtohru.context;

import cn.vtohru.annotation.ScopeRequires;
import io.micronaut.context.BeanContext;
import io.micronaut.context.condition.Condition;
import io.micronaut.context.condition.ConditionContext;
import io.micronaut.core.annotation.AnnotationMetadata;
import io.micronaut.core.annotation.AnnotationMetadataProvider;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.Introspected;

import java.util.Arrays;

@Introspected
public class ScopeCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context) {
        AnnotationMetadataProvider component = context.getComponent();
        AnnotationMetadata annotationMetadata = component.getAnnotationMetadata();
        if (annotationMetadata.hasDeclaredAnnotation(ScopeRequires.class)) {
            AnnotationValue<ScopeRequires> annotation = annotationMetadata.getAnnotation(ScopeRequires.class);
            String[] requireVerticles = annotation.get("requireVerticle", String[].class).orElse(new String[]{});
            String[] notRequireVerticles = annotation.get("notRequireVerticle", String[].class).orElse(new String[]{});
            BeanContext beanContext = context.getBeanContext();
            if (beanContext instanceof VerticleApplicationContext) {
                VerticleApplicationContext verticleApplicationContext = (VerticleApplicationContext) beanContext;
                String scopeName = verticleApplicationContext.getScopeName();
                if (Arrays.stream(requireVerticles).noneMatch(x -> x.equalsIgnoreCase(scopeName))) {
                    return false;
                }
                if (Arrays.stream(notRequireVerticles).anyMatch(x -> x.equalsIgnoreCase(scopeName))) {
                    return false;
                }
            }
        }
        return true;
    }
}
