package com.eam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志表 Entity
 * 记录用户操作行为，用于审计和安全追踪
 */
@Data
@TableName("sys_operation_log")
public class SysOperationLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 操作用户ID
     */
    private Long userId;

    /**
     * 操作用户名
     */
    private String username;

    /**
     * 操作类型(新增/修改/删除/查询等)
     */
    private String operation;

    /**
     * 操作模块
     */
    private String module;

    /**
     * 请求方法(类名+方法名)
     */
    private String method;

    /**
     * 请求参数(JSON格式)
     */
    private String params;

    /**
     * 耗时(毫秒)
     */
    private Long timeConsuming;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 操作结果(SUCCESS/FAIL)
     */
    private String result;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 操作时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
