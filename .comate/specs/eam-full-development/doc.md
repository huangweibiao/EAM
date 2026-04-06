# EAM 企业资产管理系统 - 功能开发规格说明书

## 项目概述

基于企业资产管理（EAM）单体软件系统详细设计文档，完成前后端完整功能开发。服务启动后自动生成数据库表。后端使用Spring Boot 3.5.11 + MyBatis-Plus + MySQL 8.0+maven，前端使用Vue 3 + Element Plus + TypeScript。

---

## 一、开发范围与模块划分

### 1.1 核心模块优先级

| 优先级 | 模块 | 说明 |
|--------|------|------|
| P0 | 系统管理模块 | 用户、角色、部门、菜单管理 |
| P0 | 资产管理模块 | 资产台账、分类、变动、调拨、盘点 |
| P1 | 设备维护模块 | 维护计划、维护记录 |
| P1 | 工单管理模块 | 工单创建、派单、完成、评价 |
| P2 | 备件库存模块 | 备件台账、入库、出库、预警 |
| P2 | 采购管理模块 | 采购申请、订单管理 |
| P3 | 报表分析模块 | 资产汇总、维护成本、库存分析 |

---

## 二、后端开发规格

### 2.1 技术规范

- **框架**: Spring Boot 3.5.11
- **持久层**: MyBatis-Plus 3.5.x
- **数据库**: MySQL 8.0
- **安全**: Spring Security + JWT
- **代码规范**: 
  - Controller层使用RESTful风格
  - Service层接口 + 实现类分离
  - 使用通用响应包装Result<T>
  - 统一异常处理

### 2.2 包结构设计

```
com.eam/
├── controller/           # 控制器层
│   ├── SysController    # 系统管理(用户/角色/部门/菜单)
│   ├── AssetController  # 资产管理
│   ├── MaintenanceController # 设备维护
│   ├── WorkOrderController    # 工单管理
│   ├── SparePartController   # 备件库存
│   ├── PurchaseController    # 采购管理
│   └── ReportController      # 报表分析
├── service/             # 业务层(接口+实现)
│   ├── impl/
│   └── xxxService.java
├── mapper/              # 数据访问层
│   ├── xxxMapper.java
│   └── xxxMapper.xml
├── entity/              # 实体类
├── dto/                 # 数据传输对象
├── common/              # 公共组件(Result/异常/工具)
└── config/              # 配置类
```

### 2.3 数据库表(完整23张表)

使用设计文档中的表结构，通过init.sql初始化：

- `sys_department` - 部门表
- `sys_user` - 用户表
- `sys_role` - 角色表
- `sys_permission` - 权限表
- `sys_role_permission` - 角色权限关联
- `sys_operation_log` - 操作日志表
- `asset_category` - 资产分类表
- `asset` - 资产主表
- `asset_change_log` - 资产变动记录表
- `asset_transfer` - 资产调拨表
- `asset_inventory` - 资产盘点表
- `asset_inventory_detail` - 资产盘点明细表
- `supplier` - 供应商表
- `maintenance_plan` - 维护计划表
- `maintenance_record` - 维护执行记录表
- `work_order` - 工单表
- `work_order_part` - 工单-备件关联表
- `spare_part_category` - 备件分类表
- `spare_part` - 备件表
- `part_inbound` - 备件入库记录表
- `part_outbound` - 备件出库记录表
- `purchase_request` - 采购申请表
- `purchase_order` - 采购订单表

### 2.4 核心API设计

#### 系统管理API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/system/user/page | 用户分页查询 |
| POST | /api/system/user/add | 新增用户 |
| PUT | /api/system/user/update | 修改用户 |
| DELETE | /api/system/user/{id} | 删除用户 |
| GET | /api/system/role/page | 角色分页查询 |
| POST | /api/system/role/add | 新增角色 |
| PUT | /api/system/role/perm | 角色分配权限 |
| GET | /api/system/dept/tree | 部门树形查询 |
| POST | /api/system/dept/add | 新增部门 |
| GET | /api/system/menu/tree | 菜单树形查询 |

#### 资产管理API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/asset/page | 资产分页查询 |
| POST | /api/asset/add | 新增资产 |
| PUT | /api/asset/update | 修改资产 |
| DELETE | /api/asset/{id} | 删除资产 |
| POST | /api/asset/change | 资产变动(部门/使用人/状态) |
| POST | /api/asset/transfer | 资产调拨申请 |
| GET | /api/asset/category/tree | 资产分类树 |
| POST | /api/asset/category/add | 新增分类 |
| POST | /api/asset/inventory/create | 创建盘点单 |
| POST | /api/asset/inventory/complete | 完成盘点 |

