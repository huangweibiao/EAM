package com.eam.service;

import com.eam.entity.Asset;
import com.eam.entity.WorkOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 工单完成与资产状态流转单元测试
 * 测试Task 4.3: 测试工单完成后的资产状态变化
 */
@SpringBootTest
@Transactional
public class WorkOrderCompleteAndAssetStatusTest {

    @Autowired
    private IWorkOrderService workOrderService;

    @Autowired
    private IAssetService assetService;

    /**
     * 测试正常情况下的工单完成和资产状态流转
     * 4.3.1: 测试正常情况下的状态流转
     */
    @Test
    public void testWorkOrderCompleteWithAssetStatusUpdate() {
        System.out.println("=== 测试4.3.1: 正常情况下的工单完成和资产状态流转 ===");

        // 1. 准备测试数据：创建一个测试资产
        Asset testAsset = createTestAsset();
        testAsset.setStatus("MAINTENANCE"); // 设置为维修中状态
        Asset savedAsset = assetService.add(testAsset);
        
        assertNotNull(savedAsset, "资产创建失败");
        assertEquals("MAINTENANCE", savedAsset.getStatus(), "资产状态设置失败");

        // 2. 创建测试工单
        WorkOrder workOrder = createTestWorkOrder(savedAsset.getId());
        workOrder.setStatus("PROCESSING"); // 设置为处理中状态
        WorkOrder savedWorkOrder = workOrderService.create(workOrder);
        
        assertNotNull(savedWorkOrder, "工单创建失败");
        assertEquals("PROCESSING", savedWorkOrder.getStatus(), "工单状态设置失败");

        // 3. 完成工单
        WorkOrder completedWorkOrder = workOrderService.complete(savedWorkOrder.getId(), "测试解决方案");
        
        assertNotNull(completedWorkOrder, "工单完成失败");
        assertEquals("COMPLETED", completedWorkOrder.getStatus(), "工单状态未更新为COMPLETED");
        assertNotNull(completedWorkOrder.getCompleteTime(), "工单完成时间未设置");

        // 4. 验证资产状态是否更新为IN_USE
        Asset updatedAsset = assetService.getById(savedAsset.getId());
        assertNotNull(updatedAsset, "获取更新后的资产失败");
        assertEquals("IN_USE", updatedAsset.getStatus(), "工单完成后，资产状态未正确更新为IN_USE");

        // 5. 清理测试数据
        assetService.delete(savedAsset.getId());

        System.out.println("✅ 测试通过：工单完成时，资产状态正确从MAINTENANCE更新为IN_USE");
    }

    /**
     * 测试异常状态下的处理（资产状态不是MAINTENANCE）
     * 4.3.2: 测试异常状态下的处理
     */
    @Test
    public void testWorkOrderCompleteWithNonMaintenanceAssetStatus() {
        System.out.println("=== 测试4.3.2: 异常状态下的工单完成处理 ===");

        // 1. 准备测试数据：创建一个测试资产，状态为IN_USE
        Asset testAsset = createTestAsset();
        testAsset.setStatus("IN_USE"); // 设置为使用中状态（非MAINTENANCE）
        Asset savedAsset = assetService.add(testAsset);
        
        assertNotNull(savedAsset, "资产创建失败");
        assertEquals("IN_USE", savedAsset.getStatus(), "资产状态设置失败");

        // 2. 创建测试工单
        WorkOrder workOrder = createTestWorkOrder(savedAsset.getId());
        workOrder.setStatus("PROCESSING");
        WorkOrder savedWorkOrder = workOrderService.create(workOrder);
        
        assertNotNull(savedWorkOrder, "工单创建失败");

        // 3. 完成工单
        WorkOrder completedWorkOrder = workOrderService.complete(savedWorkOrder.getId(), "测试解决方案");
        
        assertNotNull(completedWorkOrder, "工单完成失败");
        assertEquals("COMPLETED", completedWorkOrder.getStatus(), "工单状态未更新为COMPLETED");

        // 4. 验证资产状态是否保持不变（因为原状态不是MAINTENANCE）
        Asset updatedAsset = assetService.getById(savedAsset.getId());
        assertNotNull(updatedAsset, "获取更新后的资产失败");
        assertEquals("IN_USE", updatedAsset.getStatus(), "工单完成后，资产状态不应被修改");

        // 5. 清理测试数据
        assetService.delete(savedAsset.getId());

        System.out.println("✅ 测试通过：工单完成时，当资产状态不是MAINTENANCE时，资产状态保持不变");
    }

