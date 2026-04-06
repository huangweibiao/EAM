# EAM 企业资产管理系统 - 开发任务清单

## 项目概览

- **后端**: Spring Boot 3.5.11 + MyBatis-Plus + MySQL 8.0
- **前端**: Vue 3 + Element Plus + TypeScript
- **目标**: 完成EAM系统完整功能开发

---

## Phase 1: 基础设施与系统管理

### 任务 1: 数据库初始化与Entity生成
- [x] 1.1 创建23张数据库表的Entity类(使用Lombok + MyBatis-Plus注解)
- [x] 1.2 配置MyBatis-Plus自动建表策略(application.yml)
- [x] 1.3 创建基础Mapper接口(BaseMapper)
- [x] 1.4 验证数据库连接与自动建表

### 任务 2: 公共组件封装
- [x] 2.1 完善Result统一响应包装类
- [x] 2.2 创建PageResult分页结果类
- [x] 2.3 创建BusinessException业务异常类
- [x] 2.4 创建GlobalExceptionHandler全局异常处理器
- [x] 2.5 创建BaseController基类

### 任务 3: 用户管理模块
- [x] 3.1 创建SysUser Entity完善字段映射
- [x] 3.2 创建SysUserMapper接口
- [x] 3.3 创建ISysUserService接口与SysUserServiceImpl实现
- [x] 3.4 创建SysUserController(分页/新增/修改/删除/重置密码)
- [x] 3.5 前端User.vue页面开发(列表/表单/操作)

### 任务 4: 角色管理模块
- [x] 4.1 创建SysRole Entity
- [x] 4.2 创建SysRoleMapper接口
- [x] 4.3 创建ISysRoleService接口与实现
- [x] 4.4 创建SysRoleController
- [x] 4.5 前端Role.vue页面开发(列表/权限分配)

### 任务 5: 部门管理模块
- [x] 5.1 创建SysDepartment Entity(树形结构)
- [x] 5.2 创建SysDeptMapper接口
- [x] 5.3 创建ISysDeptService(树形查询/递归子节点)
- [x] 5.4 创建SysDeptController
- [x] 5.5 前端Dept.vue页面开发(树形表格/新增/编辑)

### 任务 6: 菜单权限模块
- [x] 6.1 创建SysPermission Entity(树形菜单)
- [x] 6.2 创建SysPermissionMapper接口
- [x] 6.3 创建ISysPermissionService(树形菜单/按钮权限)
- [x] 6.4 创建SysPermissionController
- [x] 6.5 前端Menu.vue页面开发(树形菜单配置)

---

## Phase 2: 资产管理模块

### 任务 7: 资产分类管理
- [x] 7.1 创建AssetCategory Entity
- [x] 7.2 创建AssetCategoryMapper接口
- [x] 7.3 创建IAssetCategoryService接口与实现
- [x] 7.4 创建AssetCategoryController(树形查询/CRUD)
- [x] 7.5 前端Category.vue页面开发

### 任务 8: 资产台账管理
- [x] 8.1 创建Asset Entity(包含状态/折旧计算)
- [x] 8.2 创建AssetMapper接口
- [x] 8.3 创建IAssetService接口与实现(CRUD/导出)
- [x] 8.4 创建AssetController
- [x] 8.5 前端AssetList.vue页面开发(列表/表单/二维码)

### 任务 9: 资产变动管理
- [x] 9.1 创建AssetChangeLog Entity
- [x] 9.2 创建AssetChangeLogMapper接口
- [x] 9.3 创建change方法(记录变动历史)
- [x] 9.4 创建/api/asset/change接口
- [x] 9.5 前端资产变动记录查看功能

### 任务 10: 资产调拨管理
- [x] 10.1 创建AssetTransfer Entity
- [x] 10.2 创建AssetTransferMapper接口
- [x] 10.3 创建IAssetTransferService(调拨申请/审批/完成)
- [x] 10.4 创建AssetTransferController
- [x] 10.5 前端Transfer.vue页面开发

### 任务 11: 资产盘点管理
- [x] 11.1 创建AssetInventory Entity
- [x] 11.2 创建AssetInventoryDetail Entity
- [x] 11.3 创建盘点Service(创建/执行/完成)
- [x] 11.4 创建AssetInventoryController
- [x] 11.5 前端Inventory.vue页面开发

---

## Phase 3: 设备维护模块

### 任务 12: 维护计划管理
- [x] 12.1 创建MaintenancePlan Entity
- [x] 12.2 创建MaintenancePlanMapper接口
- [x] 12.3 创建IMaintenancePlanService(计划/周期计算/提醒)
- [x] 12.4 创建MaintenancePlanController
- [x] 12.5 前端Plan.vue页面开发

### 任务 13: 维护记录管理
- [x] 13.1 创建MaintenanceRecord Entity
- [x] 13.2 创建MaintenanceRecordMapper接口
- [x] 13.3 创建IMaintenanceRecordService(记录/成本统计)
- [x] 13.4 创建MaintenanceRecordController
- [x] 13.5 前端Record.vue页面开发

