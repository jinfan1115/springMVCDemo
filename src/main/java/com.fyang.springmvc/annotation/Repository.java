package com.fyang.springmvc.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
/**
 * dao层注解
 */
public @interface Repository {
    public String value();
}
