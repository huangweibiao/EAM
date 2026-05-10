package com.eam.service;

import com.eam.entity.AssetTransfer;

import java.time.LocalDateTime;

/**
 * 资产调拨完成状态常量
 * Task 7.2.2: 实现审批通过后的状态管理
 */
public interface IAssetTransferStatusService {
    
    /**
     * 资产调拨状态常量
     */
    String STATUS_PENDING = "PENDING";           // 待审批
    String STATUS_APPROVED = "APPROVED";         // 已审批
    String STATUS_IN_PROCESS = "IN_PROCESS"; // 处理中
    String STATUS_COMPLETED = "COMPLETED";       // 已完成
    String STATUS_REJECTED = "REJECTED";       // 已拒绝
    String STATUS_CANCELED = "CANCELED";        // 已取消
    
    /**
     * 验证状态流转是否合法
     * @param currentStatus 当前状态
     * @param targetStatus 目标状态
     * @return 是否合法
     */
    boolean isValidStatusTransition(String currentStatus, String targetStatus);
    
    /**
     * 获取状态显示名称
     * @param status 状态代码
     * @return 状态显示名称
     */
    String getStatusDisplayName(String status);
    
    /**
     * 判断是否可以完成调拨
     * @param transfer 资产调拨对象
     * @return 是否可以完成
     */
    boolean canComplete(AssetTransfer transfer);
    
    /**
     * 判断是否可以取消调拨
     * @param transfer 资产调拨对象
     * @return 是否可以取消
     */
    boolean canCancel(AssetTransfer transfer);
    
    /**
     * 更新调拨状态
     * @param transferId 调拨ID
     * @param newStatus 新状态
     * @return 更新后的调拨对象
     */
    AssetTransfer updateStatus(Long transferId, String newStatus);
    
    /**
     * 完成调拨（设置完成时间和操作人）
     * @param transferId 调拨ID
     * @param operator 操作人
     * @return 完成后的调拨对象
     */
    AssetTransfer completeTransfer(Long transferId, String operator);
}