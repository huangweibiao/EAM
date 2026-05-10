package com.eam.service;

import com.eam.entity.MaintenancePlan;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 维护计划 Service 接口
 */
public interface IMaintenancePlanService {

    Page<MaintenancePlan> page(Long pageNum, Long pageSize, Long assetId, String status);

    MaintenancePlan add(MaintenancePlan plan);

    MaintenancePlan update(MaintenancePlan plan);

    boolean delete(Long id);

    List<MaintenancePlan> listPending();

    List<MaintenancePlan> listExpiring(int days);

    List<MaintenancePlan> list();

    MaintenancePlan getById(Long id);
}