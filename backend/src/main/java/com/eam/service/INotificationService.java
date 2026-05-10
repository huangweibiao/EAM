package com.eam.service;

import com.eam.entity.Notification;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * 通知 Service 接口
 * Task 14.2: 实现库存预警通知
 * Task 14.3: 实现维护提醒通知
 */
public interface INotificationService {

    /**
     * 分页查询通知
     */
    Page<Notification> page(Long pageNum, Long pageSize, Long receiverId, String status);

    /**
     * 获取未读通知数量
     */
    int getUnreadCount(Long receiverId);

    /**
     * 标记通知为已读
     */
    boolean markAsRead(Long notificationId);

    /**
     * 批量标记通知为已读
     */
    boolean markAllAsRead(Long receiverId);

    /**
     * 创建系统通知
     * @param title 通知标题
     * @param content 通知内容
     * @param type 通知类型
     * @param receiverId 接收人ID
     * @param businessId 业务ID
     */
    Notification createSystemNotification(String title, String content, String type, Long receiverId, Long businessId);

    /**
     * 创建库存预警通知
     * Task 14.2: 实现库存预警通知
     */
    Notification createStockWarningNotification(Long sparePartId, String sparePartName, 
                                        Integer currentQuantity, Integer minQuantity);

    /**
     * 创建维护提醒通知
     * Task 14.3: 实现维护提醒通知
     */
    Notification createMaintenanceReminderNotification(Long assetId, String assetName, 
                                                  java.time.LocalDate maintenanceDate);

    /**
     * 发送邮件通知
     */
    boolean sendEmailNotification(Notification notification);

    /**
     * 发送短信通知
     */
    boolean sendSmsNotification(Notification notification);

    /**
     * 发送系统消息
     */
    boolean sendSystemNotification(Notification notification);
}