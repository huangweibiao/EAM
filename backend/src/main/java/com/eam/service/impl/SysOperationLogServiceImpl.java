package com.eam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eam.entity.SysOperationLog;
import com.eam.mapper.SysOperationLogMapper;
import com.eam.service.ISysOperationLogService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 操作日志 Service实现
 */
@Service
public class SysOperationLogServiceImpl extends ServiceImpl<SysOperationLogMapper, SysOperationLog> implements ISysOperationLogService {

    @Override
    public void saveLog(SysOperationLog log) {
        this.save(log);
    }

    @Override
    public IPage<SysOperationLog> page(Integer pageNum, Integer pageSize, String username, String operation, String module) {
        Page<SysOperationLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysOperationLog> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(username)) {
            wrapper.like(SysOperationLog::getUsername, username);
        }
        if (StringUtils.hasText(operation)) {
            wrapper.eq(SysOperationLog::getOperation, operation);
        }
        if (StringUtils.hasText(module)) {
            wrapper.like(SysOperationLog::getModule, module);
        }
        
        wrapper.orderByDesc(SysOperationLog::getCreateTime);
        return this.page(page, wrapper);
    }
}