---

## Phase 4: 工单管理模块

### 任务 14: 工单管理
- [x] 14.1 创建WorkOrder Entity
- [x] 14.2 创建WorkOrderMapper接口
- [x] 14.3 创建IWorkOrderService(创建/派单/处理/完成/关闭)
- [x] 14.4 创建WorkOrderController
- [x] 14.5 前端WorkOrder.vue页面开发

### 任务 15: 工单-备件关联
- [x] 15.1 创建WorkOrderPart Entity
- [x] 15.2 创建工单关联备件功能
- [x] 15.3 创建工单使用备件统计接口

---

## Phase 5: 备件库存模块

### 任务 16: 备件分类管理
- [x] 16.1 创建SparePartCategory Entity
- [x] 16.2 创建SparePartCategoryMapper接口
- [x] 16.3 创建ISparePartCategoryService
- [x] 16.4 创建CategoryController

### 任务 17: 备件台账管理
- [x] 17.1 创建SparePart Entity
- [x] 17.2 创建SparePartMapper接口
- [x] 17.3 创建ISparePartService(CRUD/库存预警)
- [x] 17.4 创建SparePartController
- [x] 17.5 前端Part.vue页面开发

### 任务 18: 备件入库管理
- [x] 18.1 创建PartInbound Entity
- [x] 18.2 创建PartInboundMapper接口
- [x] 18.3 创建IInboundService(入库/更新库存)
- [x] 18.4 创建PartInboundController
- [x] 18.5 前端Inbound.vue页面开发

### 任务 19: 备件出库管理
- [x] 19.1 创建PartOutbound Entity
- [x] 19.2 创建PartOutboundMapper接口
- [x] 19.3 创建IOutboundService(出库/扣减库存/校验)
- [x] 19.4 创建PartOutboundController
- [x] 19.5 前端Outbound.vue页面开发

### 任务 20: 库存预警
- [x] 20.1 创建预警查询Service
- [x] 20.2 创建/api/part/warn接口
- [x] 20.3 前端Warning.vue页面开发

---

## Phase 6: 采购管理模块

### 任务 21: 供应商管理
- [x] 21.1 创建Supplier Entity
- [x] 21.2 创建SupplierMapper接口
- [x] 21.3 创建SupplierController(CRUD)
- [x] 21.4 前端供应商选择组件

### 任务 22: 采购申请管理
- [x] 22.1 创建PurchaseRequest Entity
- [x] 22.2 创建PurchaseRequestMapper接口
- [x] 22.3 创建IPurchaseRequestService(申请/审批)
- [x] 22.4 创建PurchaseRequestController
- [x] 22.5 前端Request.vue页面开发

### 任务 23: 采购订单管理
- [x] 23.1 创建PurchaseOrder Entity
- [x] 23.2 创建PurchaseOrderMapper接口
- [x] 23.3 创建IPurchaseOrderService(下单/收货/入库)
- [x] 23.4 创建PurchaseOrderController
- [x] 23.5 前端Order.vue页面开发

---

## Phase 7: 报表分析模块

### 任务 24: 资产汇总统计
- [x] 24.1 创建ReportService资产汇总查询
- [x] 24.2 创建/api/report/asset-summary接口
- [x] 24.3 前端AssetSummary.vue页面开发(图表+表格)

### 任务 25: 维护成本统计
- [x] 25.1 创建维护成本查询Service
- [x] 25.2 创建/api/report/maintenance-cost接口
- [x] 25.3 前端MaintenanceCost.vue页面开发

### 任务 26: 库存汇总统计
- [x] 26.1 创建库存汇总查询Service
- [x] 26.2 创建/api/report/inventory-summary接口
- [x] 26.3 前端InventoryReport.vue页面开发

---

## Phase 8: 仪表盘与优化

### 任务 27: 仪表盘数据对接
- [x] 27.1 创建Dashboard统计接口
- [x] 27.2 前端Dashboard.vue数据动态化
- [x] 27.3 添加实时数据刷新

### 任务 28: 操作日志
- [x] 28.1 创建SysOperationLog Entity
- [x] 28.2 创建AOP切面记录操作日志
- [x] 28.3 前端日志查看页面

### 任务 29: 安全与权限优化
- [x] 29.1 完善JWT Token生成与验证
- [x] 29.2 接口级权限控制(@PreAuthorize)
- [x] 29.3 前端按钮级权限控制

### 任务 30: 系统测试与调优
- [x] 30.1 前后端联调测试
- [x] 30.2 Bug修复与优化
- [x] 30.3 性能优化(缓存/索引)
- [x] 30.4 部署文档更新

---

## 技术债务清理(贯穿全程)

- [x] 统一错误处理
- [x] 统一日志记录
- [x] 代码规范检查
- [x] 单元测试补充
- [x] API文档生成