#### 设备维护API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/maintenance/plan/page | 维护计划分页 |
| POST | /api/maintenance/plan/add | 新增维护计划 |
| PUT | /api/maintenance/plan/update | 修改维护计划 |
| GET | /api/maintenance/record/page | 维护记录分页 |
| POST | /api/maintenance/record/add | 新增维护记录 |
| GET | /api/maintenance/plan/remind | 维护计划到期提醒 |

#### 工单管理API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/workorder/page | 工单分页查询 |
| POST | /api/workorder/create | 创建工单 |
| POST | /api/workorder/assign | 派单 |
| POST | /api/workorder/process | 处理中 |
| POST | /api/workorder/complete | 完成工单 |
| POST | /api/workorder/close | 关闭工单 |

#### 备件管理API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/part/page | 备件分页查询 |
| POST | /api/part/add | 新增备件 |
| POST | /api/part/inbound | 备件入库 |
| POST | /api/part/outbound | 备件出库 |
| GET | /api/part/warn | 库存预警查询 |

#### 采购管理API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/purchase/request/page | 采购申请分页 |
| POST | /api/purchase/request/add | 新增采购申请 |
| POST | /api/purchase/request/approve | 审批采购申请 |
| GET | /api/purchase/order/page | 采购订单分页 |
| POST | /api/purchase/order/add | 新增采购订单 |

#### 报表分析API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/report/asset-summary | 资产汇总统计 |
| GET | /api/report/maintenance-cost | 维护成本统计 |
| GET | /api/report/inventory-summary | 库存汇总统计 |

---

## 三、前端开发规格

### 3.1 技术规范

- **框架**: Vue 3.4+ (Composition API + `<script setup>`)
- **UI组件**: Element Plus 2.5+
- **路由**: Vue Router 4.2+
- **状态管理**: Pinia
- **HTTP客户端**: Axios
- **代码规范**: TypeScript strict mode

### 3.2 页面组件规划

#### 系统管理页面

| 页面 | 组件路径 | 功能 |
|------|----------|------|
| 用户管理 | /views/system/User.vue | 用户列表、新增、编辑、删除、分配角色 |
| 角色管理 | /views/system/Role.vue | 角色列表、权限分配 |
| 部门管理 | /views/system/Dept.vue | 部门树形结构、增删改 |
| 菜单管理 | /views/system/Menu.vue | 菜单树形配置、按钮权限 |

#### 资产管理页面

| 页面 | 组件路径 | 功能 |
|------|----------|------|
| 资产台账 | /views/asset/AssetList.vue | 资产列表、增删改、导出、二维码打印 |
| 资产分类 | /views/asset/Category.vue | 分类树形管理 |
| 资产调拨 | /views/asset/Transfer.vue | 调拨申请、审批 |
| 资产盘点 | /views/asset/Inventory.vue | 盘点计划、盘点执行、差异处理 |

#### 设备维护页面

| 页面 | 组件路径 | 功能 |
|------|----------|------|
| 维护计划 | /views/maintenance/Plan.vue | 计划列表、新增、启停用 |
| 维护记录 | /views/maintenance/Record.vue | 维护记录查询、统计 |

#### 工单管理页面

| 页面 | 组件路径 | 功能 |
|------|----------|------|
| 工单列表 | /views/workorder/WorkOrder.vue | 工单列表、创建、派单、完成、评价 |

#### 备件管理页面

| 页面 | 组件路径 | 功能 |
|------|----------|------|
| 备件台账 | /views/spare/Part.vue | 备件列表、增删改 |
| 入库管理 | /views/spare/Inbound.vue | 入库记录、新增入库 |
| 出库管理 | /views/spare/Outbound.vue | 出库记录、新增出库 |
| 库存预警 | /views/spare/Warning.vue | 预警列表 |

#### 采购管理页面

| 页面 | 组件路径 | 功能 |
|------|----------|------|
| 采购申请 | /views/purchase/Request.vue | 申请列表、申请、审批 |
| 采购订单 | /views/purchase/Order.vue | 订单列表、下单、收货 |

#### 报表分析页面

| 页面 | 组件路径 | 功能 |
|------|----------|------|
| 资产统计 | /views/report/AssetSummary.vue | 按部门/分类统计 |
| 维护成本 | /views/report/MaintenanceCost.vue | 维护费用趋势 |
| 库存分析 | /views/report/Inventory.vue | 库存汇总、出入库统计 |

