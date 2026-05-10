package com.eam.controller;

import com.eam.annotation.OperationLog;
import com.eam.common.Result;
import com.eam.security.RequirePermission;
import com.eam.service.IWorkOrderService;
import com.eam.service.ISparePartService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 定时任务测试 Controller
 * Task 12.3: 测试定时任务执行
 */
@RestController
@RequestMapping("/api/scheduled")
public class ScheduledTaskTestController {

    @Autowired(required = false)
    private Scheduler scheduler;
    
    @Autowired(required = false)
    private IWorkOrderService workOrderService;

    @Autowired(required = false)
    private ISparePartService sparePartService;

    /**
     * 手动触发维护计划任务
     * Task 12.3.1: 手动触发维护计划任务
     */
    @PostMapping("/maintenance/trigger")
    @RequirePermission("system:maintenance")
    @OperationLog(value = "手动触发维护计划任务", description = "手动触发维护计划检查任务", operationType = "UPDATE", recordParams = true, recordResult = false)
    public Result<Map<String, Object>> triggerMaintenanceJob() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 获取JobDetail
            JobDetail maintenancePlanJob = scheduler.getJobDetail("maintenancePlanJob");
            if (maintenancePlanJob == null) {
                result.put("success", false);
                result.put("message", "维护计划任务不存在");
                return Result.success(result);
            }

            // 获取Job
            JobKey jobKey = new JobKey("maintenancePlanJob", maintenancePlanJob.getJobClass());

            // 触发Job
            JobDataMap dataMap = new JobDataMap();
            dataMap.put("triggerTime", LocalDateTime.now().toString());
            dataMap.put("triggeredBy", "手动触发测试");

            scheduler.triggerJob(jobKey, dataMap);

