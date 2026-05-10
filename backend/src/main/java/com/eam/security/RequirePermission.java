package com.eam.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义权限注解
 * 用于方法级别的权限控制
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {

    /**
     * 权限编码
     */
    String value();

    /**
     * 权限名称
     */
    String name() default "";

    /**
     * 权限描述
     */
    String desc() default "";

    /**
     * 权限类型：MENU/BUTTON/API
     */
    String type() default "BUTTON";
}