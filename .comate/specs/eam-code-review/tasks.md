# EAM系统代码审查与补充实现任务清单

## 任务总览
共 4 个主要任务，包含 21 个子任务

---

## 任务1：修复代码语法错误（编译错误）

- [x] 1.1 修复 AssetServiceImpl.java 中的语法错误
  - 修复第55行：`LambdaQueryWrapperWrapper` 改为 `LambdaQueryWrapper`

- [x] 1.2 修复 AssetTransferServiceImpl.java 中的语法错误
  - 修复第26行：`ServiceImplImpl` 改为 `ServiceImpl`
  - 修复第35行：`IPagePagePagePage<AssetTransfer>` 改为 `IPageage<AssetTransfer>`
  - 修复第36行：`Page Page Page Page<AssetTransfer>` 改为 `PagePage<AssetTransfer>`
  - 修复第37行：`LambdaQueryWrapperWrapperWrapperWrapper<AssetTransfer>` 改为 `LambdaQueryWrapperWrapper<AssetTransfer>`
  - 修复第146行：`List List List List<AssetTransfer>` 改为 `ListList<AssetTransfer>`
  - 修复第147行：`LambdaQueryWrapperWrapperWrapperWrapper<AssetTransfer>` 改为 `LambdaQueryWrapperWrapper<AssetTransfer>`

- [x] 1.3 修复 MaintenanceRecordServiceImpl.java 中的语法错误
  - 修复第29行：`ServiceImplImpl` 改为 `ServiceImpl`
  - 修复第38行：`IPagePagePagePage<MaintenanceRecord>` 改为 `IPageage<MaintenanceRecord>`
  - 修复第39行：`Page Page Page Page<MaintenanceRecord>` 改为 `PagePage<MaintenanceRecord>`
  - 修复第40行：`LambdaQueryWrapperWrapperWrapperWrapper<MaintenanceRecord>` 改为 `LambdaQueryWrapperWrapper<MaintenanceRecord>`
  - 修复第92行：`List List List List<MaintenanceRecord>` 改为 `ListList<MaintenanceRecord>`
  - 修复第93行：`LambdaQueryWrapperWrapperWrapperWrapper<MaintenanceRecord>` 改为 `LambdaQueryWrapperWrapper<MaintenanceRecord>`

- [x] 1.4 修复 PartServiceImpl.java 中的语法错误
  - 修复第26行：`ServiceImplImpl` 改为 `ServiceImpl`
  - 修复第32行：`IPagePagePagePage<PartInbound>` 改为 `IPageage<PartInbound>`
  - 修复第33行：`Page Page Page Page<PartInbound>` 改为 `PagePage<PartInbound>`
  - 修复第34行：`LambdaQueryWrapperWrapperWrapperWrapper<PartInbound>` 改为 `LambdaQueryWrapperWrapper<PartInbound>`
  - 修复第67行：`ServiceImplImpl` 改为 `ServiceImpl`
  - 修复第76行：`IPagePagePagePage<PartOutbound>` 改为 `IPageage<PartOutbound>`
  - 修复第77行：`Page Page Page Page<PartOutbound>` 改为 `PagePage<PartOutbound>`
  - 修复第78行：`LambdaQueryWrapperWrapperWrapperWrapper<PartOutbound>` 改为 `LambdaQueryWrapperWrapper<PartOutbound>`

- [x] 1.5 修复 PurchaseServiceImpl.java 中的语法错误
  - 修复第155行：`ServiceImplImpl` 改为 `ServiceImpl`
  - 修复第158行：`List List List List<Supplier>` 改为 `ListList<Supplier>`
  - 修复第164行：`LambdaQueryWrapperWrapperWrapperWrapper<Supplier>` 改为 `LambdaQueryWrapperWrapper<Supplier>`

---

## 任务2：补充数据库表结构

- [x] 2.1 在 init.sql 中添加工单-备件关联表
  - 添加 `work_order_part` 表的创建语句
  - 包含字段：id, work_order_id, part_id, plan_quantity, actual_quantity, outbound_id, remark, create_time, update_time
  - 添加索引：idx_work_order_id, idx_part_id

---

## 任务3：完善报表分析功能

- [x] 3.1 扩展 IReportService 接口
  - 添加 `assetSummary()` 方法：资产汇总统计（按部门/分类）
  - 添加 `maintenanceCostStatistics(startDate, endDate)` 方法：维护成本统计
  - 添加 `inventorySummary()` 方法：库存汇总统计

- [x] 3.2 实现 ReportServiceImpl
  - 实现资产汇总统计逻辑
  - 实现维护成本统计逻辑
  - 实现库存汇总统计逻辑

- [x] 3.3 完善 ReportController
  - 添加 `/api/report/asset-summary` 接口
  - 添加 `/api/report/maintenance-cost` 接口
  - 添加 `/api/report/inventory-summary` 接口

---

## 任务4：实现操作日志查询功能

- [x] 4.1 扩展 ISysOperationLogService 接口
  - 添加 `page(pageNum, pageSize, username, operation, module)` 分页查询方法

- [x] 4.2 实现 SysOperationLogServiceImpl
  - 实现分页查询逻辑
  - 支持按用户名、操作类型、模块筛选

- [x] 4.3 完善 SysOperationLogController
  - 添加 `/api/system/log/page` 分页查询接口
  - 支持查询参数：username, operation, module

---

## 执行顺序建议

1. **优先执行任务1**：修复语法错误，确保代码可编译
2. **然后执行任务2**：补充数据库表结构
3. **最后执行任务3和4**：完善功能接口

## 验证清单

- [x] 后端代码编译通过，无语法错误
- [x] 数据库初始化脚本可正常执行
- [x] 报表分析接口可正常访问
- [x] 操作日志查询接口可正常访问
