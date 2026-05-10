package com.eam.service.impl;

import com.eam.common.BusinessException;
import com.eam.entity.Asset;
import com.eam.entity.AssetChangeLog;
import com.eam.entity.AssetTransfer;
import com.eam.repository.AssetChangeLogRepository;
import com.eam.repository.AssetTransferRepository;
import com.eam.service.IAssetTransferService;
import com.eam.service.IAssetService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
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
 * 资产调拨 Service 实现类
 */
@Service
public class AssetTransferServiceImpl implements IAssetTransferService {

    private final AssetTransferRepository transferRepository;
    private final AssetChangeLogRepository changeLogRepository;
    private final IAssetService assetService;

    @Autowired
    public AssetTransferServiceImpl(AssetTransferRepository transferRepository,
                                     AssetChangeLogRepository changeLogRepository,
                                     IAssetService assetService) {
        this.transferRepository = transferRepository;
        this.changeLogRepository = changeLogRepository;
        this.assetService = assetService;
    }

    @Override
    public Page<AssetTransfer> page(Long pageNum, Long pageSize, String status) {
        // JPA 分页从 0 开始，MyBatis-Plus 从 1 开始，需要转换
        Pageable pageable = PageRequest.of(pageNum.intValue() - 1, pageSize.intValue(),
                Sort.by(Sort.Direction.DESC, "createTime"));

        Specification<AssetTransfer> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(status)) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return transferRepository.findAll(spec, pageable);
    }

    @Override
    public AssetTransfer create(AssetTransfer transfer) {
        // 生成调拨单号
        String transferNo = "TRF" + System.currentTimeMillis();
        transfer.setTransferNo(transferNo);
        if (transfer.getStatus() == null) {
            transfer.setStatus("PENDING");
        }
        if (transfer.getTransferTime() == null) {
            transfer.setTransferTime(LocalDateTime.now());
        }

        // 检查资产是否存在
        Asset asset = assetService.getById(transfer.getAssetId());
        if (asset == null) {
            throw new BusinessException("资产不存在");
        }

        return transferRepository.save(transfer);
    }

    @Override
    public AssetTransfer approve(Long id, String approver, boolean approved) {
        AssetTransfer transfer = transferRepository.findById(id).orElse(null);
        if (transfer == null) {
            throw new BusinessException("调拨单不存在");
        }
        if (!"PENDING".equals(transfer.getStatus())) {
            throw new BusinessException("只有待审批的调拨单可以审批");
        }

        transfer.setApprover(approver);
        transfer.setApproveTime(LocalDateTime.now());
        transfer.setStatus(approved ? "APPROVED" : "REJECTED");
        transferRepository.save(transfer);

        // 如果审批通过，自动完成调拨（更新资产信息）
        if (approved) {
            complete(id);
        }

        return transfer;
    }

    @Override
    public AssetTransfer complete(Long id) {
        AssetTransfer transfer = transferRepository.findById(id).orElse(null);
        if (transfer == null) {
            throw new BusinessException("调拨单不存在");
        }
        if (!"APPROVED".equals(transfer.getStatus())) {
            throw new BusinessException("只有已审批的调拨单可以完成");
        }

        // 获取资产原信息
        Asset asset = assetService.getById(transfer.getAssetId());
        if (asset == null) {
            throw new BusinessException("资产不存在");
        }

        Long oldDeptId = asset.getDeptId();
        Long oldUserId = asset.getUserId();

        // 更新资产信息（部门和使用人）
        asset.setDeptId(transfer.getToDeptId());
        if (transfer.getToUserId() != null) {
            asset.setUserId(transfer.getToUserId());
        }
        assetService.update(asset);

        // 生成资产变动记录 - 部门变动
        AssetChangeLog deptLog = new AssetChangeLog();
        deptLog.setAssetId(transfer.getAssetId());
        deptLog.setChangeType("DEPT");
        deptLog.setOldValue(String.valueOf(oldDeptId));
        deptLog.setNewValue(String.valueOf(transfer.getToDeptId()));
        deptLog.setReason("资产调拨: " + transfer.getTransferReason());
        deptLog.setChangeTime(LocalDateTime.now());
        deptLog.setOperator(transfer.getOperator());
        changeLogRepository.save(deptLog);

        // 如果使用人也变更了，生成使用人变动记录
        if (transfer.getToUserId() != null && !transfer.getToUserId().equals(oldUserId)) {
            AssetChangeLog userLog = new AssetChangeLog();
            userLog.setAssetId(transfer.getAssetId());
            userLog.setChangeType("USER");
            userLog.setOldValue(String.valueOf(oldUserId));
            userLog.setNewValue(String.valueOf(transfer.getToUserId()));
            userLog.setReason("资产调拨: " + transfer.getTransferReason());
            userLog.setChangeTime(LocalDateTime.now());
            userLog.setOperator(transfer.getOperator());
            changeLogRepository.save(userLog);
        }

        transfer.setStatus("COMPLETED");
        return transferRepository.save(transfer);
    }

    @Override
    public List<AssetTransfer> listPending() {
        return transferRepository.findByStatusOrderByCreateTimeDesc("PENDING");
    }
}