package com.eam.security;

import com.eam.common.BusinessException;
import com.eam.entity.SysUser;
import com.eam.service.ISysUserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 权限检查切面
 */
@Aspect
@Component
public class PermissionAspect {

    @Autowired
    private ISysUserService userService;

    /**
     * 环绕通知：执行权限检查
     */
    @Around("@annotation(com.eam.security.RequirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        
        // 获取当前用户
        SysUser currentUser = getCurrentUser(request);
        if (currentUser == null) {
            throw new BusinessException("用户未登录，请先登录");
        }
        
        // 检查是否为管理员（管理员拥有所有权限）
        if (isAdmin(currentUser)) {
            return joinPoint.proceed();
        }
        
        // 获取注解
        RequirePermission requirePermission = getRequirePermissionAnnotation(joinPoint);
        if (requirePermission == null) {
            return joinPoint.proceed();
        }
        
        // 获取需要的权限
        String requiredPermission = requirePermission.value();
        if (requiredPermission == null || requiredPermission.trim().isEmpty()) {
            return joinPoint.proceed();
        }
        
        // 检查权限
        boolean hasPermission = checkUserPermission(currentUser, requiredPermission);
        if (!hasPermission) {
            throw new BusinessException("权限不足，无权限访问该资源。需要权限：" + requiredPermission);
        }
        
        // 权限验证通过，执行目标方法
        return joinPoint.proceed();
    }

    /**
     * 获取当前登录用户
     */
    private SysUser getCurrentUser(HttpServletRequest request) {
        try {
            String username = request.getHeader("username");
            if (username != null && !username.trim().isEmpty()) {
                return userService.getByUsername(username);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 检查是否为管理员
     */
    private boolean isAdmin(SysUser user) {
        if (user == null || user.getRoleId() == null) {
            return false;
        }
        // 管理员角色ID为1
        return user.getRoleId().equals(1L);
    }

    /**
     * 检查用户是否拥有指定权限
     */
    private boolean checkUserPermission(SysUser user, String permission) {
        if (user == null || permission == null) {
            return false;
        }
        
        try {
            // 调用UserService检查权限
            return userService.hasPermission(user.getId(), permission);
        } catch (Exception e) {
            // 权限检查异常，默认拒绝
            System.err.println("权限检查异常: " + e.getMessage());
            return false;
        }
    }

    /**
     * 获取方法上的RequirePermission注解
     */
    private RequirePermission getRequirePermissionAnnotation(ProceedingJoinPoint joinPoint) {
        try {
            // 获取目标类
            Class<?> targetClass = joinPoint.getTarget().getClass();
            
            // 获取方法名称
            String methodName = joinPoint.getSignature().getName();
            Class<?>[] parameterTypes = ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getParameterTypes();
            
            // 获取方法
            Method method = targetClass.getMethod(methodName, parameterTypes);
            
            // 获取方法上的注解
            return method.getAnnotation(RequirePermission.class);
        } catch (Exception e) {
            return null;
        }
    }
}