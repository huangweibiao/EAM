package com.eam.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.eam.entity.MaintenancePlan;

import java.util.List;

/**
 * 维护计划 Service 接口
 */
public interface IMaintenancePlanService extends IService<MaintenancePlan> {

    IPage<MaintenancePlan> page(Long pageNum, Long pageSize, Long assetId, String status);

    MaintenancePlan add(MaintenancePlan plan);

    MaintenancePlan update(MaintenancePlan plan);

    boolean delete(Long id);

    List<MaintenancePlan> listPending();

    List<MaintenancePlan> listExpiring(int days);
}