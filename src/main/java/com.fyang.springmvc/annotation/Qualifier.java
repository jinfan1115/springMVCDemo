package com.fyang.springmvc.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)//作用于字段上，实现注入
@Retention(RetentionPolicy.RUNTIME)
public @interface Qualifier {

    public String value();
}
