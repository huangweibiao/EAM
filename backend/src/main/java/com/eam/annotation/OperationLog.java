package com.eam.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 用于标记需要记录操作日志的方法
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /**
     * 操作名称（默认为方法名）
     */
    String value() default "";

    /**
     * 操作类型
     */
    String operationType() default "OTHER";

    /**
     * 操作描述
     */
    String description() default "";

    /**
     * 是否记录参数
     */
    boolean recordParams() default false;

    /**
     * 是否记录结果
     */
    boolean recordResult() default true;
}