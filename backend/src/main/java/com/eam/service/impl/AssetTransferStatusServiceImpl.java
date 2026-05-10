package com.eam.service.impl;

import com.eam.entity.AssetTransfer;
import com.eam.repository.AssetTransferRepository;
import com.eam.service.IAssetTransferService;
import com.eam.service.IAssetTransferStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 资产调拨状态管理 Service 实现类
 * Task 7.2.2: 实现审批通过后的状态管理
 */
@Service
public class AssetTransferStatusServiceImpl implements IAssetTransferStatusService {

    @Autowired
    private AssetTransferRepository transferRepository;

    /**
     * 资产调拨状态常量
     */
    @Override
    public String getSTATUS_PENDING() {
        return STATUS_PENDING;
    }

    @Override
    public String getSTATUS_APPROVED() {
        return STATUS_APPROVED;
    }

    @Override
    public String getSTATUS_IN_PROCESS() {
        return STATUS_IN_PROCESS;
    }

    @Override
    public String getSTATUS_COMPLETED() {
        return STATUS_COMPLETED;
    }

    @Override
    public String getSTATUS_REJECTED() {
        return STATUS_REJECTED;
    }

    @Override
    public String getSTATUS_CANCELED() {
        return STATUS_CANCELED;
    }

    /**
     * 验证状态流转是否合法
     * Task 7.2.4: 添加状态流转验证
     */
    @Override
    public boolean isValidStatusTransition(String currentStatus, String targetStatus) {
        if (currentStatus == null || targetStatus == null) {
            return false;
        }

        switch (currentStatus) {
            case STATUS_PENDING:
                // 待审批 -> 已审批 或 已拒绝 或 已取消
                return STATUS_APPROVED.equals(targetStatus) || 
                       STATUS_REJECTED.equals(targetStatus) || 
                       STATUS_CANCELED.equals(targetStatus);
                       
            case STATUS_APPROVED:
                // 已审批 -> 处理中 或 已完成
                return STATUS_IN_PROCESS.equals(targetStatus) || 
                       STATUS_COMPLETED.equals(targetStatus);
                       
            case STATUS_IN_PROCESS:
                // 处理中 -> 已完成
                return STATUS_COMPLETED.equals(targetStatus);
                
            case STATUS_COMPLETED:
                // 已完成是最终状态，不能再转换
                return false;
                
            case STATUS_REJECTED:
            case STATUS_CANCELED:
                // 已拒绝和已取消是最终状态，不能再转换
                return false;
                
            default:
                return false;
        }
    }

    /**
     * 获取状态显示名称
     */
    @Override
    public String getStatusDisplayName(String status) {
        if (status == null) {
            return "未知状态";
        }

        switch (status) {
            case STATUS_PENDING:
                return "待审批";
            case STATUS_APPROVED:
                return "已审批";
            case STATUS_IN_PROCESS:
                return "处理中";
            case STATUS_COMPLETED:
                return "已完成";
            case STATUS_REJECTED:
                return "已拒绝";
            case STATUS_CANCELED:
                return "已取消";
            default:
                return "未知状态";
        }
    }

    /**
     * 判断是否可以完成调拨
     */
    @Override
    public boolean canComplete(AssetTransfer transfer) {
        if (transfer == null) {
            return false;
        }

        // 只有已审批状态的调拨单可以完成
        return STATUS_APPROVED.equals(transfer.getStatus());
    }

    /**
     * 判断是否可以取消调拨
     */
    @Override
    public boolean canCancel(AssetTransfer transfer) {
        if (transfer == null) {
            return false;
        }

        // 只有待审批状态的调拨单可以取消
        return STATUS_PENDING.equals(transfer.getStatus());
    }

    /**
     * 更新调拨状态
     */
    @Override
    @Transactional
    public AssetTransfer updateStatus(Long transferId, String newStatus) {
        AssetTransfer transfer = transferRepository.findById(transferId).orElse(null);
        if (transfer == null) {
            return null;
        }

        // 验证状态流转是否合法
        if (!isValidStatusTransition(transfer.getStatus(), newStatus)) {
            throw new RuntimeException("非法的状态流转：从 " + transfer.getStatus() + " 到 " + newStatus);
        }

        // 更新状态
        transfer.setStatus(newStatus);
        return transferRepository.save(transfer);
    }

    /**
     * 完成调拨（设置完成时间和状态）
     */
    @Override
    @Transactional
    public AssetTransfer completeTransfer(Long transferId, String operator) {
        AssetTransfer transfer = transferRepository.findById(transferId).orElse(null);
        if (transfer == null) {
            return null;
        }

        // 检查是否可以完成
        if (!canComplete(transfer)) {
            throw new RuntimeException("调拨单状态不允许完成操作。当前状态：" + transfer.getStatus());
        }

        // 设置完成时间和状态
        transfer.setCompleteTime(LocalDateTime.now());
        transfer.setOperator(operator);
        transfer.setStatus(STATUS_COMPLETED);

        return transferRepository.save(transfer);
    }
}