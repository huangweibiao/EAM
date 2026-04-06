package com.eam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eam.common.BusinessException;
import com.eam.entity.MaintenancePlan;
import com.eam.mapper.MaintenancePlanMapper;
import com.eam.service.IMaintenancePlanService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 维护计划 Service 实现类
 */
@Service
public class MaintenancePlanServiceImpl extends ServiceImpl<MaintenancePlanMapper, MaintenancePlan> implements IMaintenancePlanService {

    @Override
    public IPage<MaintenancePlan> page(Long pageNum, Long pageSize, Long assetId, String status) {
        Page<MaintenancePlan> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<MaintenancePlan> wrapper = new LambdaQueryWrapper<>();
        if (assetId != null) {
            wrapper.eq(MaintenancePlan::getAssetId, assetId);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(MaintenancePlan::getStatus, status);
        }
        wrapper.orderByDesc(MaintenancePlan::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public MaintenancePlan add(MaintenancePlan plan) {
        // 生成计划编码
        String planCode = "MP" + System.currentTimeMillis();
        plan.setPlanCode(planCode);
        if (plan.getStatus() == null) {
            plan.setStatus("ACTIVE");
        }
        if (plan.getCreateTime() == null) {
            plan.setCreateTime(LocalDateTime.now());
        }

        // 计算下次执行时间
        calculateNextExecuteTime(plan);

        this.save(plan);
        return plan;
    }

    @Override
    public MaintenancePlan update(MaintenancePlan plan) {
        if (plan.getId() == null) {
            throw new BusinessException("计划ID不能为空");
        }
        // 重新计算下次执行时间
        calculateNextExecuteTime(plan);
        this.updateById(plan);
        return plan;
    }

    @Override
    public boolean delete(Long id) {
        return this.removeById(id);
    }

    @Override
    public List<MaintenancePlan> listPending() {
        return this.list(new LambdaQueryWrapper<MaintenancePlan>()
                .eq(MaintenancePlan::getStatus, "ACTIVE")
                .orderByAsc(MaintenancePlan::getNextExecuteTime));
    }

    @Override
    public List<MaintenancePlan> listExpiring(int days) {
        LocalDateTime expiringDate = LocalDateTime.now().plusDays(days);
        return this.list(new LambdaQueryWrapper<MaintenancePlan>()
                .eq(MaintenancePlan::getStatus, "ACTIVE")
                .le(MaintenancePlan::getNextExecuteTime, expiringDate)
                .orderByAsc(MaintenancePlan::getNextExecuteTime));
    }

    private void calculateNextExecuteTime(MaintenancePlan plan) {
        if (plan.getCycleType() != null && plan.getCycleValue() != null) {
            LocalDateTime baseTime = plan.getLastExecuteTime() != null ?
                    plan.getLastExecuteTime() : LocalDateTime.now();

            LocalDateTime nextTime;
            switch (plan.getCycleType()) {
                case "DAY":
                    nextTime = baseTime.plusDays(plan.getCycleValue());
                    break;
                case "MONTH":
                    nextTime = baseTime.plusMonths(plan.getCycleValue());
                    break;
                case "YEAR":
                    nextTime = baseTime.plusYears(plan.getCycleValue());
                    break;
                default:
                    nextTime = baseTime.plusDays(30);
            }
            plan.setNextExecuteTime(nextTime);
        }
    }
}