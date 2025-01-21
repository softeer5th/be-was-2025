package webserver.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Cookie {
    String name();
    boolean required() default true;
    boolean authenticated() default false;
}
