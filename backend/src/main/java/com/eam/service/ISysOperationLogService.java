package com.eam.service;

import com.eam.entity.SysOperationLog;
import org.springframework.data.domain.Page;

/**
 * 操作日志 Service 接口
 */
public interface ISysOperationLogService {

    /**
     * 记录操作日志
     *
     * @param log 日志对象
     */
    void saveLog(SysOperationLog log);

    /**
     * 分页查询操作日志
     *
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param username 用户名
     * @param operation 操作类型
     * @param module 操作模块
     * @return 分页结果
     */
    Page<SysOperationLog> page(Integer pageNum, Integer pageSize, String username, String operation, String module);

    /**
     * 根据ID获取操作日志
     */
    SysOperationLog getById(Long id);

    /**
     * 根据用户ID查询操作日志
     */
    java.util.List<SysOperationLog> getByUserId(Long userId);

    /**
     * 删除操作日志
     */
    boolean removeById(Long id);

    /**
     * 批量删除操作日志
     */
    boolean removeByIds(java.util.List<Long> ids);
}