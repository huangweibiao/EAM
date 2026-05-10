package com.eam.service.impl;

import com.eam.annotation.OperationLog;
import com.eam.common.BusinessException;
import com.eam.entity.Notification;
import com.eam.entity.SparePart;
import com.eam.entity.SysUser;
import com.eam.entity.Asset;
import com.eam.entity.SysDepartment;
import com.eam.repository.NotificationRepository;
import com.eam.service.IAssetService;
import com.eam.service.ISparePartService;
import com.eam.service.ISysUserService;
import com.eam.service.INotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通知 Service 实现类
 * Task 14.4: 集成通知服务
 */
@Service
public class NotificationServiceImpl implements INotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    @Autowired(required = false)
    private ISysUserService userService;
    
    @Autowired(required = false)
    private IAssetService assetService;
    
    @Autowired(required = false)
    private ISparePartService sparePartService;
    
    @Autowired
    private NotificationRepository notificationRepository;

    /**
     * 分页查询通知
     */
    @Override
    public Page<Notification> page(Long pageNum, Long pageSize, Long receiverId, String status) {
        Specification<Notification> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            
            if (receiverId != null) {
                predicates.add(cb.equal(root.get("receiverId"), receiverId));
            }
            
            if (status != null && !status.isEmpty()) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        Pageable pageable = PageRequest.of(pageNum.intValue() - 1, pageSize.intValue(), 
                                            org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createTime"));
        return notificationRepository.findAll(spec, pageable);
    }

    /**
     * 获取未读通知数量
     */
    @Override
    public int getUnreadCount(Long receiverId) {
        Specification<Notification> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("receiverId"), receiverId));
            predicates.add(cb.equal(root.get("status"), Notification.NotificationStatus.UNREAD));
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
        
        return (int) notificationRepository.count(spec);
    }

    /**
     * 标记通知为已读
     */
    @Override
    @Transactional
    @OperationLog(value = "标记通知为已读", description = "标记通知为已读", operationType = "UPDATE", recordParams = true, recordResult = true)
    public boolean markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElse(null);
        if (notification == null) {
            return false;
        }
        
        notification.setStatus(Notification.NotificationStatus.READ);
        notification.setReadTime(java.time.LocalDateTime.now());
        notificationRepository.save(notification);
        
        return true;
    }

    /**
     * 批量标记通知为已读
     */
    @Override
    @Transactional
    @OperationLog(value = "批量标记通知为已读", description = "批量标记通知为已读", operationType = "UPDATE", recordParams = true, recordResult = true)
    public boolean markAllAsRead(Long receiverId) {
        try {
            Specification<Notification> spec = (root, query, cb) -> {
                List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("receiverId"), receiverId));
                predicates.add(cb.equal(root.get("status"), Notification.NotificationStatus.UNREAD));
                return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
            };
            
            List<Notification> notifications = notificationRepository.findAll(spec);
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            
            for (Notification notification : notifications) {
                notification.setStatus(Notification.NotificationStatus.READ);
                notification.setReadTime(now);
            }
            
            notificationRepository.saveAll(notifications);
            return true;
        } catch (Exception e) {
            logger.error("批量标记通知为已读失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 创建系统通知
     * Task 14.1: 设计通知机制（邮件/短信/系统消息）
     */
    @Override
    @Transactional
    @OperationLog(value = "创建系统通知", description = "创建系统通知", operationType = "CREATE", recordParams = true, recordResult = true)
    public Notification createSystemNotification(String title, String content, String type, Long receiverId, Long businessId) {
        try {
            // 查询接收用户
            SysUser receiver = null;
            if (receiverId != null) {
                receiver = userService.getById(receiverId);
            }
            
            // 创建通知实体
            Notification notification = new Notification();
            notification.setTitle(title);
            notification.setContent(content);
            notification.setType(type);
            notification.setLevel(Notification.NotificationLevel.INFO);
            notification.setStatus(Notification.NotificationStatus.UNREAD);
            notification.setChannel(Notification.NotificationChannel.SYSTEM);
            
            // 设置接收人信息
            if (receiver != null) {
                notification.setReceiverId(receiver.getId());
                notification.setReceiverName(receiver.getRealName());
            } else {
                notification.setReceiverId(1L); // 系统用户ID
                notification.setReceiverName("系统用户");
            }
            
            notification.setBusinessId(businessId);
            notification.setIsSent(true);
            notification.setSendTime(java.time.LocalDateTime.now());
            
            // 保存通知
            Notification savedNotification = notificationRepository.save(notification);
            
            logger.info("系统通知已创建: 标题={}, 接收人={}", title, notification.getReceiverName());
            return savedNotification;
            
        } catch (Exception e) {
            logger.error("创建系统通知失败: {}", e.getMessage(), e);
            throw new BusinessException("创建系统通知失败: " + e.getMessage());
        }
    }

    /**
     * 创建库存预警通知
     * Task 14.2: 实现库存预警通知
     */
    @Override
    @Transactional
    @OperationLog(value = "创建库存预警通知", description = "创建库存预警通知", operationType = "CREATE", recordParams = true, recordResult = true)
    public Notification createStockWarningNotification(Long sparePartId, String sparePartName, 
                                                   Integer currentQuantity, Integer minQuantity) {
        try {
            // 查询备件信息
            SparePart sparePart = sparePartService.getById(sparePartId);
            if (sparePart == null) {
                logger.error("备件不存在: ID={}", sparePartId);
                return null;
            }
            
            // 构建通知标题和内容
            String title = "库存预警：" + (sparePartName != null ? sparePartName : sparePart.getSparePartName());
            String content = String.format("备件【%s】当前库存为%d，已低于最小库存%d，请及时补货。",
                                        sparePart.getSparePartName(),
                                        currentQuantity,
                                        minQuantity);
            
            // 查询相关部门用户
            List<SysUser> deptUsers = userService.listByDept(sparePart.getDeptId());
            if (deptUsers.isEmpty()) {
                logger.warn("部门没有用户: 部门ID={}", sparePart.getDeptId());
                return null;
            }
            
            // 为部门所有用户创建通知
            List<Notification> notifications = new ArrayList<>();
            for (SysUser user : deptUsers) {
                Notification notification = new Notification();
                notification.setTitle(title);
                notification.setContent(content);
                notification.setType(Notification.NotificationType.STOCK_WARNING);
                notification.setLevel(Notification.NotificationLevel.WARNING);
                notification.setStatus(Notification.NotificationStatus.UNREAD);
                notification.setChannel(Notification.NotificationChannel.SYSTEM);
                notification.setReceiverId(user.getId());
                notification.setReceiverName(user.getRealName());
                notification.setBusinessId(sparePartId);
                notification.setExtraData(String.format("{\"sparePartId\":%d,\"currentQuantity\":%d,\"minQuantity\":%d}",
                                                       sparePartId, currentQuantity, minQuantity));
                notification.setIsSent(true);
                notification.setSendTime(java.time.LocalDateTime.now());
                
                notifications.add(notification);
            }
            
            // 批量保存通知
            List<Notification> savedNotifications = notificationRepository.saveAll(notifications);
            
            logger.info("库存预警通知已创建: 备件ID={}, 数量={}, 通知数={}", 
                       sparePartId, notifications.size(), savedNotifications.size());
            return savedNotifications.isEmpty() ? null : savedNotifications.get(0);
            
        } catch (Exception e) {
            logger.error("创建库存预警通知失败: {}", e.getMessage(), e);
            throw new BusinessException("创建库存预警通知失败: " + e.getMessage());
        }
    }

    /**
     * 创建维护提醒通知
     * Task 14.3: 实现维护提醒通知
     */
    @Override
    @Transactional
    @OperationLog(value = "创建维护提醒通知", description = "创建维护提醒通知", operationType = "CREATE", recordParams = true, recordResult = true)
    public Notification createMaintenanceReminderNotification(Long assetId, String assetName, 
                                                          LocalDate maintenanceDate) {
        try {
            // 查询资产信息
            Asset asset = assetService.getById(assetId);
            if (asset == null) {
                logger.error("资产不存在: ID={}", assetId);
                return null;
            }
            
            // 构建通知标题和内容
            String title = "维护提醒：" + (assetName != null ? assetName : asset.getAssetName());
            String content = String.format("资产【%s】计划于%s进行定期维护，请提前做好准备。",
                                        asset.getAssetName(),
                                        maintenanceDate);
            
            // 查询资产使用人
            Long receiverId = asset.getUserId();
            if (receiverId == null) {
                // 如果没有使用人，发送给部门所有用户
                List<SysUser> deptUsers = userService.listByDept(asset.getDeptId());
                if (deptUsers.isEmpty()) {
                    logger.warn("部门没有用户: 部门ID={}", asset.getDeptId());
                    return null;
                }
                receiverId = deptUsers.get(0).getId(); // 取第一个用户作为接收人
            }
            
            SysUser receiver = userService.getById(receiverId);
            if (receiver == null) {
                logger.warn("接收用户不存在: ID={}", receiverId);
                return null;
            }
            
            // 创建通知
            Notification notification = new Notification();
            notification.setTitle(title);
            notification.setContent(content);
            notification.setType(Notification.NotificationType.MAINTENANCE);
            notification.setLevel(Notification.NotificationLevel.INFO);
            notification.setStatus(Notification.NotificationStatus.UNREAD);
            notification.setChannel(Notification.NotificationChannel.SYSTEM);
            notification.setReceiverId(receiver.getId());
            notification.setReceiverName(receiver.getRealName());
            notification.setBusinessId(assetId);
            notification.setExtraData(String.format("{\"assetId\":%d,\"maintenanceDate\":\"%s\",\"assetName\":\"%s\"}",
                                                       assetId, maintenanceDate, asset.getAssetName()));
            notification.setIsSent(true);
            notification.setSendTime(java.time.LocalDateTime.now());
            
            // 保存通知
            Notification savedNotification = notificationRepository.save(notification);
            
            logger.info("维护提醒通知已创建: 资产ID={}, 维护日期={}, 接收人={}", 
                       assetId, maintenanceDate, receiver.getRealName());
            return savedNotification;
            
        } catch (Exception e) {
            logger.error("创建维护提醒通知失败: {}", e.getMessage(), e);
            throw new BusinessException("创建维护提醒通知失败: " + e.getMessage());
        }
    }

    /**
     * 发送邮件通知
     */
    @Override
    @Transactional
    @OperationLog(value = "发送邮件通知", description = "发送邮件通知", operationType = "CREATE", recordParams = true, recordResult = true)
    public boolean sendEmailNotification(Notification notification) {
        try {
            if (mailSender == null) {
                logger.warn("邮件发送服务未配置，跳过邮件发送");
                return false;
            }
            
            // 查询接收用户
            SysUser receiver = userService.getById(notification.getReceiverId());
            if (receiver == null) {
                logger.warn("接收用户不存在: ID={}", notification.getReceiverId());
                return false;
            }
            
            // 发送邮件
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(receiver.getEmail());
            message.setSubject(notification.getTitle());
            message.setText(notification.getContent());
            
            mailSender.send(message);
            
            // 标记为已发送
            notification.setIsSent(true);
            notification.setChannel(Notification.NotificationChannel.EMAIL);
            notificationRepository.save(notification);
            
            logger.info("邮件通知已发送: 标题={}, 收件人={}", notification.getTitle(), receiver.getEmail());
            return true;
            
        } catch (Exception e) {
            logger.error("发送邮件通知失败: {}", e.getMessage(), e);
            // 标记为发送失败
            notification.setIsSent(false);
            notificationRepository.save(notification);
            return false;
        }
    }

    /**
     * 发送短信通知
     */
    @Override
    @Transactional
    @OperationLog(value = "发送短信通知", description = "发送短信通知", operationType = "CREATE", recordParams = true, recordResult = true)
    public boolean sendSmsNotification(Notification notification) {
        try {
            // 这里需要集成短信服务（如阿里云、腾讯云短信服务）
            // 暂时只记录日志，不实际发送
            logger.info("短信通知待发送: 标题={}, 收件人ID={}, 内容={}", 
                       notification.getTitle(), notification.getReceiverId(), notification.getContent());
            
            // 标记为已发送（模拟）
            notification.setIsSent(true);
            notification.setChannel(Notification.NotificationChannel.SMS);
            notificationRepository.save(notification);
            
            logger.info("短信通知已标记为已发送: 标题={}", notification.getTitle());
            return true;
            
        } catch (Exception e) {
            logger.error("发送短信通知失败: {}", e.getMessage(), e);
            notification.setIsSent(false);
            notificationRepository.save(notification);
            return false;
        }
    }

    /**
     * 发送系统消息
     */
    @Override
    @Transactional
    @OperationLog(value = "发送系统消息", description = "发送系统消息", operationType = "CREATE", recordParams = true, recordResult = true)
    public boolean sendSystemNotification(Notification notification) {
        try {
            // 系统消息直接标记为已发送
            notification.setIsSent(true);
            notification.setSendTime(java.time.LocalDateTime.now());
            notification.setChannel(Notification.NotificationChannel.SYSTEM);
            notificationRepository.save(notification);
            
            logger.info("系统消息已发送: 标题={}, 接收人ID={}", 
                       notification.getTitle(), notification.getReceiverId());
            return true;
            
        } catch (Exception e) {
            logger.error("发送系统消息失败: {}", e.getMessage(), e);
            return false;
        }
    }
}