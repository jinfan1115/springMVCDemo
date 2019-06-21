package com.fyang.springmvc.annotation;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD,ElementType.TYPE})//该注解可用于类跟方法上
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    public String value();
}
