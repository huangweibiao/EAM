package com.eam.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.eam.entity.SysOperationLog;

/**
 * 操作日志 Service接口
 */
public interface ISysOperationLogService extends IServiceService<SysOperationLog> {

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
    IPagePage<SysOperationLog> page(Integer pageNum, Integer pageSize, String username, String operation, String module);
}
