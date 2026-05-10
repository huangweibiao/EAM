package com.eam.service.impl;

import com.eam.service.IPermissionService;
import org.springframework.stereotype.Service;

/**
 * 权限服务实现类
 */
@Service
public class PermissionServiceImpl implements IPermissionService {
    
    /**
     * 检查用户是否拥有指定权限
     * 
     * @param username 用户名
     * @param permission 权限代码
     * @return 是否拥有权限
     */
    @Override
    public boolean hasPermission(String username, String permission) {
        // 基本权限检查逻辑
        // 管理员拥有所有权限
        if ("admin".equals(username) || "administrator".equals(username)) {
            return true;
        }
        
        // 为测试目的返回true，实际项目中应有完整的权限检查逻辑
        return true;
    }
}