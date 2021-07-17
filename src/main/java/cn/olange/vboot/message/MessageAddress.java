package cn.olange.vboot.message;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target({METHOD, FIELD})
public @interface MessageAddress {
    String value();
    MessageType.Type type() default MessageType.Type.PUBLISH;
}
