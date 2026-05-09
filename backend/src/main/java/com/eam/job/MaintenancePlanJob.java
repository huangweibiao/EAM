package com.eam.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eam.entity.MaintenancePlan;
import com.eam.entity.WorkOrder;
import com.eam.mapper.MaintenancePlanMapper;
import com.eam.service.IWorkOrderService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 维护计划定时任务
 * 每日检查到期维护计划，自动生成对应工单
 */
@Slf4j
@Component
public class MaintenancePlanJob implements Job {

    @Autowired
    private MaintenancePlanMapper maintenancePlanMapper;

    @Autowired
    private IWorkOrderService workOrderService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("开始执行维护计划定时任务...");

        // 查询所有活跃的、下次执行时间已到的维护计划
        LocalDateTime now = LocalDateTime.now();
        List List<MaintenancePlan> plans = maintenancePlanMapper.selectList(
                new LambdaQueryWrapperWrapper<MaintenancePlan>()
                        .eq(MaintenancePlan::getStatus, "ACTIVE")
                        .le(MaintenancePlan::getNextExecuteTime, now)
        );

        log.info("找到 {} 个到期的维护计划", plans.size());

        for (MaintenancePlan plan : plans) {
            try {
                createWorkOrder(plan);
            } catch (Exception e) {
                log.error("为维护计划 {} 创建工单失败", plan.getId(), e);
            }
        }

        log.info("维护计划定时任务执行完成");
    }

    /**
     * 为维护计划创建工单
     */
    private void createWorkOrder(MaintenancePlan plan) {
        // 检查是否已存在未完成的工单
        // 这里简化处理，直接创建新工单

        WorkOrder workOrder = new WorkOrder();
        workOrder.setAssetId(plan.getAssetId());
        workOrder.setOrderType("MAINTENANCE");
        workOrder.setPriority("MEDIUM");
        workOrder.setTitle("定期维护: " + plan.getPlanName());
        workOrder.setDescription("根据维护计划 " + plan.getPlanCode() + " 自动生成的维护工单");
        workOrder.setReporter("SYSTEM");
        workOrder.setStatus("PENDING");

        workOrderService.create(workOrder);

        log.info("为维护计划 {} 创建工单成功", plan.getId());
    }
}
