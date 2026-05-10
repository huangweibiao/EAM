package com.eam.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 通知消息 Entity
 * Task 14.1: 设计通知机制（邮件/短信/系统消息）
 */
@Entity
@Table(name = "eam_notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 通知标题
     */
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * 通知内容
     */
    @Column(name = "content", nullable = false, length = 2000)
    private String content;

    /**
     * 通知类型
     * SYSTEM: 系统消息
     * MAINTENANCE: 维护提醒
     * STOCK_WARNING: 库存预警
     * WORK_ORDER: 工单通知
     * PURCHASE: 采购通知
     */
    @Column(name = "type", nullable = false, length = 20)
    private String type;

    /**
     * 通知级别
     * INFO: 信息
     * WARNING: 警告
     * ERROR: 错误
     * SUCCESS: 成功
     */
    @Column(name = "level", nullable = false, length = 10)
    private String level;

    /**
     * 通知状态
     * UNREAD: 未读
     * READ: 已读
     */
    @Column(name = "status", nullable = false, length = 10)
    private String status;

    /**
     * 接收人ID
     */
    @Column(name = "receiver_id")
    private Long receiverId;

    /**
     * 接收人姓名
     */
    @Column(name = "receiver_name", length = 100)
    private String receiverName;

    /**
     * 发送渠道
     * SYSTEM: 系统内消息
     * EMAIL: 邮件
     * SMS: 短信
     */
    @Column(name = "channel", length = 10)
    private String channel;

    /**
     * 是否发送成功
     */
    @Column(name = "is_sent")
    private Boolean isSent;

    /**
     * 发送时间
     */
    @Column(name = "send_time")
    private LocalDateTime sendTime;

    /**
     * 阅读时间
     */
    @Column(name = "read_time")
    private LocalDateTime readTime;

    /**
     * 创建时间
     */
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    /**
     * 关联的业务数据ID
     * 根据type不同，关联不同的业务ID
     */
    @Column(name = "business_id")
    private Long businessId;

    /**
     * 额外数据（JSON格式）
     */
    @Column(name = "extra_data", length = 1000)
    private String extraData;

    /**
     * 备注
     */
    @Column(name = "remark", length = 500)
    private String remark;

    // ========== Getter和Setter方法 ==========

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Boolean getIsSent() {
        return isSent;
    }

    public void setIsSent(Boolean isSent) {
        this.isSent = isSent;
    }

    public LocalDateTime getSendTime() {
        return sendTime;
    }

    public void setSendTime(LocalDateTime sendTime) {
        this.sendTime = sendTime;
    }

    public LocalDateTime getReadTime() {
        return readTime;
    }

    public void setReadTime(LocalDateTime readTime) {
        this.readTime = readTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.create = createTime;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        if (status == null) {
            status = "UNREAD";
        }
        if (isSent == null) {
            isSent = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        // 更新时不修改创建时间
    }

    /**
     * 通知类型常量
     */
    public static class NotificationType {
        public static final String SYSTEM = "SYSTEM";
        public static final String MAINTENANCE = "MAINTENANCE";
        public static final String STOCK_WARNING = "STOCK_WARNING";
        public static final String WORK_ORDER = "WORK_ORDER";
        public static final String PURCHASE = "PURCHASE";
    }

    /**
     * 通知级别常量
     */
    public static class NotificationLevel {
        public static final String INFO = "INFO";
        public static final String WARNING = "WARNING";
        public static final String ERROR = "ERROR";
        public static final String SUCCESS = "SUCCESS";
    }

    /**
     * 通知渠道常量
     */
    public static class NotificationChannel {
        public static final String SYSTEM = "SYSTEM";
        public static final String EMAIL = "EMAIL";
        public static final String SMS = "SMS";
    }

    /**
     * 通知状态常量
     */
    public static class NotificationStatus {
        public static final String UNREAD = "UNREAD";
        public static final String READ = "READ";
    }
}