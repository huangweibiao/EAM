package com.eam.service;


/**
 * 工单 Service 单元测试接口
 */
public interface IWorkOrderServiceTest {

    /**
     * 测试工单完成时资产状态更新
     */
    boolean testWorkOrderCompleteWithAssetStatusUpdate();

    /**
     * 测试工单完成时资产状态不更新（非MAINTENANCE状态）
     */
    boolean testWorkOrderCompleteWithoutAssetStatusUpdate();

    /**
     * 测试工单创建与资产关联
     */
    boolean testWorkOrderCreationWithAsset();

    /**
     * 测试工单状态流转验证
     */
    boolean testAssetStatusTransitionValidation();

    /**
     * 测试状态流转规则
     */
    boolean testStatusTransitionRules();
}