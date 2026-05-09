package com.eam.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 用于标记需要记录操作日志的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /**
     * 操作类型
     * 例如：新增、修改、删除、查询、导出等
     */
    String operation() default "";

    /**
     * 操作模块
     * 例如：资产管理、用户管理、系统管理等
     */
    String module() default "";

    /**
     * 是否记录请求参数
     */
    boolean recordParams() default true;
}