    /**
     * 测试工单完成时资产状态流转验证
     */
    @Test
    public void testWorkOrderCompleteWithStatusValidation() {
        System.out.println("=== 测试工单完成时的资产状态流转验证 ===");

        // 1. 测试NEW -> MAINTENANCE流转是否合法
        Asset testAsset = createTestAsset();
        testAsset.setStatus("NEW");
        Asset savedAsset = assetService.add(testAsset);
        
        boolean isValidTransition = assetService.isValidStatusTransition("NEW", "MAINTENANCE");
        assertTrue(isValidTransition, "NEW -> MAINTENANCE 状态流转应该是合法的");

        // 2. 测试MAINTENANCE -> IN_USE流转是否合法
        isValidTransition = assetService.isValidStatusTransition("MAINTENANCE", "IN_USE");
        assertTrue(isValidTransition, "MAINTENANCE -> IN_USE 状态流转应该是合法的");

        // 3. 测试非法流转：MAINTENANCE -> SCRAP
        isValidTransition = assetService.isValidStatusTransition("MAINTENANCE", "SCRAP");
        assertFalse(isValidTransition, "MAINTENANCE -> SCRAP 状态流转应该是非法的");

        // 4. 测试最终状态流转：SCRAP -> IN_USE
        isValidTransition = assetService.isValidStatusTransition("SCRAP", "IN_USE");
        assertFalse(isValidTransition, "SCRAP -> IN_USE 状态流转应该是非法的（SCRAP是最终状态）");

        // 清理测试数据
        assetService.delete(savedAsset.getId());

        System.out.println("✅ 测试通过：资产状态流转验证逻辑正确");
    }

    /**
     * 验证资产状态与工单状态的关联性
     * 4.3.3: 验证资产状态与工单状态的关联性
     */
    @Test
    public void testAssetWorkOrderStatusAssociation() {
        System.out.println("=== 测试4.3.3: 验证资产状态与工单状态的关联性 ===");

        // 1. 场景1: 工单状态为PROCESSING，资产状态为MAINTENANCE
        Asset testAsset = createTestAsset();
        testAsset.setStatus("MAINTENANCE");
        Asset savedAsset = assetService.add(testAsset);
        
        WorkOrder workOrder = createTestWorkOrder(savedAsset.getId());
        workOrder.setStatus("PROCESSING");
        WorkOrder savedWorkOrder = workOrderService.create(workOrder);
        
        Asset asset = assetService.getById(savedAsset.getId());
        WorkOrder currentWorkOrder = workOrderService.getById(savedWorkOrder.getId());
        
        boolean isAssociationValid = (currentWorkOrder.getStatus().equals("PROCESSING") && 
                                      asset.getStatus().equals("MAINTENANCE")) ||
                                     (currentWorkOrder.getStatus().equals("PROCESSING") && 
                                      asset.getStatus().equals("IN_USE"));
        assertTrue(isAssociationValid, "PROCESSING状态工单与MAINTENANCE/IN_USE状态资产关联不正确");

        // 2. 场景2: 工单完成后，资产状态应为IN_USE
        workOrderService.complete(savedWorkOrder.getId(), "测试完成");
        
        WorkOrder completedWorkOrder = workOrderService.getById(savedWorkOrder.getId());
        Asset updatedAsset = assetService.getById(savedAsset.getId());
        
        boolean isCompletedAssociationValid = 
            completedWorkOrder.getStatus().equals("COMPLETED") && 
            updatedAsset.getStatus().equals("IN_USE");
        
        assertTrue(isCompletedAssociationValid, "COMPLETED状态工单应与IN_USE状态资产关联");

        // 清理测试数据
        assetService.delete(savedAsset.getId());

        System.out.println("✅ 测试通过：资产状态与工单状态关联性正确");
    }

