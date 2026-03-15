-- EAM Enterprise Asset Management System Database Initialization Script

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS eam DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE eam;

-- Drop tables if exists
DROP TABLE IF EXISTS `eam_asset`;
DROP TABLE IF EXISTS `eam_asset_category`;
DROP TABLE IF EXISTS `sys_role_menu`;
DROP TABLE IF EXISTS `sys_user_role`;
DROP TABLE IF EXISTS `sys_menu`;
DROP TABLE IF EXISTS `sys_role`;
DROP TABLE IF EXISTS `sys_user`;
DROP TABLE IF EXISTS `sys_dept`;

-- Department Table
CREATE TABLE `sys_dept` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Department ID',
    `dept_name` VARCHAR(50) NOT NULL COMMENT 'Department Name',
    `parent_id` BIGINT DEFAULT 0 COMMENT 'Parent Department ID',
    `sort` INT DEFAULT 0 COMMENT 'Sort Order',
    `leader` VARCHAR(50) DEFAULT NULL COMMENT 'Department Leader',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT 'Contact Phone',
    `email` VARCHAR(50) DEFAULT NULL COMMENT 'Contact Email',
    `status` TINYINT DEFAULT 1 COMMENT 'Status (0:Disabled, 1:Enabled)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT 'Remark',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Department Table';

