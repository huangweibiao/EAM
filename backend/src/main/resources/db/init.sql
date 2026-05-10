-- EAM Enterprise Asset Management System Database Initialization Script
-- 根据设计文档完善的数据库初始化脚本

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS eam DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE eam;

-- ============================================
-- 1. 系统管理模块表
-- ============================================

-- 1.1 部门表 (eam_sys_department) - 统一表名
DROP TABLE IF EXISTS `eam_sys_role_permission`;
DROP TABLE IF EXISTS `eam_sys_permission`;
DROP TABLE IF EXISTS `eam_sys_user_role`;
DROP TABLE IF EXISTS `eam_sys_role`;
DROP TABLE IF EXISTS `eam_sys_user`;
DROP TABLE IF EXISTS `eam_sys_department`;

CREATE TABLE `eam_sys_department` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `parent_id` BIGINT DEFAULT 0 COMMENT '上级部门ID',
    `dept_code` VARCHAR(50) NOT NULL COMMENT '部门编码',
    `dept_name` VARCHAR(100) NOT NULL COMMENT '部门名称',
    `manager` VARCHAR(50) DEFAULT NULL COMMENT '负责人',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `status` TINYINT DEFAULT 1 COMMENT '状态(0停用 1启用)',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dept_code` (`dept_code`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

-- 1.2 用户表 (eam_sys_user)
CREATE TABLE `eam_sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名(唯一)',
    `password` VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
    `dept_id` BIGINT DEFAULT NULL COMMENT '部门ID',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `role_id` BIGINT DEFAULT NULL COMMENT '角色ID',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像路径',
    `status` TINYINT DEFAULT 1 COMMENT '状态(0禁用 1启用)',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 1.3 角色表 (eam_sys_role)
CREATE TABLE `eam_sys_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码(唯一)',
    `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `status` TINYINT DEFAULT 1 COMMENT '状态(0禁用 1启用)',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 1.4 权限表 (eam_sys_permission)
