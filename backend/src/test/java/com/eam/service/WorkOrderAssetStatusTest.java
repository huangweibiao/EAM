package com.eam.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

import com.eam.entity.Asset;
import com.eam.entity.WorkOrder;

import java.time.LocalDateTime;

/**
 * 工单完成时资产状态更新功能单元测试
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class WorkOrderAssetStatusTest {

    @Autowired
    private IWorkOrderService workOrderService;

    @Autowired
    private IAssetService assetService;

    /**
     * 4.3.1: 测试正常情况下的状态流转
     * 工单完成时，资产状态从MAINTENANCE变为IN_USE
     */
    @Test
    public void testWorkOrderCompleteWithAssetStatusUpdate() {
        // 1. 准备测试数据：创建资产并设置为MAINTENANCE状态
        Asset asset = new Asset();
        asset.setAssetCode("TEST" + System.currentTimeMillis());
        asset.setAssetName("测试资产");
        asset.setCategoryId(1L);
        asset.setDeptId(1L);
        asset.setStatus("MAINTENANCE");
        asset.setCreateBy("test");
        Asset createdAsset = assetService.add(asset);
        
        assertNotNull(createdAsset, "资产创建成功");
        assertEquals("MAINTENANCE", createdAsset.getStatus(), "资产状态应为MAINTENANCE");

        // 2. 创建工单
        WorkOrder workOrder = new WorkOrder();
        workOrder.setOrderNo("WO" + System.currentTimeMillis());
        workOrder.setAssetId(createdAsset.getId());
        workOrder.setOrderType("REPAIR");
        workOrder.setPriority("MEDIUM");
        workOrder.setTitle("测试工单");
        workOrder.setDescription("测试工单完成功能");
        workOrder.setReporter("test_user");
        workOrder.setStatus("PROCESSING");
        WorkOrder createdWorkOrder = workOrderService.create(workOrder);

        assertNotNull(createdWorkOrder, "工单创建成功");
        assertEquals("PROCESSING", createdWorkOrder.getStatus(), "工单状态应为PROCESSING");

        // 3. 完成工单
        WorkOrder completedWorkOrder = workOrderService.complete(createdWorkOrder.getId(), "测试解决方案");

        assertNotNull(completedWorkOrder, "工单完成成功");
        assertEquals("COMPLETED", completedWorkOrder.getStatus(), "工单状态应为COMPLETED");
        assertNotNull(completedWorkOrder.getCompleteTime(), "工单完成时间不应为空");

        // 4. 验证资产状态是否更新为IN_USE
        Asset updatedAsset = assetService.getById(createdAsset.getId());
        assertNotNull(updatedAsset, "资产查询成功");
        assertEquals("IN_USE", updatedAsset.getStatus(), "工单完成后，资产状态应更新为IN_USE");

        // 清理测试数据
        workOrderService.delete(completedWorkOrder.getId());
        assetService.delete(createdAsset.getId());
    }

    /**
     * 4.3.2: 测试异常状态下的处理
     * 工单完成时，资产状态不是MAINTENANCE，不应修改资产状态
     */
    @Test
    public void testWorkOrderCompleteWithNonMaintenanceAssetStatus() {
        // 1. 准备测试数据：创建资产并设置为IN_USE状态
        Asset asset = new Asset();
        asset.setAssetCode("TEST" + System.currentTimeMillis());
        asset.setAssetName("测试资产");
        asset.setCategoryId(1L);
        asset.setDeptId(1L);
        asset.setStatus("IN_USE"); // 使用中状态
        asset.setCreateBy("test");
        Asset createdAsset = assetService.add(asset);

        assertNotNull(createdAsset, "资产创建成功");
        assertEquals("IN_USE", createdAsset.getStatus(), "资产状态应为IN_USE");

        // 2. 创建工单
        WorkOrder workOrder = new WorkOrder();
        workOrder.setOrderNo("WO" + System.currentTimeMillis());
        workOrder.setAssetId(createdAsset.getId());
        workOrder.setOrderType("REPAIR");
        workOrder.setPriority("MEDIUM");
        workOrder.setTitle("测试工单");
        workOrder.setDescription("测试工单完成功能");
        workOrder.setReporter("test_user");
        workOrder.setStatus("PROCESSING");
        WorkOrder createdWorkOrder = workOrderService.create(workOrder);

        // 3. 完成工单
        WorkOrder completedWorkOrder = workOrderService.complete(createdWorkOrder.getId(), "测试解决方案");

        assertNotNull(completedWorkOrder, "工单完成成功");
        assertEquals("COMPLETED", completedWorkOrder.getStatus(), "工单状态应为COMPLETED");

        // 4. 验证资产状态仍然为IN_USE（不应被修改）
        Asset updatedAsset = assetService.getById(createdAsset.getId());
        assertNotNull(updatedAsset, "资产查询成功");
        assertEquals("IN_USE", updatedAsset.getStatus(), "资产状态应保持为IN_USE");

        // 清理测试数据
        workOrderService.delete(completedWorkOrder.getId());
        assetService.delete(createdAsset.getId());
    }

    /**
     * 4.3.3: 验证资产状态与工单状态的关联性
     */
    @Test
    public void testAssetWorkOrderStatusAssociation() {
        // 1. 创建资产和工单
        Asset asset = new Asset();
        asset.setAssetCode("TEST" + System.currentTimeMillis());
        asset.setAssetName("测试资产");
        asset.setCategoryId(1L);
        asset.setDeptId(1L);
        asset.setStatus("MAINTENANCE");
        asset.setCreateBy("test");
        Asset createdAsset = assetService.add(asset);

        WorkOrder workOrder = new WorkOrder();
        workOrder.setOrderNo("WO" + System.currentTimeMillis());
        workOrder.setAssetId(createdAsset.getId());
        workOrder.setOrderType("REPAIR");
        workOrder.setPriority("MEDIUM");
        workOrder.setTitle("测试工单");
        workOrder.setDescription("测试工单完成功能");
        workOrder.setReporter("test_user");
        workOrder.setStatus("PROCESSING");
        WorkOrder createdWorkOrder = workOrderService.create(workOrder);

        // 2. 验证PROCESSING状态下的关联性
        // PROCESSING状态下，资产状态可以为MAINTENANCE或IN_USE
        Asset assetInProcessing = assetService.getById(createdAsset.getId());
        assertTrue(
            "MAINTENANCE".equals(assetInProcessing.getStatus()) || 
            "IN_USE".equals(assetInProcessing.getStatus()),
            "PROCESSING状态下资产状态应为MAINTENANCE或IN_USE"
        );

        // 3. 完成工单
        WorkOrder completedWorkOrder = workOrderService.complete(createdWorkOrder.getId(), "测试解决方案");

        // 4. 验证COMPLETED状态下的关联性
        // COMPLETED状态下，资产状态应为IN_USE
        Asset assetAfterComplete = assetService.getById(createdAsset.getId());
        assertEquals("IN_USE", assetAfterComplete.getStatus(), 
            "COMPLETED状态下资产状态应为IN_USE");

        // 清理测试数据
        workOrderService.delete(completedWorkOrder.getId());
        assetService.delete(createdAsset.getId());
    }

    /**
     * 测试资产状态流转验证
     */
    @Test
    public void testAssetStatusTransitionValidation() {
        // 创建测试资产
        Asset asset = new Asset();
        asset.setAssetCode("TEST" + System.currentTimeMillis());
        asset.setAssetName("测试资产");
        asset.setCategoryId(1L);
        asset.setDeptId(1L);
        asset.setStatus("NEW");
        asset.setCreateBy("test");
        Asset createdAsset = assetService.add(asset);

        // 测试合法的状态流转
        assertTrue(assetService.isValidStatusTransition("NEW", "IN_USE"), 
            "NEW -> IN_USE 应为合法流转");
        assertTrue(assetService.isValidStatusTransition("NEW", "MAINTENANCE"), 
            "NEW -> MAINTENANCE 应为合法流转");
        assertTrue(assetService.isValidStatusTransition("IN_USE", "MAINTENANCE"), 
            "IN_USE -> MAINTENANCE 应为合法流转");
        assertTrue(assetService.isValidStatusTransition("IN_USE", "SCRAP"), 
            "IN_USE -> SCRAP 应为合法流转");
        assertTrue(assetService.isValidStatusTransition("IN_USE", "LOST"), 
            "IN_USE -> LOST 应为合法流转");
        assertTrue(assetService.isValidStatusTransition("MAINTENANCE", "IN_USE"), 
            "MAINTENANCE -> IN_USE 应为合法流转");

        // 测试不合法的状态流转
        assertFalse(assetService.isValidStatusTransition("NEW", "SCRAP"), 
            "NEW -> SCRAP 应为不合法流转");
        assertFalse(assetService.isValidStatusTransition("MAINTENANCE", "SCRAP"), 
            "MAINTENANCE -> SCRAP 应为不合法流转");
        assertFalse(assetService.isValidStatusTransition("SCRAP", "IN_USE"), 
            "SCRAP -> IN_USE 应为不合法流转");
        assertFalse(assetService.isValidStatusTransition("LOST", "IN_USE"), 
            "LOST -> IN_USE 应为不合法流转");

        // 清理测试数据
        assetService.delete(createdAsset.getId());
    }

    /**
     * 测试资产状态流转边界情况
     */
    @Test
    public void testAssetStatusTransitionEdgeCases() {
        // 测试null值处理
        assertFalse(assetService.isValidStatusTransition(null, "IN_USE"), 
            "null状态不应为合法流转");
        assertFalse(assetService.isValidStatusTransition("NEW", null), 
            "目标状态为null不应为合法流转");

        // 测试无效状态
        assertFalse(assetService.isValidStatusTransition("INVALID", "IN_USE"), 
            "无效状态不应为合法流转");
        assertFalse(assetService.isValidStatusTransition("NEW", "INVALID"), 
            "目标无效状态不应为合法流转");
    }

    /**
     * 测试工单状态流转的完整性
     */
    @Test
    public void testWorkOrderStatusFlow() {
        // 创建资产
        Asset asset = new Asset();
        asset.setAssetCode("TEST" + System.currentTimeMillis());
        asset.setAssetName("测试资产");
        asset.setCategoryId(1L);
        asset.setDeptId(1L);
        asset.setStatus("MAINTENANCE");
        asset.setCreateBy("test");
        Asset createdAsset = assetService.add(asset);

        // 创建工单
        WorkOrder workOrder = new WorkOrder();
        workOrder.setOrderNo("WO" + System.currentTimeMillis());
        workOrder.setAssetId(createdAsset.getId());
        workOrder.setOrderType("REPAIR");
        workOrder.setPriority("MEDIUM");
        workOrder.setTitle("测试工单状态流转");
        workOrder.setDescription("测试工单完整状态流转");
        workOrder.setReporter("test_user");
        workOrder.setStatus("PENDING");
        WorkOrder createdWorkOrder = workOrderService.create(workOrder);

        // 验证初始状态
        assertEquals("PENDING", createdWorkOrder.getStatus(), "工单初始状态应为PENDING");

        // 指派工单
        WorkOrder assignedWorkOrder = workOrderService.assign(createdWorkOrder.getId(), "test_assignee");
        assertEquals("ASSIGNED", assignedWorkOrder.getStatus(), "工单指派后状态应为ASSIGNED");
        assertNotNull(assignedWorkOrder.getAssignTime(), "指派时间不应为空");

        // 开始处理
        WorkOrder processingWorkOrder = workOrderService.process(assignedWorkOrder.getId());
        assertEquals("PROCESSING", processingWorkOrder.getStatus(), "工单处理状态应为PROCESSING");

        // 完成工单
        WorkOrder completedWorkOrder = workOrderService.complete(processingWorkOrder.getId(), "测试解决方案");
        assertEquals("COMPLETED", completedWorkOrder.getStatus(), "工单完成状态应为COMPLETED");
        assertNotNull(completedWorkOrder.getCompleteTime(), "完成时间不应为空");

        // 评价工单
        WorkOrder ratedWorkOrder = workOrderService.rate(completedWorkOrder.getId(), 5);
        assertEquals(5, ratedWorkOrder.getRating(), "工单评价应为5分");

        // 关闭工单
        WorkOrder closedWorkOrder = workOrderService.close(ratedWorkOrder.getId());
        assertEquals("CLOSED", closedWorkOrder.getStatus(), "工单关闭状态应为CLOSED");
        assertNotNull(closedWorkOrder.getClosedTime(), "关闭时间不应为空");

        // 清理测试数据
        workOrderService.delete(closedWorkOrder.getId());
        assetService.delete(createdAsset.getId());
    }
}