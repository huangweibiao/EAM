package com.eam.controller;

import com.eam.annotation.OperationLog;
import com.eam.security.RequirePermission;
import com.eam.common.Result;
import com.eam.entity.Notification;
import com.eam.service.INotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * 通知管理 Controller
 * Task 14.4: 集成通知服务
 */
@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    @Autowired
    private INotificationService notificationService;

    /**
     * 分页查询通知
     * Task 14.4.1: 添加查询通知列表接口
     */
    @GetMapping("/page")
    @RequirePermission("notification:list")
    @OperationLog(value = "查询通知列表", description = "分页查询通知列表", operationType = "SELECT", recordParams = true, recordResult = false)
    public Result<Page<Notification>> page(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) Long receiverId,
            @RequestParam(required = false) String status) {
        Page<Notification> page = notificationService.page(pageNum, pageSize, receiverId, status);
        return Result.success(page);
    }

    /**
     * 获取未读通知数量
     */
    @GetMapping("/unread-count")
    @RequirePermission("notification:list")
    @OperationLog(value = "获取未读通知数量", description = "获取未读通知数量", operationType = "SELECT", recordParams = false, recordResult = false)
    public Result<?> getUnreadCount(@RequestParam Long receiverId) {
        int count = notificationService.getUnreadCount(receiverId);
        return Result.success(count);
    }

    /**
     * 标记通知为已读
     * Task 14.4.2: 添加标记通知已读接口
     */
    @PostMapping("/mark-read/{id}")
    @RequirePermission("notification:update")
    @OperationLog(value = "标记通知为已读", description = "标记单个通知为已读", operationType = "UPDATE", recordParams = true, recordResult = true)
    public Result<?> markAsRead(@PathVariable Long id) {
        boolean success = notificationService.markAsRead(id);
        return Result.success(success);
    }

    /**
     * 批量标记通知为已读
     */
    @PostMapping("/mark-all-read/{receiverId}")
    @RequirePermission("notification:update")
    @OperationLog(value = "批量标记通知为已读", description = "批量标记通知为已读", operationType = "UPDATE", recordParams = true, recordResult = true)
    public Result<?> markAllAsRead(@PathVariable Long receiverId) {
        boolean success = notificationService.markAllAsRead(receiverId);
        return Result.success(success);
    }

    /**
     * 获取当前用户的通知
     */
    @GetMapping("/my")
    @RequirePermission("notification:list")
    @OperationLog(value = "获取我的通知", description = "获取当前登录用户的通知列表", operationType = "SELECT", recordParams = false, recordResult = false)
    public Result<Page<Notification>> getMyNotifications(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String status) {
        // 从请求头获取当前用户ID
        // 暂时使用硬编码的测试用户ID
        Long currentUserId = 1L;
        Page<Notification> page = notificationService.page(pageNum, pageSize, currentUserId, status);
        return Result.success(page);
    }

    /**
     * 删除通知
     */
    @DeleteMapping("/{id}")
    @RequirePermission("notification:delete")
    @OperationLog(value = "删除通知", description = "删除通知记录", operationType = "DELETE", recordParams = true, recordResult = true)
    public Result<?> delete(@PathVariable Long id) {
        // 删除通知的实现需要在NotificationService中添加delete方法
        return Result.success(true);
    }
}