-- User Table
CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'User ID',
    `username` VARCHAR(50) NOT NULL COMMENT 'Username',
    `password` VARCHAR(100) DEFAULT NULL COMMENT 'Password (OAuth2 users may not have this)',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT 'Nickname',
    `email` VARCHAR(100) DEFAULT NULL COMMENT 'Email',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT 'Phone Number',
    `gender` TINYINT DEFAULT 0 COMMENT 'Gender (0:Unknown, 1:Male, 2:Female)',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT 'Avatar URL',
    `dept_id` BIGINT DEFAULT NULL COMMENT 'Department ID',
    `status` TINYINT DEFAULT 1 COMMENT 'Status (0:Disabled, 1:Enabled)',
    `last_login_time` DATETIME DEFAULT NULL COMMENT 'Last Login Time',
    `last_login_ip` VARCHAR(50) DEFAULT NULL COMMENT 'Last Login IP',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    `oauth2_provider` VARCHAR(50) DEFAULT NULL COMMENT 'OAuth2 Provider Name',
    `oauth2_user_id` VARCHAR(100) DEFAULT NULL COMMENT 'OAuth2 User ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_dept_id` (`dept_id`),
    KEY `idx_oauth2_user` (`oauth2_provider`, `oauth2_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='User Table';

-- Role Table
CREATE TABLE `sys_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Role ID',
    `role_name` VARCHAR(50) NOT NULL COMMENT 'Role Name',
    `role_key` VARCHAR(50) NOT NULL COMMENT 'Role Key',
    `role_sort` INT DEFAULT 0 COMMENT 'Role Sort Order',
    `status` TINYINT DEFAULT 1 COMMENT 'Status (0:Disabled, 1:Enabled)',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT 'Remark',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_key` (`role_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Role Table';

-- User Role Relation Table
CREATE TABLE `sys_user_role` (
    `user_id` BIGINT NOT NULL COMMENT 'User ID',
    `role_id` BIGINT NOT NULL COMMENT 'Role ID',
    PRIMARY KEY (`user_id`, `role_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='User Role Relation Table';

-- Menu Table
CREATE TABLE `sys_menu` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Menu ID',
    `menu_name` VARCHAR(50) NOT NULL COMMENT 'Menu Name',
    `parent_id` BIGINT DEFAULT 0 COMMENT 'Parent Menu ID',
    `order_num` INT DEFAULT 0 COMMENT 'Display Order',
    `menu_type` CHAR(1) NOT NULL COMMENT 'Menu Type (M:Directory, C:Menu, F:Button)',
    `path` VARCHAR(200) DEFAULT NULL COMMENT 'Router Path',
    `component` VARCHAR(255) DEFAULT NULL COMMENT 'Component Path',
    `query` VARCHAR(255) DEFAULT NULL COMMENT 'Route Parameters',
    `is_frame` TINYINT DEFAULT 0 COMMENT 'Is External Link (0:No, 1:Yes)',
    `visible` TINYINT DEFAULT 1 COMMENT 'Is Visible (0:Hidden, 1:Visible)',
    `status` TINYINT DEFAULT 1 COMMENT 'Status (0:Disabled, 1:Enabled)',
    `perms` VARCHAR(100) DEFAULT NULL COMMENT 'Permission Identifier',
    `icon` VARCHAR(100) DEFAULT NULL COMMENT 'Menu Icon',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT 'Remark',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Menu Table';

-- Role Menu Relation Table
CREATE TABLE `sys_role_menu` (
    `role_id` BIGINT NOT NULL COMMENT 'Role ID',
    `menu_id` BIGINT NOT NULL COMMENT 'Menu ID',
    PRIMARY KEY (`role_id`, `menu_id`),
    KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Role Menu Relation Table';

-- Asset Category Table
CREATE TABLE `eam_asset_category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Category ID',
    `category_name` VARCHAR(50) NOT NULL COMMENT 'Category Name',
    `parent_id` BIGINT DEFAULT 0 COMMENT 'Parent Category ID',
    `category_code` VARCHAR(50) NOT NULL COMMENT 'Category Code',
    `sort` INT DEFAULT 0 COMMENT 'Sort Order',
    `status` TINYINT DEFAULT 1 COMMENT 'Status (0:Disabled, 1:Enabled)',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT 'Remark',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_category_code` (`category_code`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Asset Category Table';

-- Asset Table
CREATE TABLE `eam_asset` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Asset ID',
    `asset_code` VARCHAR(50) NOT NULL COMMENT 'Asset Code',
    `asset_name` VARCHAR(100) NOT NULL COMMENT 'Asset Name',
    `category_id` BIGINT NOT NULL COMMENT 'Asset Category ID',
    `specification` VARCHAR(200) DEFAULT NULL COMMENT 'Specification',
    `model` VARCHAR(100) DEFAULT NULL COMMENT 'Model',
    `brand` VARCHAR(50) DEFAULT NULL COMMENT 'Brand',
    `unit` VARCHAR(20) DEFAULT NULL COMMENT 'Measurement Unit',
    `purchase_date` DATE DEFAULT NULL COMMENT 'Purchase Date',
    `purchase_price` DECIMAL(18,2) DEFAULT NULL COMMENT 'Purchase Price',
    `supplier` VARCHAR(100) DEFAULT NULL COMMENT 'Supplier',
    `serial_number` VARCHAR(100) DEFAULT NULL COMMENT 'Serial Number',
    `warranty_period` INT DEFAULT NULL COMMENT 'Warranty Period (months)',
    `dept_id` BIGINT DEFAULT NULL COMMENT 'Department ID',
    `user_id` BIGINT DEFAULT NULL COMMENT 'User ID (Responsible Person)',
    `location` VARCHAR(200) DEFAULT NULL COMMENT 'Asset Location',
    `status` TINYINT DEFAULT 1 COMMENT 'Status (0:Scrapped, 1:Normal, 2:In Repair, 3:In Transfer)',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT 'Remark',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    `created_by` BIGINT DEFAULT NULL COMMENT 'Creator ID',
    `updated_by` BIGINT DEFAULT NULL COMMENT 'Updater ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_asset_code` (`asset_code`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_dept_id` (`dept_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Asset Table';

-- Insert initial data
-- Default department
INSERT INTO `sys_dept` (`id`, `dept_name`, `parent_id`, `sort`, `leader`, `status`) VALUES
(1, '总公司', 0, 1, 'admin', 1),
(2, '技术部', 1, 1, 'tech_lead', 1),
(3, '财务部', 1, 2, 'finance_lead', 1),
(4, '人事部', 1, 3, 'hr_lead', 1);

-- Default roles
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `role_sort`, `status`, `remark`) VALUES
(1, '超级管理员', 'admin', 1, 1, 'Super administrator with all permissions'),
(2, '普通用户', 'user', 2, 1, 'Regular user with basic permissions'),
(3, '资产管理员', 'asset_admin', 3, 1, 'Asset administrator');

-- Default user (OAuth2 users will be created dynamically)
INSERT INTO `sys_user` (`id`, `username`, `nickname`, `email`, `dept_id`, `status`) VALUES
(1, 'admin', '系统管理员', 'admin@example.com', 1, 1);

-- Assign admin role
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, 1);

-- Default menu structure
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `menu_type`, `path`, `component`, `icon`, `status`) VALUES
(1, '系统管理', 0, 1, 'M', 'system', NULL, 'system', 1),
(2, '用户管理', 1, 1, 'C', 'user', 'system/user/index', 'user', 1),
(3, '角色管理', 1, 2, 'C', 'role', 'system/role/index', 'peoples', 1),
(4, '菜单管理', 1, 3, 'C', 'menu', 'system/menu/index', 'tree-table', 1),
(5, '部门管理', 1, 4, 'C', 'dept', 'system/dept/index', 'tree', 1),
(6, '资产管理', 0, 2, 'M', 'asset', NULL, 'asset', 1),
(7, '资产台账', 6, 1, 'C', 'asset-list', 'asset/asset/index', 'list', 1),
(8, '资产分类', 6, 2, 'C', 'asset-category', 'asset/category/index', 'tree', 1);

-- Default asset categories
INSERT INTO `eam_asset_category` (`id`, `category_name`, `parent_id`, `category_code`, `sort`, `status`) VALUES
(1, '电子产品', 0, 'ELECTRONICS', 1, 1),
(2, '办公设备', 0, 'OFFICE', 2, 1),
(3, '家具', 0, 'FURNITURE', 3, 1),
(4, '车辆', 0, 'VEHICLE', 4, 1),
(5, '电脑', 1, 'COMPUTER', 1, 1),
(6, '打印机', 1, 'PRINTER', 2, 1),
(7, '桌椅', 3, 'DESK_CHAIR', 1, 1);

-- Assign all menus to admin role
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, `id` FROM `sys_menu`;

COMMIT;