CREATE TABLE `eam_sys_permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `perm_code` VARCHAR(100) NOT NULL COMMENT '权限编码',
    `perm_name` VARCHAR(100) NOT NULL COMMENT '权限名称',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父权限ID',
    `perm_type` VARCHAR(20) NOT NULL COMMENT '权限类型(MENU/BUTTON/API)',
    `path` VARCHAR(255) DEFAULT NULL COMMENT '菜单路径/API路径',
    `icon` VARCHAR(50) DEFAULT NULL COMMENT '菜单图标',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态(0禁用 1启用)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 1.5 角色-权限关联表 (eam_sys_role_permission)
CREATE TABLE `eam_sys_role_permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `perm_id` BIGINT NOT NULL COMMENT '权限ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_perm` (`role_id`, `perm_id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_perm_id` (`perm_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 1.6 用户-角色关联表 (eam_sys_user_role)
CREATE TABLE `eam_sys_user_role` (
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`user_id`, `role_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 1.7 操作日志表 (eam_sys_operation_log) - 新增
CREATE TABLE `eam_sys_operation_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT DEFAULT NULL COMMENT '操作用户ID',
    `username` VARCHAR(50) DEFAULT NULL COMMENT '操作用户名',
    `operation` VARCHAR(100) NOT NULL COMMENT '操作类型(新增/修改/删除/查询等)',
    `module` VARCHAR(50) DEFAULT NULL COMMENT '操作模块',
    `method` VARCHAR(200) DEFAULT NULL COMMENT '请求方法(类名+方法名)',
    `params` TEXT DEFAULT NULL COMMENT '请求参数(JSON格式)',
    `time_consuming` BIGINT DEFAULT 0 COMMENT '耗时(毫秒)',
    `ip` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    `result` VARCHAR(10) NOT NULL DEFAULT 'SUCCESS' COMMENT '结果(SUCCESS/FAIL)',
    `error_msg` TEXT DEFAULT NULL COMMENT '错误信息',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_operation` (`operation`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ============================================
-- 2. 资产管理模块表
-- ============================================

-- 2.1 资产分类表 (eam_asset_category)
DROP TABLE IF EXISTS `eam_asset_change_log`;
DROP TABLE IF EXISTS `eam_asset`;
DROP TABLE IF EXISTS `eam_asset_category`;

CREATE TABLE `eam_asset_category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父分类ID',
    `category_code` VARCHAR(50) NOT NULL COMMENT '分类编码',
    `category_name` VARCHAR(100) NOT NULL COMMENT '分类名称',
    `depreciation_rate` DECIMAL(5,2) DEFAULT 0 COMMENT '年折旧率(%)',
    `useful_life` INT DEFAULT 0 COMMENT '使用寿命(月)',
    `status` TINYINT DEFAULT 1 COMMENT '状态(0停用 1启用)',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_category_code` (`category_code`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资产分类表';

-- 2.2 资产主表 (eam_asset)
CREATE TABLE `eam_asset` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `asset_code` VARCHAR(50) NOT NULL COMMENT '资产编码(唯一)',
    `asset_name` VARCHAR(200) NOT NULL COMMENT '资产名称',
    `category_id` BIGINT NOT NULL COMMENT '分类ID',
    `model` VARCHAR(100) DEFAULT NULL COMMENT '规格型号',
    `brand` VARCHAR(100) DEFAULT NULL COMMENT '品牌',
    `serial_number` VARCHAR(100) DEFAULT NULL COMMENT '序列号/出厂编号',
    `dept_id` BIGINT NOT NULL COMMENT '使用部门ID',
    `user_id` BIGINT DEFAULT NULL COMMENT '使用人ID',
    `location` VARCHAR(200) DEFAULT NULL COMMENT '存放位置',
    `purchase_date` DATE DEFAULT NULL COMMENT '购买日期',
    `purchase_price` DECIMAL(12,2) DEFAULT 0 COMMENT '购买原值',
    `current_value` DECIMAL(12,2) DEFAULT 0 COMMENT '当前净值',
    `supplier_id` BIGINT DEFAULT NULL COMMENT '供应商ID',
    `warranty_end_date` DATE DEFAULT NULL COMMENT '保修截止日期',
    `status` VARCHAR(20) NOT NULL DEFAULT 'IN_USE' COMMENT '状态(NEW/IN_USE/MAINTENANCE/SCRAP/LOST)',
    `maintenance_cycle` INT DEFAULT 0 COMMENT '维护周期(天)',
    `last_maintenance_date` DATE DEFAULT NULL COMMENT '最后维护日期',
    `next_maintenance_date` DATE DEFAULT NULL COMMENT '下次维护日期',
    `qr_code` VARCHAR(255) DEFAULT NULL COMMENT '二维码/条码',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_by` VARCHAR(50) NOT NULL COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_asset_code` (`asset_code`),
    KEY `idx_dept_id` (`dept_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`),
    KEY `idx_next_maintenance_date` (`next_maintenance_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资产主表';

-- 2.3 资产变动记录表 (eam_asset_change_log)
CREATE TABLE `eam_asset_change_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `asset_id` BIGINT NOT NULL COMMENT '资产ID',
    `change_type` VARCHAR(20) NOT NULL COMMENT '变动类型(DEPT/USER/STATUS/LOCATION/PRICE等)',
    `old_value` VARCHAR(255) DEFAULT NULL COMMENT '原值',
    `new_value` VARCHAR(255) DEFAULT NULL COMMENT '新值',
    `reason` VARCHAR(500) DEFAULT NULL COMMENT '变动原因',
    `change_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '变动时间',
    `operator` VARCHAR(50) NOT NULL COMMENT '操作人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_asset_id` (`asset_id`),
    KEY `idx_change_time` (`change_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资产变动记录表';

-- 2.4 资产调拨表 (eam_asset_transfer)
DROP TABLE IF EXISTS `eam_asset_transfer`;

CREATE TABLE `eam_asset_transfer` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `transfer_no` VARCHAR(50) NOT NULL COMMENT '调拨单号(唯一)',
    `asset_id` BIGINT NOT NULL COMMENT '资产ID',
    `from_dept_id` BIGINT NOT NULL COMMENT '调出部门ID',
    `to_dept_id` BIGINT NOT NULL COMMENT '调入部门ID',
    `from_user_id` BIGINT DEFAULT NULL COMMENT '调出使用人ID',
    `to_user_id` BIGINT DEFAULT NULL COMMENT '调入使用人ID',
    `transfer_reason` VARCHAR(500) DEFAULT NULL COMMENT '调拨原因',
    `transfer_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '调拨时间',
    `operator` VARCHAR(50) NOT NULL COMMENT '操作人',
    `approver` VARCHAR(50) DEFAULT NULL COMMENT '审批人',
    `approve_time` DATETIME DEFAULT NULL COMMENT '审批时间',
    `complete_time` DATETIME DEFAULT NULL COMMENT '调拨完成时间',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态(PENDING/APPROVED/REJECTED/COMPLETED)',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_transfer_no` (`transfer_no`),
    KEY `idx_asset_id` (`asset_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资产调拨表';

-- 2.5 资产盘点表 (eam_asset_inventory)
DROP TABLE IF EXISTS `eam_asset_inventory_detail`;
DROP TABLE IF EXISTS `eam_asset_inventory`;

CREATE TABLE `eam_asset_inventory` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `inventory_no` VARCHAR(50) NOT NULL COMMENT '盘点单号(唯一)',
    `inventory_name` VARCHAR(200) NOT NULL COMMENT '盘点名称',
    `dept_id` BIGINT DEFAULT NULL COMMENT '盘点部门ID(空则全公司)',
    `inventory_scope` VARCHAR(20) NOT NULL DEFAULT 'ALL' COMMENT '范围(ALL/PARTIAL)',
    `start_time` DATETIME NOT NULL COMMENT '盘点开始时间',
    `end_time` DATETIME DEFAULT NULL COMMENT '盘点结束时间',
    `status` VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS' COMMENT '状态(IN_PROGRESS/COMPLETED/CANCELED)',
    `total_asset_count` INT DEFAULT 0 COMMENT '应盘点资产总数',
    `actual_count` INT DEFAULT 0 COMMENT '实际盘点数量',
    `mismatch_count` INT DEFAULT 0 COMMENT '差异数量',
    `create_by` VARCHAR(50) NOT NULL COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_inventory_no` (`inventory_no`),
    KEY `idx_dept_id` (`dept_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资产盘点表';

-- 2.6 资产盘点明细表 (eam_asset_inventory_detail)
CREATE TABLE `eam_asset_inventory_detail` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `inventory_id` BIGINT NOT NULL COMMENT '盘点单ID',
    `asset_id` BIGINT NOT NULL COMMENT '资产ID',
    `system_location` VARCHAR(200) DEFAULT NULL COMMENT '系统记录位置',
    `actual_location` VARCHAR(200) DEFAULT NULL COMMENT '实际盘点位置',
    `system_status` VARCHAR(20) DEFAULT NULL COMMENT '系统记录状态',
    `actual_status` VARCHAR(20) DEFAULT NULL COMMENT '实际盘点状态',
    `is_match` TINYINT NOT NULL DEFAULT 1 COMMENT '是否一致(0不一致 1一致)',
    `inventory_time` DATETIME DEFAULT NULL COMMENT '盘点时间',
    `inventory_by` VARCHAR(50) DEFAULT NULL COMMENT '盘点人',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '差异说明',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_inventory_asset` (`inventory_id`, `asset_id`),
    KEY `idx_asset_id` (`asset_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资产盘点明细表';

-- ============================================
-- 3. 供应商模块表
-- ============================================

DROP TABLE IF EXISTS `eam_supplier`;

CREATE TABLE `eam_supplier` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `supplier_code` VARCHAR(50) NOT NULL COMMENT '供应商编码(唯一)',
    `supplier_name` VARCHAR(200) NOT NULL COMMENT '供应商名称',
    `contact_person` VARCHAR(50) DEFAULT NULL COMMENT '联系人',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `address` VARCHAR(500) DEFAULT NULL COMMENT '地址',
    `bank_account` VARCHAR(100) DEFAULT NULL COMMENT '银行账号',
    `tax_no` VARCHAR(50) DEFAULT NULL COMMENT '税号',
    `cooperation_status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '合作状态(ACTIVE/INACTIVE)',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_supplier_code` (`supplier_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='供应商表';

-- ============================================
-- 4. 设备维护模块表
-- ============================================

-- 4.1 维护计划表 (eam_maintenance_plan)
DROP TABLE IF EXISTS `eam_maintenance_record`;
DROP TABLE IF EXISTS `eam_maintenance_plan`;

CREATE TABLE `eam_maintenance_plan` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `plan_code` VARCHAR(50) NOT NULL COMMENT '计划编码(唯一)',
    `asset_id` BIGINT NOT NULL COMMENT '资产ID',
    `plan_name` VARCHAR(200) NOT NULL COMMENT '计划名称',
    `maintenance_type` VARCHAR(20) NOT NULL COMMENT '类型(PREVENTIVE/CORRECTIVE)',
    `cycle_type` VARCHAR(20) NOT NULL COMMENT '周期类型(DAY/MONTH/YEAR)',
    `cycle_value` INT DEFAULT 0 COMMENT '周期值',
    `last_execute_time` DATETIME DEFAULT NULL COMMENT '上次执行时间',
    `next_execute_time` DATETIME DEFAULT NULL COMMENT '下次执行时间',
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态(ACTIVE/PAUSED/COMPLETED)',
    `responsible_person` VARCHAR(50) DEFAULT NULL COMMENT '负责人',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_by` VARCHAR(50) NOT NULL COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_plan_code` (`plan_code`),
    KEY `idx_asset_id` (`asset_id`),
    KEY `idx_next_execute_time` (`next_execute_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='维护计划表';

-- 4.2 维护执行记录表 (eam_maintenance_record)
CREATE TABLE `eam_maintenance_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `plan_id` BIGINT DEFAULT NULL COMMENT '计划ID(可为空)',
    `asset_id` BIGINT NOT NULL COMMENT '资产ID',
    `maintenance_code` VARCHAR(50) NOT NULL COMMENT '维护单号(唯一)',
    `maintenance_date` DATETIME NOT NULL COMMENT '维护日期',
    `maintenance_type` VARCHAR(20) NOT NULL COMMENT '维护类型',
    `content` TEXT DEFAULT NULL COMMENT '维护内容',
    `cost` DECIMAL(10,2) DEFAULT 0 COMMENT '维护费用',
    `technician` VARCHAR(50) DEFAULT NULL COMMENT '维修人员',
    `result` VARCHAR(20) NOT NULL DEFAULT 'COMPLETED' COMMENT '结果(COMPLETED/PENDING/FAILED)',
    `next_maintenance_date` DATE DEFAULT NULL COMMENT '下次维护建议日期',
    `attachments` VARCHAR(500) DEFAULT NULL COMMENT '附件(图片/文档，存储路径)',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_by` VARCHAR(50) NOT NULL COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_maintenance_code` (`maintenance_code`),
    KEY `idx_asset_id` (`asset_id`),
    KEY `idx_maintenance_date` (`maintenance_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='维护执行记录表';

-- ============================================
-- 5. 工单管理模块表
-- ============================================

DROP TABLE IF EXISTS `eam_work_order_part`;
DROP TABLE IF EXISTS `eam_work_order`;

CREATE TABLE `eam_work_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_no` VARCHAR(50) NOT NULL COMMENT '工单编号(唯一)',
    `asset_id` BIGINT NOT NULL COMMENT '资产ID',
    `order_type` VARCHAR(20) NOT NULL COMMENT '类型(REPAIR/MAINTENANCE/INSPECTION)',
    `priority` VARCHAR(10) NOT NULL DEFAULT 'MEDIUM' COMMENT '优先级(HIGH/MEDIUM/LOW)',
    `title` VARCHAR(200) NOT NULL COMMENT '工单标题',
    `description` TEXT DEFAULT NULL COMMENT '问题描述',
    `reporter` VARCHAR(50) NOT NULL COMMENT '报修人',
    `report_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '报修时间',
    `assigned_to` VARCHAR(50) DEFAULT NULL COMMENT '指派给',
    `assign_time` DATETIME DEFAULT NULL COMMENT '指派时间',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态(PENDING/ASSIGNED/PROCESSING/COMPLETED/CLOSED/CANCELED)',
    `solution` TEXT DEFAULT NULL COMMENT '解决方案',
    `complete_time` DATETIME DEFAULT NULL COMMENT '完成时间',
    `closed_time` DATETIME DEFAULT NULL COMMENT '关闭时间',
    `rating` TINYINT DEFAULT NULL COMMENT '满意度评分(1-5)',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_asset_id` (`asset_id`),
    KEY `idx_status` (`status`),
    KEY `idx_assigned_to` (`assigned_to`),
    KEY `idx_report_time` (`report_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工单表';

-- ============================================
-- 6. 备件库存模块表
-- ============================================

-- 6.1 备件分类表 (eam_spare_part_category)
DROP TABLE IF EXISTS `eam_part_outbound`;
DROP TABLE IF EXISTS `eam_part_inbound`;
DROP TABLE IF EXISTS `eam_spare_part`;
DROP TABLE IF EXISTS `eam_spare_part_category`;

CREATE TABLE `eam_spare_part_category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父分类ID',
    `category_code` VARCHAR(50) NOT NULL COMMENT '分类编码',
    `category_name` VARCHAR(100) NOT NULL COMMENT '分类名称',
    `status` TINYINT DEFAULT 1 COMMENT '状态(0停用 1启用)',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_category_code` (`category_code`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='备件分类表';

-- 6.2 备件表 (eam_spare_part)
CREATE TABLE `eam_spare_part` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `part_code` VARCHAR(50) NOT NULL COMMENT '备件编码(唯一)',
    `part_name` VARCHAR(200) NOT NULL COMMENT '备件名称',
    `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
    `model` VARCHAR(100) DEFAULT NULL COMMENT '规格型号',
    `unit` VARCHAR(10) NOT NULL COMMENT '单位(个/套/米等)',
    `quantity` DECIMAL(12,2) DEFAULT 0 COMMENT '当前库存数量',
    `min_quantity` DECIMAL(12,2) DEFAULT 0 COMMENT '最低库存预警量',
    `max_quantity` DECIMAL(12,2) DEFAULT 0 COMMENT '最高库存量',
    `location` VARCHAR(100) DEFAULT NULL COMMENT '仓库存放位置(库位)',
    `supplier_id` BIGINT DEFAULT NULL COMMENT '首选供应商ID',
    `unit_price` DECIMAL(10,2) DEFAULT 0 COMMENT '参考单价',
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态(ACTIVE/INACTIVE)',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_part_code` (`part_code`),
    KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='备件表';

-- 6.3 备件入库记录表 (eam_part_inbound)
CREATE TABLE `eam_part_inbound` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `inbound_no` VARCHAR(50) NOT NULL COMMENT '入库单号(唯一)',
    `part_id` BIGINT NOT NULL COMMENT '备件ID',
    `purchase_order_id` BIGINT DEFAULT NULL COMMENT '采购订单ID',
    `quantity` DECIMAL(12,2) DEFAULT 0 COMMENT '入库数量',
    `unit_price` DECIMAL(10,2) DEFAULT 0 COMMENT '入库单价',
    `total_amount` DECIMAL(12,2) DEFAULT 0 COMMENT '总金额',
    `inbound_date` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '入库日期',
    `supplier_id` BIGINT DEFAULT NULL COMMENT '供应商ID',
    `batch_no` VARCHAR(50) DEFAULT NULL COMMENT '批次号',
    `production_date` DATE DEFAULT NULL COMMENT '生产日期',
    `expiry_date` DATE DEFAULT NULL COMMENT '有效期',
    `checker` VARCHAR(50) DEFAULT NULL COMMENT '验收人',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_by` VARCHAR(50) NOT NULL COMMENT '入库人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_inbound_no` (`inbound_no`),
    KEY `idx_part_id` (`part_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='备件入库记录表';

-- 6.4 备件出库记录表 (eam_part_outbound)
CREATE TABLE `eam_part_outbound` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `outbound_no` VARCHAR(50) NOT NULL COMMENT '出库单号(唯一)',
    `part_id` BIGINT NOT NULL COMMENT '备件ID',
    `work_order_id` BIGINT DEFAULT NULL COMMENT '关联工单ID',
    `quantity` DECIMAL(12,2) DEFAULT 0 COMMENT '出库数量',
    `unit_price` DECIMAL(10,2) DEFAULT 0 COMMENT '出库单价(参考)',
    `total_amount` DECIMAL(12,2) DEFAULT 0 COMMENT '总金额',
    `outbound_date` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '出库日期',
    `department_id` BIGINT DEFAULT NULL COMMENT '领用部门ID',
    `receiver` VARCHAR(50) DEFAULT NULL COMMENT '领用人',
    `purpose` VARCHAR(200) DEFAULT NULL COMMENT '用途(维修/保养/其他)',
    `checker` VARCHAR(50) DEFAULT NULL COMMENT '审核人',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_by` VARCHAR(50) NOT NULL COMMENT '出库经办人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_outbound_no` (`outbound_no`),
    KEY `idx_part_id` (`part_id`),
    KEY `idx_work_order_id` (`work_order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='备件出库记录表';

-- 6.5 工单-备件关联表 (eam_work_order_part)
CREATE TABLE `eam_work_order_part` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `work_order_id` BIGINT NOT NULL COMMENT '工单ID',
    `part_id` BIGINT NOT NULL COMMENT '备件ID',
    `plan_quantity` DECIMAL(12,2) DEFAULT 0 COMMENT '计划使用数量',
    `actual_quantity` DECIMAL(12,2) DEFAULT 0 COMMENT '实际消耗数量',
    `outbound_id` BIGINT DEFAULT NULL COMMENT '出库记录ID',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_work_order_id` (`work_order_id`),
    KEY `idx_part_id` (`part_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工单备件关联表';

-- ============================================
-- 7. 采购管理模块表
-- ============================================

-- 7.1 采购申请表 (eam_purchase_request)
DROP TABLE IF EXISTS `eam_purchase_order`;
DROP TABLE IF EXISTS `eam_purchase_request`;

CREATE TABLE `eam_purchase_request` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `request_no` VARCHAR(50) NOT NULL COMMENT '申请单号(唯一)',
    `part_id` BIGINT NOT NULL COMMENT '备件ID',
    `request_quantity` DECIMAL(12,2) DEFAULT 0 COMMENT '申请数量',
    `estimated_price` DECIMAL(10,2) DEFAULT 0 COMMENT '预估单价',
    `total_estimated_amount` DECIMAL(12,2) DEFAULT 0 COMMENT '预估总金额',
    `urgency` VARCHAR(10) NOT NULL DEFAULT 'NORMAL' COMMENT '紧急程度(URGENT/NORMAL/LOW)',
    `expected_date` DATE NOT NULL COMMENT '期望到货日期',
    `reason` VARCHAR(500) NOT NULL COMMENT '申请原因',
    `requester` VARCHAR(50) NOT NULL COMMENT '申请人',
    `request_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    `approver` VARCHAR(50) DEFAULT NULL COMMENT '审批人',
    `approve_time` DATETIME DEFAULT NULL COMMENT '审批时间',
    `approve_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '审批状态(PENDING/APPROVED/REJECTED)',
    `approve_remark` VARCHAR(500) DEFAULT NULL COMMENT '审批意见',
    `purchase_order_id` BIGINT DEFAULT NULL COMMENT '关联采购订单ID',
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态(ACTIVE/COMPLETED/CANCELED)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_request_no` (`request_no`),
    KEY `idx_part_id` (`part_id`),
    KEY `idx_approve_status` (`approve_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='采购申请表';

-- 7.2 采购订单表 (eam_purchase_order)
CREATE TABLE `eam_purchase_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_no` VARCHAR(50) NOT NULL COMMENT '采购订单号(唯一)',
    `request_id` BIGINT DEFAULT NULL COMMENT '采购申请ID',
    `supplier_id` BIGINT NOT NULL COMMENT '供应商ID',
    `part_id` BIGINT NOT NULL COMMENT '备件ID',
    `order_quantity` DECIMAL(12,2) DEFAULT 0 COMMENT '采购数量',
    `unit_price` DECIMAL(10,2) DEFAULT 0 COMMENT '合同单价',
    `total_amount` DECIMAL(12,2) DEFAULT 0 COMMENT '订单总金额',
    `tax_amount` DECIMAL(12,2) DEFAULT 0 COMMENT '税额',
    `order_date` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '下单日期',
    `delivery_date` DATE DEFAULT NULL COMMENT '预计交货日期',
    `actual_delivery_date` DATE DEFAULT NULL COMMENT '实际交货日期',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态(PENDING/CONFIRMED/SHIPPED/RECEIVED/CANCELED)',
    `receiver` VARCHAR(50) DEFAULT NULL COMMENT '收货人',
    `receive_time` DATETIME DEFAULT NULL COMMENT '收货时间',
    `payment_status` VARCHAR(20) NOT NULL DEFAULT 'UNPAID' COMMENT '支付状态(UNPAID/PARTIAL/PAID)',
    `payment_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_by` VARCHAR(50) NOT NULL COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_supplier_id` (`supplier_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='采购订单表';

-- ============================================
-- 8. 初始化数据
-- ============================================

-- 8.1 默认部门数据
INSERT INTO `eam_sys_department` (`id`, `dept_code`, `dept_name`, `parent_id`, `sort_order`, `manager`, `status`) VALUES
(1, 'ROOT', '总公司', 0, 1, 'admin', 1),
(2, 'TECH', '技术部', 1, 1, 'tech_lead', 1),
(3, 'FIN', '财务部', 1, 2, 'finance_lead', 1),
(4, 'HR', '人事部', 1, 3, 'hr_lead', 1),
(5, 'OPS', '运维部', 1, 4, 'ops_lead', 1);

-- 8.2 默认角色数据
INSERT INTO `eam_sys_role` (`id`, `role_code`, `role_name`, `status`, `remark`) VALUES
(1, 'admin', '超级管理员', 1, '拥有所有权限'),
(2, 'user', '普通用户', 1, '基本操作权限'),
(3, 'asset_admin', '资产管理员', 1, '资产管理相关权限'),
(4, 'maintainer', '维护人员', 1, '设备维护相关权限');

-- 8.3 默认用户数据 (密码: admin123)
INSERT INTO `eam_sys_user` (`id`, `username`, `password`, `real_name`, `dept_id`, `role_id`, `email`, `phone`, `status`) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '系统管理员', 1, 1, 'admin@eam.com', '13800138000', 1);

-- 8.4 用户角色关联
INSERT INTO `eam_sys_user_role` (`user_id`, `role_id`) VALUES
(1, 1);

-- 8.5 默认权限数据
INSERT INTO `eam_sys_permission` (`id`, `perm_code`, `perm_name`, `parent_id`, `perm_type`, `path`, `icon`, `sort_order`, `status`) VALUES
-- 系统管理
(1, 'system', '系统管理', 0, 'MENU', '/system', 'system', 1, 1),
(2, 'system:user', '用户管理', 1, 'MENU', '/system/user', 'user', 1, 1),
(3, 'system:role', '角色管理', 1, 'MENU', '/system/role', 'peoples', 2, 1),
(4, 'system:dept', '部门管理', 1, 'MENU', '/system/dept', 'tree', 3, 1),
(5, 'system:log', '操作日志', 1, 'MENU', '/system/log', 'log', 4, 1),
-- 资产管理
(10, 'asset', '资产管理', 0, 'MENU', '/asset', 'asset', 2, 1),
(11, 'asset:list', '资产台账', 10, 'MENU', '/asset/list', 'list', 1, 1),
(12, 'asset:category', '资产分类', 10, 'MENU', '/asset/category', 'tree', 2, 1),
(13, 'asset:transfer', '资产调拨', 10, 'MENU', '/asset/transfer', 'transfer', 3, 1),
(14, 'asset:inventory', '资产盘点', 10, 'MENU', '/asset/inventory', 'inventory', 4, 1),
-- 维护管理
(20, 'maintenance', '维护管理', 0, 'MENU', '/maintenance', 'tools', 3, 1),
(21, 'maintenance:plan', '维护计划', 20, 'MENU', '/maintenance/plan', 'plan', 1, 1),
(22, 'maintenance:record', '维护记录', 20, 'MENU', '/maintenance/record', 'record', 2, 1),
-- 工单管理
(30, 'workorder', '工单管理', 0, 'MENU', '/workorder', 'workorder', 4, 1),
(31, 'workorder:list', '工单列表', 30, 'MENU', '/workorder/list', 'list', 1, 1),
-- 备件管理
(40, 'sparepart', '备件管理', 0, 'MENU', '/sparepart', 'part', 5, 1),
(41, 'sparepart:list', '备件台账', 40, 'MENU', '/sparepart/list', 'list', 1, 1),
(42, 'sparepart:inbound', '入库管理', 40, 'MENU', '/sparepart/inbound', 'inbound', 2, 1),
(43, 'sparepart:outbound', '出库管理', 40, 'MENU', '/sparepart/outbound', 'outbound', 3, 1),
-- 采购管理
(50, 'purchase', '采购管理', 0, 'MENU', '/purchase', 'purchase', 6, 1),
(51, 'purchase:request', '采购申请', 50, 'MENU', '/purchase/request', 'request', 1, 1),
(52, 'purchase:order', '采购订单', 50, 'MENU', '/purchase/order', 'order', 2, 1);

-- 8.6 角色权限关联 (给admin角色分配所有权限)
INSERT INTO `eam_sys_role_permission` (`role_id`, `perm_id`)
SELECT 1, `id` FROM `eam_sys_permission`;

-- 8.7 默认资产分类
INSERT INTO `eam_asset_category` (`id`, `category_code`, `category_name`, `parent_id`, `sort_order`, `status`, `depreciation_rate`, `useful_life`) VALUES
(1, 'ELECTRONICS', '电子产品', 0, 1, 1, 20.00, 36),
(2, 'OFFICE', '办公设备', 0, 2, 1, 15.00, 60),
(3, 'FURNITURE', '家具', 0, 3, 1, 10.00, 120),
(4, 'VEHICLE', '车辆', 0, 4, 1, 25.00, 60),
(5, 'COMPUTER', '电脑', 1, 1, 1, 25.00, 48),
(6, 'PRINTER', '打印机', 1, 2, 1, 20.00, 36),
(7, 'DESK_CHAIR', '桌椅', 3, 1, 1, 10.00, 120);

-- 8.8 默认备件分类
INSERT INTO `eam_spare_part_category` (`id`, `category_code`, `category_name`, `parent_id`, `sort_order`, `status`) VALUES
(1, 'ELEC_PARTS', '电子配件', 0, 1, 1),
(2, 'MECH_PARTS', '机械配件', 0, 2, 1),
(3, 'CONSUMABLES', '耗材', 0, 3, 1);

COMMIT;