### 3.3 路由配置

在 `/router/index.ts` 中添加以下路由：

```typescript
// 系统管理
'/system/user'    -> SystemUser
'/system/role'    -> SystemRole
'/system/dept'    -> SystemDept
'/system/menu'    -> SystemMenu

// 资产管理
'/asset/list'     -> AssetList
'/asset/category' -> AssetCategory
'/asset/transfer' -> AssetTransfer
'/asset/inventory'-> AssetInventory

// 设备维护
'/maintenance/plan'   -> MaintenancePlan
'/maintenance/record'  -> MaintenanceRecord

// 工单管理
'/workorder'      -> WorkOrder

// 备件管理
'/spare/part'     -> SparePart
'/spare/inbound'  -> SpareInbound
'/spare/outbound' -> SpareOutbound
'/spare/warning'  -> SpareWarning

// 采购管理
'/purchase/request' -> PurchaseRequest
'/purchase/order'   -> PurchaseOrder

// 报表分析
'/report/asset'      -> AssetSummary
'/report/maintenance'-> MaintenanceCost
'/report/inventory'  -> InventoryReport
```

### 3.4 API封装

在 `/api/index.ts` 中扩展以下模块：

```typescript
// 系统管理API
systemApi = {
  userPage, userAdd, userUpdate, userDelete,
  rolePage, roleAdd, rolePerm, roleDelete,
  deptTree, deptAdd, deptUpdate, deptDelete,
  menuTree, menuAdd, menuUpdate, menuDelete
}

// 资产管理API
assetApi = {
  page, add, update, delete, change, transfer,
  categoryTree, categoryAdd, categoryUpdate,
  inventoryCreate, inventoryComplete, inventoryDetail
}

// 设备维护API
maintenanceApi = {
  planPage, planAdd, planUpdate, planDelete,
  recordPage, recordAdd, recordUpdate,
  planRemind
}

// 工单管理API
workOrderApi = {
  page, create, assign, process, complete, close, rate
}

// 备件管理API
spareApi = {
  page, add, update, delete,
  inbound, outbound, warn
}

// 采购管理API
purchaseApi = {
  requestPage, requestAdd, requestApprove,
  orderPage, orderAdd, orderReceive
}

// 报表API
reportApi = {
  assetSummary, maintenanceCost, inventorySummary
}
```

---

## 四、开发顺序与里程碑

### Phase 1: 基础设施 (优先级最高)

1. 完善数据库表结构与初始化脚本
2. 完成系统管理模块后端(用户/角色/部门/菜单)
3. 完成系统管理模块前端

### Phase 2: 核心业务 (P0)

4. 资产管理模块后端(资产CRUD、分类、变动、调拨、盘点)
5. 资产管理模块前端

### Phase 3: 业务扩展 (P1)

6. 设备维护模块后端+前端
7. 工单管理模块后端+前端

### Phase 4: 供应链 (P2)

8. 备件库存模块后端+前端
9. 采购管理模块后端+前端

### Phase 5: 决策支持 (P3)

10. 报表分析模块后端+前端
11. 仪表盘数据对接

---

## 五、关键实现要点

### 5.1 权限控制

- 后端: 使用 `@PreAuthorize` 注解控制接口权限
- 前端: 路由守卫 + 按钮级权限控制(基于菜单数据)

### 5.2 数据校验

- 后端: 使用Hibernate Validator注解校验
- 前端: Element Plus表单验证规则

### 5.3 代码生成策略

- 后端Entity使用Lombok减少样板代码
- 后端Service层抽取BaseService通用逻辑
- 前端列表页使用统一模板(搜索区+表格+分页+操作列)

### 5.4 状态流转控制

- 资产状态: NEW → IN_USE ↔ MAINTENANCE → SCRAP/LOST
- 工单状态: PENDING → ASSIGNED → PROCESSING → COMPLETED → CLOSED
- 调拨状态: PENDING → APPROVED/REJECTED → COMPLETED
- 采购状态: PENDING → APPROVED → CONFIRMED → SHIPPED → RECEIVED

---

## 六、验收标准

1. 所有API返回统一包装格式 `Result<T>`
2. 前端页面响应式布局，支持1440px及以上分辨率
3. 列表页支持分页、搜索、排序
4. 增删改操作有表单验证和成功/失败提示
5. 无前端控制台错误，后端无500异常
6. 关键业务操作记录操作日志