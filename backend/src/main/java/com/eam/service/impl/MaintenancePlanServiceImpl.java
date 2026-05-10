package com.eam.service.impl;

import com.eam.common.BusinessException;
import com.eam.entity.MaintenancePlan;
import com.eam.repository.MaintenancePlanRepository;
import com.eam.service.IMaintenancePlanService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 维护计划 Service 实现类
 */
@Service
public class MaintenancePlanServiceImpl implements IMaintenancePlanService {

    private final MaintenancePlanRepository maintenancePlanRepository;

    public MaintenancePlanServiceImpl(MaintenancePlanRepository maintenancePlanRepository) {
        this.maintenancePlanRepository = maintenancePlanRepository;
    }

    @Override
    public Page<MaintenancePlan> page(Long pageNum, Long pageSize, Long assetId, String status) {
        // JPA 分页从 0 开始，MyBatis-Plus 从 1 开始，需要转换
        Pageable pageable = PageRequest.of(pageNum.intValue() - 1, pageSize.intValue(),
                Sort.by(Sort.Direction.DESC, "createTime"));

        Specification<MaintenancePlan> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (assetId != null) {
                predicates.add(cb.equal(root.get("assetId"), assetId));
            }
            if (StringUtils.hasText(status)) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return maintenancePlanRepository.findAll(spec, pageable);
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

        return maintenancePlanRepository.save(plan);
    }

    @Override
    public MaintenancePlan update(MaintenancePlan plan) {
        if (plan.getId() == null) {
            throw new BusinessException("计划ID不能为空");
        }
        // 重新计算下次执行时间
        calculateNextExecuteTime(plan);
        return maintenancePlanRepository.save(plan);
    }

    @Override
    public boolean delete(Long id) {
        maintenancePlanRepository.deleteById(id);
        return true;
    }

    @Override
    public List<MaintenancePlan> listPending() {
        return maintenancePlanRepository.findByStatusOrderByNextExecuteTimeAsc("ACTIVE");
    }

    @Override
    public List<MaintenancePlan> listExpiring(int days) {
        LocalDateTime expiringDate = LocalDateTime.now().plusDays(days);
        return maintenancePlanRepository.findExpiringPlans("ACTIVE", expiringDate);
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

    @Override
    public List<MaintenancePlan> list() {
        return maintenancePlanRepository.findAll();
    }

    @Override
    public MaintenancePlan getById(Long id) {
        return maintenancePlanRepository.findById(id).orElse(null);
    }
}