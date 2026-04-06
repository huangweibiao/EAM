package com.eam.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.eam.entity.WorkOrder;

/**
 * 工单 Service 接口
 */
public interface IWorkOrderService extends IService<WorkOrder> {

    IPage<WorkOrder> page(Long pageNum, Long pageSize, String keyword, String status, String orderType);

    WorkOrder create(WorkOrder workOrder);

    WorkOrder assign(Long id, String assignedTo);

    WorkOrder process(Long id);

    WorkOrder complete(Long id, String solution);

    WorkOrder close(Long id);

    WorkOrder rate(Long id, Integer rating);
}