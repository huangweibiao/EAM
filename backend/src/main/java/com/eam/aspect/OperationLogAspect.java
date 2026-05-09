package com.eam.aspect;

import com.alibaba.fastjson.JSON;
import com.eam.annotation.OperationLog;
import com.eam.entity.SysOperationLog;
import com.eam.service.ISysOperationLogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 操作日志切面
 * 拦截带有@OperationLog注解的方法，记录操作日志
 */
@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private ISysOperationLogService operationLogService;

    /**
     * 定义切点：拦截带有@OperationLog注解的方法
     */
    @Pointcut("@annotation(com.eam.annotation.OperationLog)")
    public void operationLogPointCut() {
    }

    /**
     * 环绕通知：记录操作日志
     */
    @Around("operationLogPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取方法信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperationLog operationLogAnnotation = method.getAnnotation(OperationLog.class);

        // 获取请求信息
        HttpServletRequest request = getRequest();

        // 执行目标方法
        Object result = null;
        String resultStatus = "SUCCESS";
        String errorMsg = null;

        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            resultStatus = "FAIL";
            errorMsg = e.getMessage();
            throw e;
        } finally {
            // 计算耗时
            long timeConsuming = System.currentTimeMillis() - startTime;

            // 保存操作日志
            saveOperationLog(joinPoint, operationLogAnnotation, request, timeConsuming, resultStatus, errorMsg);
        }

        return result;
    }

    /**
     * 异常通知：记录异常信息
     */
    @AfterThrowing(pointcut = "operationLogPointCut()", throwing = "e")
    public void afterThrowing(JoinPoint joinPoint, Exception e) {
        // 异常信息已在around中处理
    }

    /**
     * 保存操作日志
     */
    private void saveOperationLog(ProceedingJoinPoint joinPoint, OperationLog operationLogAnnotation,
                                  HttpServletRequest request, long timeConsuming, String result, String errorMsg) {
        try {
            SysOperationLog log = new SysOperationLog();

            // 获取当前用户信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                String username = authentication.getName();
                log.setUsername(username);
                // TODO: 根据用户名查询userId
            }

            // 设置操作信息
            log.setOperation(operationLogAnnotation.operation());
            log.setModule(operationLogAnnotation.module());

            // 设置方法信息
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = signature.getName();
            log.setMethod(className + "." + methodName + "()");

            // 设置请求参数
            if (operationLogAnnotation.recordParams()) {
                Object[] args = joinPoint.getArgs();
                if (args != null && args.length > 0) {
                    try {
                        String params = JSON.toJSONString(args);
                        // 限制参数长度
                        if (params.length() > 2000) {
                            params = params.substring(0, 2000) + "...";
                        }
                        log.setParams(params);
                    } catch (Exception e) {
                        log.setParams("参数序列化失败");
                    }
                }
            }

            // 设置IP地址
            log.setIp(getIpAddress(request));

            // 设置耗时和结果
            log.setTimeConsuming(timeConsuming);
            log.setResult(result);
            log.setErrorMsg(errorMsg);

            // 设置创建时间
            log.setCreateTime(LocalDateTime.now());

            // 异步保存日志
            operationLogService.saveLog(log);
        } catch (Exception e) {
            // 日志记录失败不影响主业务
            e.printStackTrace();
        }
    }

    /**
     * 获取HttpServletRequest
     */
    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return attributes.getRequest();
        }
        return null;
    }

    /**
     * 获取IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 多个代理情况，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }
}
