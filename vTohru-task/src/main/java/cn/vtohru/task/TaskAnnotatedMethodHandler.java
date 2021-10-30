package cn.vtohru.task;

import cn.vtohru.task.annotation.Task;
import io.micronaut.context.processor.ExecutableMethodProcessor;
import io.micronaut.core.annotation.Indexed;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.ExecutableMethod;

import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Singleton
@Indexed(TaskAnnotatedMethodHandler.class)
public class TaskAnnotatedMethodHandler implements ExecutableMethodProcessor<Task> {
    private List<TaskDefinition> taskDefinitionList = new ArrayList<>();

    @Override
    public void process(BeanDefinition<?> beanDefinition, ExecutableMethod<?, ?> method) {
        Optional<Class<? extends Annotation>> actionAnn = method.getAnnotationTypeByStereotype(Task.class);
        actionAnn.ifPresent(annotationClass -> {
            taskDefinitionList.add(new TaskDefinition(beanDefinition, method));
        });
    }

    public final class TaskDefinition {
        private final BeanDefinition<?> beanDefinition;
        private final ExecutableMethod<?,?> executableMethod;
        public TaskDefinition(BeanDefinition<?> beanDefinition, ExecutableMethod<?, ?> executableMethod) {
            this.beanDefinition = beanDefinition;
            this.executableMethod = executableMethod;
        }
        public BeanDefinition<?> getBeanDefinition() {
            return beanDefinition;
        }

        public ExecutableMethod<?, ?> getExecutableMethod() {
            return executableMethod;
        }
    }

    public List<TaskDefinition> getTaskDefinitionList() {
        return taskDefinitionList;
    }
}
