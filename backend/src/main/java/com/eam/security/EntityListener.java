package com.eam.security;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;

/**
 * 审体监听器
 * 自动填充创建时间和更新时间
 */
public class EntityListener {

    @PrePersist
    public void onCreate(Object entity) {
        try {
            if (entity.getClass().getMethod("getCreateTime") != null) {
                entity.getClass().getMethod("setCreateTime", LocalDateTime.class).invoke(entity, LocalDateTime.now());
            }
            if (entity.getClass().getMethod("getUpdateTime") != null) {
                entity.getClass().getMethod("setUpdateTime", LocalDateTime.class).invoke(entity, LocalDateTime.now());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PreUpdate
    public void onUpdate(Object entity) {
        try {
            if (entity.getClass().getMethod("getUpdateTime") != null) {
                entity.getClass().getMethod("setUpdateTime", LocalDateTime.class).invoke(entity, LocalDateTime.now());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}