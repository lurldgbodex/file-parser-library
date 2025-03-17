package com.github.lurldgbodex.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ValidationRule {
    boolean required() default false;
    String regex() default "";
    int minLength() default 0;
    int maxLength() default Integer.MAX_VALUE;
}
