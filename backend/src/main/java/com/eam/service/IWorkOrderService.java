package com.eam.service;

import com.eam.entity.WorkOrder;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 工单 Service 接口
 */
public interface IWorkOrderService {

    Page<WorkOrder> page(Long pageNum, Long pageSize, String keyword, String status, String orderType);

    WorkOrder create(WorkOrder workOrder);

    WorkOrder assign(Long id, String assignedTo);

    WorkOrder process(Long id);

    WorkOrder complete(Long id, String solution);

    WorkOrder close(Long id);

    WorkOrder getById(Long id);

    List<WorkOrder> list();

    WorkOrder rate(Long id, Integer rating);
}