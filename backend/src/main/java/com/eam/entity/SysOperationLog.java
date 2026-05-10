package com.eam.entity;

import jakarta.persistence.*;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.LocalDateTime;

/**
 * 操作日志表 Entity
 * 记录用户操作行为，用于审计和安全追踪
 */
@Entity
@Table(name = "eam_sys_operation_log")
public class SysOperationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 操作用户ID
     */
    @Column(name = "user_id")
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
    @Column(name = "time_consuming")
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
    @Column(name = "error_msg")
    private String errorMsg;

    /**
     * 操作时间
     */
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @PrePersist
    public void onCreate() {
        this.createTime = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public Long getTimeConsuming() {
        return timeConsuming;
    }

    public void setTimeConsuming(Long timeConsuming) {
        this.timeConsuming = timeConsuming;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
