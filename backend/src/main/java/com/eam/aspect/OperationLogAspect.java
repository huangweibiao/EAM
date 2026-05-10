package com.eam.aspect;

import com.eam.annotation.OperationLog;
import com.eam.common.BusinessException;
import com.eam.entity.SysOperationLog;
import com.eam.entity.SysUser;
import com.eam.service.ISysOperationLogService;
import com.eam.service.ISysUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作日志切面
 * Task 6.2: 配置操作日志切面
 */
@Aspect
@Component
public class OperationLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(OperationLogAspect.class);

    @Autowired(required = false)
    private ISysOperationLogService operationLogService;

    @Autowired(required = false)
    private ISysUserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 定义切入点：Controller包下的所有公共方法
     */
    @Pointcut("execution(public * com.eam.controller..*.*(..))")
    public void controllerPointcut() {
    }

    /**
     * 环绕通知：拦截带有@OperationLog注解的方法，记录操作日志
     */
    @Around("@annotation(com.eam.annotation.OperationLog)")
    public Object aroundOperationLog(ProceedingJoinPoint joinPoint) throws Throwable {
        // 记录开始时间
        long startTime = System.currentTimeMillis();
        
        // 获取请求和用户信息
        HttpServletRequest request = getCurrentRequest();
        SysUser currentUser = getCurrentUser(request);
        
        // 获取方法上的@OperationLog注解
        OperationLog operationLogAnnotation = getOperationLogAnnotation(joinPoint);
        
        // 执行目标方法
        Object result = null;
        Throwable exception = null;
        
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            exception = e;
            throw e; // 继续抛出异常，由全局异常处理器处理
        } finally {
            // 如果方法上有@OperationLog注解，记录日志
            if (operationLogAnnotation != null) {
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                
                try {
                    saveOperationLog(request, currentUser, joinPoint, operationLogAnnotation, 
                                      result, exception, duration);
                } catch (Exception logException) {
                    // 日志记录异常不影响业务流程
                    logger.error("记录操作日志失败: {}", logException.getMessage(), logException);
                }
            }
        }
    }

    /**
     * 保存操作日志
     * Task 6.2.1: 完善OperationLogAspect切面实现
     * Task 6.2.2: 实现日志信息收集逻辑
     * Task 6.2.3: 实现操作耗时统计
     * Task 6.2.4: 实现异常信息记录
     */
    private void saveOperationLog(HttpServletRequest request, SysUser currentUser,
                               ProceedingJoinPoint joinPoint, OperationLog annotation,
                               Object result, Throwable exception, long duration) {
        try {
            // 获取方法信息
            String className = joinPoint.getTarget().getClass().getSimpleName();
            String methodName = joinPoint.getSignature().getName();
            
            // 获取操作信息
            String operationName = annotation.value().isEmpty() ? methodName : annotation.value();
            String operationType = annotation.operationType();
            String description = annotation.description().isEmpty() ? operationName : annotation.description();
            
            // 获取请求参数
            String params = annotation.recordParams() ? getRequestParams(joinPoint) : "{}";
            
            // 获取结果
            String resultStr = annotation.recordResult() ? getResultString(result) : "{}";
            
            // 获取异常信息
            String exceptionInfo = exception != null ? getExceptionInfo(exception) : null;
            
            // 构建操作日志实体
            SysOperationLog operationLog = new SysOperationLog();
            operationLog.setOperation(operationName);
            operationLog.setMethod(className + "." + methodName);
            operationLog.setParams(params);
            operationLog.setResult(resultStr);
            operationLog.setErrorMsg(exceptionInfo);
            operationLog.setTimeConsuming(duration);
            
            // 设置用户信息
            if (currentUser != null) {
                operationLog.setUsername(currentUser.getUsername());
                operationLog.setUserId(currentUser.getId());
            } else {
                operationLog.setUsername("anonymous");
                operationLog.setUserId(null);
            }
            
            // 设置IP地址
            operationLog.setIp(getClientIp(request));
            
            // 设置操作时间
            operationLog.setCreateTime(LocalDateTime.now());
            
            // 保存日志
            operationLogService.save(operationLog);
            
            logger.info("操作日志已记录: 用户={}, 操作={}, 耗时={}ms", 
                       operationLog.getUsername(), operationName, duration);
                       
        } catch (Exception e) {
            logger.error("保存操作日志失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 获取请求参数
     */
    private String getRequestParams(ProceedingJoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            Map<String, Object> paramMap = new HashMap<>();
            
            // 提取方法参数（简化实现，实际中可以根据需要提取特定参数）
            if (args != null && args.length > 0) {
                String[] paramNames = ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getParameterNames();
                for (int i = 0; i < args.length; i++) {
                    if (args[i] != null) {
                        // 简化参数处理，避免序列化问题
                        paramMap.put(paramNames[i], args[i].getClass().getSimpleName() + ":" + args[i]);
                    }
                }
            }
            
            return objectMapper.writeValueAsString(paramMap);
        } catch (Exception e) {
            logger.error("获取请求参数失败: {}", e.getMessage());
            return "{}";
        }
    }

    /**
     * 获取结果字符串
     */
    private String getResultString(Object result) {
        try {
            if (result == null) {
                return "null";
            }
            return objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            logger.error("序列化结果失败: {}", e.getMessage());
            return result.toString();
        }
    }

    /**
     * 获取异常信息
     */
    private String getExceptionInfo(Throwable exception) {
        try {
            Map<String, Object> exceptionMap = new HashMap<>();
            exceptionMap.put("exceptionType", exception.getClass().getSimpleName());
            exceptionMap.put("message", exception.getMessage());
            exceptionMap.put("stackTrace", getStackTrace(exception));
            return objectMapper.writeValueAsString(exceptionMap);
        } catch (Exception e) {
            logger.error("序列化异常信息失败: {}", e.getMessage());
            return "{\"message\": \"" + exception.getMessage() + "\"}";
        }
    }

    /**
     * 获取堆栈跟踪信息（简化版）
     */
    private String getStackTrace(Throwable exception) {
        // 只记录前10行堆栈，避免日志过大
        StringBuilder stackTrace = new StringBuilder();
        StackTraceElement[] stackTraceElements = exception.getStackTrace();
        int maxLines = Math.min(stackTraceElements.length, 10);
        
        for (int i = 0; i < maxLines; i++) {
            stackTrace.append(stackTraceElements[i].toString()).append("\n");
        }
        
        return stackTrace.toString();
    }

    /**
     * 获取当前HTTP请求
     */
    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes.getRequest();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前登录用户
     */
    private SysUser getCurrentUser(HttpServletRequest request) {
        try {
            if (request == null) {
                return null;
            }
            
            // 从请求头获取用户信息（假设有认证信息）
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
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // 处理多个IP的情况（X-Forwarded-For可能包含多个IP）
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }

    /**
     * 获取方法上的@OperationLog注解
     */
    private OperationLog getOperationLogAnnotation(ProceedingJoinPoint joinPoint) {
        try {
            // 获取目标类
            Class<?> targetClass = joinPoint.getTarget().getClass();
            
            // 获取方法名称
            String methodName = joinPoint.getSignature().getName();
            Class<?>[] parameterTypes = ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getParameterTypes();
            
            // 获取方法
            Method method = targetClass.getMethod(methodName, parameterTypes);
            
            // 获取方法上的注解
            return method.getAnnotation(OperationLog.class);
        } catch (Exception e) {
            logger.error("获取@OperationLog注解失败: {}", e.getMessage());
            return null;
        }
    }
}