    /**
     * 测试工单状态流转的完整流程
     */
    @Test
    public void testCompleteWorkOrderStatusFlow() {
        System.out.println("=== 测试工单状态流转的完整流程 ===");

        // 1. 创建测试资产
        Asset testAsset = createTestAsset();
        testAsset.setStatus("IN_USE");
        Asset savedAsset = assetService.add(testAsset);

        // 2. 创建工单（PENDING状态）
        WorkOrder workOrder = createTestWorkOrder(savedAsset.getId());
        WorkOrder savedWorkOrder = workOrderService.create(workOrder);
        assertEquals("PENDING", savedWorkOrder.getStatus(), "工单初始状态应为PENDING");

        // 3. 指派工单（PENDING -> ASSIGNED）
        WorkOrder assignedWorkOrder = workOrderService.assign(savedWorkOrder.getId(), "test_user");
        assertEquals("ASSIGNED", assignedWorkOrder.getStatus(), "工单指派后状态应为ASSIGNED");

        // 4. 开始处理（ASSIGNED -> PROCESSING）
        WorkOrder processingWorkOrder = workOrderService.process(assignedWorkOrder.getId());
        assertEquals("PROCESSING", processingWorkOrder.getStatus(), "工单处理中状态应为PROCESSING");

        // 5. 完成工单（PROCESSING -> COMPLETED）
        WorkOrder completedWorkOrder = workOrderService.complete(processingWorkOrder.getId(), "测试完成");
        assertEquals("COMPLETED", completedWorkOrder.getStatus(), "工单完成状态应为COMPLETED");

        // 6. 关闭工单（COMPLETED -> CLOSED）
        WorkOrder closedWorkOrder = workOrderService.close(completedWorkOrder.getId());
        assertEquals("CLOSED", closedWorkOrder.getStatus(), "工单关闭状态应为CLOSED");

        // 清理测试数据
        assetService.delete(savedAsset.getId());

        System.out.println("✅ 测试通过：工单状态流转流程正确");
    }

    /**
     * 测试工单取消功能
     */
    @Test
    public void testWorkOrderCancel() {
        System.out.println("=== 测试工单取消功能 ===");

        // 1. 创建测试资产
        Asset testAsset = createTestAsset();
        Asset savedAsset = assetService.add(testAsset);

        // 2. 创建工单
        WorkOrder workOrder = createTestWorkOrder(savedAsset.getId());
        WorkOrder savedWorkOrder = workOrderService.create(workOrder);
        assertEquals("PENDING", savedWorkOrder.getStatus(), "工单初始状态应为PENDING");

        // 3. 测试从PENDING状态取消工单
        // 注意：这个功能需要在Task 9中实现，这里做预期测试
        // WorkOrder canceledWorkOrder = workOrderService.cancel(savedWorkOrder.getId(), "测试取消");
        // assertEquals("CANCELED", canceledWorkOrder.getStatus(), "工单取消后状态应为CANCELED");

        // 清理测试数据
        assetService.delete(savedAsset.getId());

        System.out.println("✅ 测试通过：工单取消功能（待Task 9实现完整逻辑）");
    }

    // ========== 辅助方法 ==========

    /**
     * 创建测试资产
     */
    private Asset createTestAsset() {
        Asset asset = new Asset();
        asset.setAssetCode("TEST_" + System.currentTimeMillis());
        asset.setAssetName("测试资产_" + System.currentTimeMillis());
        asset.setCategoryId(1L);
        asset.setDeptId(1L);
        asset.setMaintenanceCycle(30); // 30天维护周期
        asset.setPurchasePrice(new java.math.BigDecimal("10000.00"));
        asset.setStatus("NEW");
        asset.setCreateBy("test_user");
        return asset;
    }

    /**
     * 创建测试工单
     */
    private WorkOrder createTestWorkOrder(Long assetId) {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setOrderNo("WO_TEST_" + System.currentTimeMillis());
        workOrder.setAssetId(assetId);
        workOrder.setOrderType("REPAIR");
        workOrder.setPriority("MEDIUM");
        workOrder.setTitle("测试工单_" + System.currentTimeMillis());
        workOrder.setDescription("单元测试工单");
        workOrder.setReporter("test_user");
        workOrder.setReportTime(LocalDateTime.now());
        workOrder.setStatus("PENDING");
        return workOrder;
    }
}