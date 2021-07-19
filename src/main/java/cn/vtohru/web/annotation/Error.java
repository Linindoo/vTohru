package cn.vtohru.web.annotation;

import io.micronaut.context.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target({ElementType.METHOD})
public @interface Error {
    /**
     * @return The exception to map to
     */
    @AliasFor(member = "exception")
    Class<? extends Throwable> value() default Throwable.class;

    /**
     * @return The exception to map to
     */
    @AliasFor(member = "value")
    Class<? extends Throwable> exception() default Throwable.class;

    int status() default 500;

    /**
     * Whether the error handler should be registered as a global error handler or just locally to the declaring
     * {@link Controller}.
     *
     * @return True if it should be global
     */
    boolean global() default false;
}