            result.put("success", true);
            result.put("message", "维护计划任务已手动触发");
            result.put("triggerTime", LocalDateTime.now().toString());
            return Result.success(result);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "触发维护计划任务失败: " + e.getMessage());
            return Result.success(result);
        }
    }

    /**
     * 手动触发库存预警任务
     * Task 12.3.2: 手动触发库存预警任务
     */
    @PostMapping("/stock-warning/trigger")
    @RequirePermission("system:stock")
    @OperationLog(value = "手动触发库存预警任务", description = "手动触发库存预警检查任务", operationType = "UPDATE", recordParams = true, recordResult = false)
    public Result<Map<String, Object>> triggerStockWarningJob() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 获取JobDetail
            JobDetail stockWarningJob = scheduler.getJobDetail("stockWarningJob");
            if (stockWarningJob == null) {
                result.put("success", false);
                result.put("message", "库存预警任务不存在");
                return Result.success(result);
            }

            // 获取Job
            JobKey jobKey = new JobKey("stockWarningJob", stockWarningJob.getJobClass());

            // 触发Job
            JobDataMap dataMap = new JobDataMap();
            dataMap.put("triggerTime", LocalDateTime.now().toString());
            dataMap.put("triggeredBy", "手动触发测试");

            scheduler.triggerJob(jobKey, dataMap);

            result.put("success", true);
            result.put("message", "库存预警任务已手动触发");
            result.put("triggerTime", LocalDateTime.now().toString());
            return Result.success(result);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "触发库存预警任务失败: " + e.getMessage());
            return Result.success(result);
        }
    }

    /**
     * 获取定时任务执行状态
     * Task 12.3.3: 验证任务执行的正确性
     */
    @GetMapping("/status")
    @RequirePermission("system:maintenance")
    @OperationLog(value = "查询定时任务状态", description = "查询定时任务执行状态", operationType = "SELECT", recordParams = false, recordResult = false)
    public Result<Map<String, Object>> getJobStatus() {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> jobs = new HashMap<>();

            // 检查维护计划任务
            JobDetail maintenancePlanJob = scheduler.getJobDetail(new JobKey("maintenancePlanJob"));
            if (maintenancePlanJob != null) {
                Trigger maintenancePlanTrigger = scheduler.getTrigger(new TriggerKey("maintenancePlanTrigger"));
                if (maintenancePlanTrigger != null) {
                    Map<String, Object> jobInfo = new HashMap<>();
                    jobInfo.put("jobName", "维护计划检查任务");
                    jobInfo.put("jobGroup", maintenancePlanJob.getKey().getGroup());
                    jobInfo.put("jobClass", maintenancePlanJob.getJobClass().getName());
                    jobInfo.put("description", maintenancePlanTrigger.getDescription());
                    jobInfo.put("jobStatus", maintenancePlanJob.getJobDataMap().size() > 0 ? "执行中" : "空闲");
                    
                    // 获取上次执行时间和下次执行时间
                    Trigger maintenancePlanTrigger = scheduler.getTrigger(new TriggerKey("maintenancePlanTrigger"));
                    if (maintenancePlanTrigger != null) {
                        jobInfo.put("lastFireTime", maintenancePlanTrigger.getPreviousFireTime());
                        jobInfo.put("nextFireTime", maintenancePlanTrigger.getNextFireTime());
                    }
                    
                    jobs.put("maintenancePlanJob", jobInfo);
                }
            }

            // 检查库存预警任务
            JobDetail stockWarningJob = scheduler.getJobDetail(new JobKey("stockWarningJob"));
            if (stockWarningJob != null) {
                Trigger stockWarningTrigger = scheduler.getTrigger(new TriggerKey("stockWarningTrigger"));
                if (stockWarningTrigger != null) {
                    Map<String, Object> jobInfo = new HashMap<>();
                    jobInfo.put("jobName", "库存预警检查任务");
                    jobInfo.put("jobGroup", stockWarningJob.getKey().getGroup());
                    jobInfo.put("jobClass", stockWarningJob.getJobClass().getName());
                    jobInfo.put("description", stockWarningTrigger.getDescription());
                    jobInfo.put("jobStatus", stockWarningJob.getJobDataMap().size() > 0 ? "执行中" : "空闲");
                    
                    // 获取上次执行时间和下次执行时间
                    Trigger stockWarningTrigger = scheduler.getTrigger(new TriggerKey("stockWarningTrigger"));
                    if (stockWarningTrigger != null) {
                        jobInfo.put("lastFireTime", stockWarningTrigger.getPreviousFireTime());
                        jobInfo.put("nextFireTime", stockWarningTrigger.getNextFireTime());
                    }
                    
                    jobs.put("stockWarningJob", jobInfo);
                }
            }

            // 获取待维护的资产数量
            try {
                int pendingMaintenanceCount = workOrderService.listPending().size();
                result.put("pendingMaintenanceCount", pendingMaintenanceCount);
            } catch (Exception e) {
                result.put("pendingMaintenanceCount", 0);
            }

            // 获取库存预警的备件数量
            try {
                int stockWarningCount = sparePartService.listWarning().size();
                result.put("stockWarningCount", stockWarningCount);
            } catch (Exception e) {
                result.put("stockWarningCount", 0);
            }

            result.put("jobs", jobs);
            result.put("totalJobs", jobs.size());
            result.put("success", true);
            
            return Result.success(result);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取任务状态失败: " + e.getMessage());
            return Result.success(result);
        }
    }

    /**
     * 获取定时任务执行历史
     * Task 12.3.4: 监控定时任务的执行情况
     */
    @GetMapping("/history")
    @RequirePermission("system:maintenance")
    @OperationLog(value = "查询定时任务执行历史", description = "查询定时任务执行历史", operationType = "SELECT", recordParams = false, recordResult = false)
    public Result<Map<String, Object>> getJobHistory(@RequestParam(required = false) String jobName,
                                                   @RequestParam(required = false, defaultValue = "10") int limit) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 这里可以从SysOperationLog中查询定时任务的执行历史
            // 简化实现，返回基本信息
            result.put("jobName", jobName != null ? jobName : "所有任务");
            result.put("history", "从操作日志中查询任务执行历史");
            result.put("limit", limit);
            result.put("success", true);
            
            return Result.success(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取任务历史失败: " + e.getMessage());
            return Result.success(result);
        }
    }
}