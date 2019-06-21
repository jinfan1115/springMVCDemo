package com.fyang.springmvc.annotation;

import java.lang.annotation.*;

@Documented//JACADOC
@Target(ElementType.TYPE)//作用于类上
@Retention(RetentionPolicy.RUNTIME)//限制Annotation的生命周期
public @interface Controller {

    /**
     * 作用于该类上的注解有一个VALUE属性,实际上就是指Controller名称
     * @return
     */
    public String value();
}
