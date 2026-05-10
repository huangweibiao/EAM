package com.eam.service;

/**
 * 权限服务接口
 */
public interface IPermissionService {
    
    /**
     * 检查用户是否拥有指定权限
     * 
     * @param username 用户名
     * @param permission 权限代码
     * @return 是否拥有权限
     */
    boolean hasPermission(String username, String permission);
    
    /**
     * 检查用户是否拥有所有权限
     * 
     * @param username 用户名
     * @param permissions 权限代码数组
     * @return 是否拥有所有权限
     */
    default boolean hasAllPermissions(String username, String... permissions) {
        for (String permission : permissions) {
            if (!hasPermission(username, permission)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 检查用户是否拥有任意权限
     * 
     * @param username 用户名
     * @param permissions 权限代码数组
     * @return 是否拥有任意权限
     */
    default boolean hasAnyPermission(String username, String... permissions) {
        for (String permission : permissions) {
            if (hasPermission(username, permission)) {
                return true;
            }
        }
        return false;
    }
}