package com.eam.service.impl;

import com.eam.common.BusinessException;
import com.eam.entity.Asset;
import com.eam.entity.AssetChangeLog;
import com.eam.entity.AssetTransfer;
import com.eam.entity.AssetChangeLog;
import com.eam.repository.AssetChangeLogRepository;
import com.eam.repository.AssetTransferRepository;
import com.eam.service.IAssetService;
import com.eam.service.IAssetTransferService;
import com.eam.service.IAssetTransferStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 资产调拨 Service 实现类
 */
@Service
public class AssetTransferServiceImpl implements IAssetTransferService {

    private final AssetTransferRepository transferRepository;
    private final AssetChangeLogRepository changeLogRepository;
    private final IAssetService assetService;

    private final AssetTransferRepository transferRepository;
    private final AssetChangeLogRepository changeLogRepository;
    private final IAssetService assetService;
    private final IAssetTransferStatusService statusService;

    @Autowired
    public AssetTransferServiceImpl(AssetTransferRepository transferRepository,
                                      AssetChangeLogRepository changeLogRepository,
                                      IAssetService assetService,
                                      IAssetTransferStatusService statusService) {
        this.transferRepository = transferRepository;
        this.changeLogRepository = changeLogRepository;
        this.assetService = assetService;
        this.statusService = statusService;
    }

    @Override
    public Page<AssetTransfer> page(Long pageNum, Long pageSize, String status) {
        Specification<AssetTransfer> spec = (root, query, cb) -> {
            java.util.List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            if (status != null && !status.isEmpty()) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        Pageable pageable = PageRequest.of(pageNum.intValue() - 1, pageSize.intValue(),
                Sort.by(Sort.Direction.DESC, "createTime"));
        return transferRepository.findAll(spec, pageable);
    }

    @Override
    public AssetTransfer create(AssetTransfer transfer) {
        if (transfer.getAssetId() == null) {
            throw new BusinessException("资产ID不能为空");
        }
        transfer.setCreateTime(LocalDateTime.now());
        if (transfer.getStatus() == null) {
            transfer.setStatus("PENDING");
        }
        return transferRepository.save(transfer);
    }

    @Override
    public AssetTransfer approve(Long id, String approver, boolean approved, String remark) {
        AssetTransfer transfer = transferRepository.findById(id).orElse(null);
        if (transfer == null) {
            throw new BusinessException("调拨单不存在");
        }
        if (!"PENDING".equals(transfer.getStatus())) {
            throw new BusinessException("只有待审批的调拨单可以审批");
        }

        // 设置审批人
        transfer.setApprover(approver);
        transfer.setApproveTime(LocalDateTime.now());
        
        if (approved) {
            // 审批通过：状态改为APPROVED，但不立即完成
            transfer.setStatus("APPROVED");
        } else {
            // 审批拒绝：状态改为REJECTED
            transfer.setStatus("REJECTED");
        }
        
        // 保存审批结果
        return transferRepository.save(transfer);
    }

    @Override
    @Transactional
    public AssetTransfer approve(Long id, String approver, boolean approved, String remark) {
        AssetTransfer transfer = transferRepository.findById(id).orElse(null);
        if (transfer == null) {
            throw new BusinessException("调拨单不存在");
        }
        if (!statusService.STATUS_PENDING.equals(transfer.getStatus())) {
            throw new BusinessException("只有待审批的调拨单可以审批");
        }

        // 验证状态流转是否合法
        if (!statusService.isValidStatusTransition(transfer.getStatus(), approved ? statusService.STATUS_APPROVED : statusService.STATUS_REJECTED)) {
            throw new RuntimeException("非法的状态流转：从 " + transfer.getStatus() + " 到 " + 
                              (approved ? statusService.STATUS_APPROVED : statusService.STATUS_REJECTED));
        }

        // 设置审批人和备注
        transfer.setApprover(approver);
        if (remark != null && !remark.trim().isEmpty()) {
            transfer.setRemark(remark);
        }
        transfer.setApproveTime(LocalDateTime.now());
        
        // 更新状态
        if (approved) {
            // 审批通过：状态改为APPROVED，但不立即完成
            transfer.setStatus(statusService.STATUS_APPROVED);
        } else {
            // 审批拒绝：状态改为REJECTED
            transfer.setStatus(statusService.STATUS_REJECTED);
        }
        
        return transferRepository.save(transfer);
    }

    @Override
    @Transactional
    public AssetTransfer complete(Long id, String operator, String completeRemark) {
        AssetTransfer transfer = transferRepository.findById(id).orElse(null);
        if (transfer == null) {
            throw new BusinessException("调拨单不存在");
        }

        // 验证状态是否允许完成
        if (!statusService.canComplete(transfer)) {
            throw new RuntimeException("调拨单状态不允许完成操作。当前状态：" + transfer.getStatus());
        }

        // 更新资产信息
        Asset asset = assetService.getById(transfer.getAssetId());
        if (asset == null) {
            throw new BusinessException("资产不存在");
        }

        // 生成资产变动记录 - 部门变动
        AssetChangeLog deptLog = new AssetChangeLog();
        deptLog.setAssetId(transfer.getAssetId());
        deptLog.setChangeType("DEPT");
        deptLog.setOldValue(String.valueOf(asset.getDeptId()));
        deptLog.setNewValue(String.valueOf(transfer.getToDeptId()));
        deptLog.setReason("资产调拨: " + transfer.getTransferReason());
        deptLog.setChangeTime(LocalDateTime.now());
        deptLog.setOperator(transfer.getApprover());
        if (operator != null && !operator.trim().isEmpty()) {
            deptLog.setOperator(operator);
        }
        changeLogRepository.save(deptLog);

        // 如果使用人也变更了，生成使用人变动记录
        if (transfer.getToUserId() != null && !transfer.getToUserId().equals(asset.getUserId())) {
            AssetChangeLog userLog = new AssetChangeLog();
            userLog.setAssetId(transfer.getAssetId());
            userLog.setChangeType("USER");
            userLog.setOldValue(String.valueOf(asset.getUserId()));
            userLog.setNewValue(String.valueOf(transfer.getToUserId()));
            userLog.setReason("资产调拨使用人变更: " + transfer.getTransferReason());
            userLog.setChangeTime(LocalDateTime.now());
            userLog.setOperator(transfer.getApprover());
            if (operator != null && !operator.trim().isEmpty()) {
                userLog.setOperator(operator);
            }
            changeLogRepository.save(userLog);

            // 更新资产使用人
            asset.setUserId(transfer.getToUserId());
        }

        // 更新资产部门
        asset.setDeptId(transfer.getToDeptId());
        assetService.update(asset);

        // 更新调拨单状态
        transfer.setCompleteTime(LocalDateTime.now());
        if (completeRemark != null && !completeRemark.trim().isEmpty()) {
            transfer.setRemark(completeRemark);
        }
        transfer.setStatus(statusService.STATUS_COMPLETED);

        return transferRepository.save(transfer);
    }

    @Override
    public List<AssetTransfer> list() {
        return transferRepository.findAll();
    }

    @Override
    public AssetTransfer getById(Long id) {
        return transferRepository.findById(id).orElse(null);
    }

    @Override
    public List<AssetTransfer> listPending() {
        return transferRepository.findByStatusOrderByCreateTimeDesc("PENDING");
    }
}