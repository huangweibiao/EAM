package com.eam.service.impl;

import com.eam.entity.WorkOrderPart;
import com.eam.repository.WorkOrderPartRepository;
import com.eam.service.IWorkOrderPartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 工单-备件关联 Service 实现类
 */
@Service
public class WorkOrderPartServiceImpl implements IWorkOrderPartService {

    @Autowired
    private WorkOrderPartRepository workOrderPartRepository;

    @Override
    public Page<WorkOrderPart> page(Long workOrderId, Pageable pageable) {
        if (workOrderId != null) {
            return workOrderPartRepository.findByWorkOrderId(workOrderId, pageable);
        }
        return workOrderPartRepository.findAll(pageable);
    }

    @Override
    public List<WorkOrderPart> listByWorkOrderId(Long workOrderId) {
        return workOrderPartRepository.findByWorkOrderId(workOrderId);
    }

    @Override
    public List<WorkOrderPart> listByPartId(Long partId) {
        return workOrderPartRepository.findByPartId(partId);
    }

    @Override
    @Transactional
    public WorkOrderPart add(WorkOrderPart workOrderPart) {
        return workOrderPartRepository.save(workOrderPart);
    }

    @Override
    @Transactional
    public WorkOrderPart update(WorkOrderPart workOrderPart) {
        return workOrderPartRepository.save(workOrderPart);
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        try {
            workOrderPartRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteByWorkOrderAndPart(Long workOrderId, Long partId) {
        try {
            workOrderPartRepository.deleteByWorkOrderIdAndPartId(workOrderId, partId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public WorkOrderPart updateActualQuantity(Long id, BigDecimal actualQuantity) {
        WorkOrderPart workOrderPart = workOrderPartRepository.findById(id).orElse(null);
        if (workOrderPart != null) {
            workOrderPart.setActualQuantity(actualQuantity);
            return workOrderPartRepository.save(workOrderPart);
        }
        return null;
    }

    @Override
    @Transactional
    public List<WorkOrderPart> addBatch(List<WorkOrderPart> workOrderParts) {
        return workOrderPartRepository.saveAll(workOrderParts);
    }
}