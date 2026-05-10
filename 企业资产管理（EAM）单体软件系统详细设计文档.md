# 企业资产管理（EAM）单体软件系统详细设计文档

## 一、系统概述

EAM（Enterprise Asset Management）系统是企业用来管理其物理资产（如设备、设施、IT设备等）的软件系统。目标是通过资产全生命周期管理，提高资产利用率，降低运营成本，保证生产和运营的稳定性。

**系统目标：**

1. 资产信息统一管理（台账、生命周期状态）。

2. 维修和保养计划管理。

3. 资产采购、报废及调拨管理。

4. 数据分析和决策支持。

5. 权限分级与操作日志。

## 二、系统架构设计

### 2.1 总体架构

采用**单应用系统（Monolithic）** 架构，设计时保持模块化、可扩展性：

```Plain Text

+----------------------------+
|        用户界面层(UI)       |
|  Web端/移动端/桌面端       |
+----------------------------+
|        应用服务层(App)      |
| - 资产管理模块              |
| - 设备维护模块              |
| - 备件库存模块              |
| - 工单管理模块              |
| - 采购管理模块              |
| - 报表分析模块              |
| - 系统管理模块              |
+----------------------------+
|        数据访问层(DB)       |
| - 关系型数据库（MySQL 8.0） |
| - 缓存（Redis）             |
+----------------------------+
```

### 2.2 技术栈选型

|层级|技术选型|说明|
|---|---|---|
|前端|Vue3 + Element Plus+Typescript|SPA 应用，组件化|
|后端|Spring Boot + JPA|企业级稳定框架|
|数据库|MySQL 8.0|关系型，支持事务、复杂查询|
|缓存|Redis|提升查询性能、会话存储|
|安全|Spring Security + JWT|用户权限管理与身份验证|
|任务调度|Quartz|定时保养/提醒任务|
|日志|ELK (Elasticsearch + Logstash + Kibana)|系统日志和操作日志|
|部署|单体应用 + Nginx反向代理|简化部署，支持反向代理|
## 三、功能模块划分

```Plain Text

├── 资产管理模块（核心）：资产台账、生命周期、变动记录、盘点管理
├── 设备维护模块：维护计划、维护记录、周期提醒
├── 备件库存模块：备件台账、入库/出库、库存预警
├── 工单管理模块：工单创建、指派、处理、完成、评价
├── 采购管理模块：采购申请、采购订单、订单执行
├── 报表分析模块：资产汇总、维护成本、库存分析等
└── 系统管理模块：用户、角色、权限、操作日志
```

## 四、数据库设计

### 4.1 ER关系概览

```Plain Text

部门 ←→ 资产（多对一）
资产 ←→ 维护记录（一对多）
资产 ←→ 工单（一对多）
资产 ←→ 资产变动记录（一对多）
备件 ←→ 入库/出库记录（一对多）
工单 ←→ 备件使用（多对多）
采购申请 ←→ 采购订单（一对多）
用户 ←→ 角色（多对一）
角色 ←→ 权限（多对多）
```

### 4.2 数据库表详细设计

#### 1. 部门表 (sys_department)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|bigint|20|NOT NULL|-|主键ID|
|parent_id|bigint|20|YES|0|上级部门ID|
|dept_code|varchar|50|NOT NULL|-|部门编码|
|dept_name|varchar|100|NOT NULL|-|部门名称|
|manager|varchar|50|YES|NULL|负责人|
|phone|varchar|20|YES|NULL|联系电话|
|status|tinyint|1|NOT NULL|1|状态(0停用 1启用)|
|sort_order|int|11|NOT NULL|0|排序|
|remark|varchar|500|YES|NULL|备注|
|create_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP|创建时间|
|update_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP ON UPDATE|更新时间|
#### 2. 资产分类表 (asset_category)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|bigint|20|NOT NULL|-|主键ID|
|parent_id|bigint|20|YES|0|父分类ID|
|category_code|varchar|50|NOT NULL|-|分类编码|
|category_name|varchar|100|NOT NULL|-|分类名称|
|depreciation_rate|decimal|(5,2)|NOT NULL|0|年折旧率(%)|
|useful_life|int|11|NOT NULL|0|使用寿命(月)|
|status|tinyint|1|NOT NULL|1|状态(0停用 1启用)|
|sort_order|int|11|NOT NULL|0|排序|
|create_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP|创建时间|
|update_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP ON UPDATE|更新时间|
#### 3. 资产主表 (asset)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|bigint|20|NOT NULL|-|主键ID|
|asset_code|varchar|50|NOT NULL|-|资产编码(唯一)|
|asset_name|varchar|200|NOT NULL|-|资产名称|
|category_id|bigint|20|NOT NULL|-|分类ID|
|model|varchar|100|YES|NULL|规格型号|
|brand|varchar|100|YES|NULL|品牌|
|serial_number|varchar|100|YES|NULL|序列号/出厂编号|
|dept_id|bigint|20|NOT NULL|-|使用部门ID|
|user_id|bigint|20|YES|NULL|使用人ID|
|location|varchar|200|YES|NULL|存放位置|
|purchase_date|date|-|YES|NULL|购买日期|
|purchase_price|decimal|(12,2)|YES|0|购买原值|
|current_value|decimal|(12,2)|YES|0|当前净值|
|supplier_id|bigint|20|YES|NULL|供应商ID|
|warranty_end_date|date|-|YES|NULL|保修截止日期|
|status|varchar|20|NOT NULL|'IN_USE'|状态(NEW/IN_USE/MAINTENANCE/SCRAP/LOST)|
|maintenance_cycle|int|11|YES|0|维护周期(天)|
|last_maintenance_date|date|-|YES|NULL|最后维护日期|
|next_maintenance_date|date|-|YES|NULL|下次维护日期|
|qr_code|varchar|255|YES|NULL|二维码/条码|
|remark|varchar|500|YES|NULL|备注|
|create_by|varchar|50|NOT NULL|-|创建人|
|create_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP|创建时间|
|update_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP ON UPDATE|更新时间|
#### 4. 资产变动记录表 (asset_change_log)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|bigint|20|NOT NULL|-|主键ID|
|asset_id|bigint|20|NOT NULL|-|资产ID|
|change_type|varchar|20|NOT NULL|-|变动类型(DEPT/USER/STATUS/LOCATION/PRICE等)|
|old_value|varchar|255|YES|NULL|原值|
|new_value|varchar|255|YES|NULL|新值|
|reason|varchar|500|YES|NULL|变动原因|
|change_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP|变动时间|
|operator|varchar|50|NOT NULL|-|操作人|
|update_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP ON UPDATE|更新时间|
#### 5. 资产调拨表 (asset_transfer)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|bigint|20|NOT NULL|-|主键ID|
|transfer_no|varchar|50|NOT NULL|-|调拨单号(唯一)|
|asset_id|bigint|20|NOT NULL|-|资产ID|
|from_dept_id|bigint|20|NOT NULL|-|调出部门ID|
|to_dept_id|bigint|20|NOT NULL|-|调入部门ID|
|from_user_id|bigint|20|YES|NULL|调出使用人ID|
|to_user_id|bigint|20|YES|NULL|调入使用人ID|
|transfer_reason|varchar|500|YES|NULL|调拨原因|
|transfer_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP|调拨时间|
|operator|varchar|50|NOT NULL|-|操作人|
|approver|varchar|50|YES|NULL|审批人|
|approve_time|datetime|-|YES|NULL|审批时间|
|status|varchar|20|NOT NULL|'PENDING'|状态(PENDING/APPROVED/REJECTED/COMPLETED)|
|remark|varchar|500|YES|NULL|备注|
|create_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP|创建时间|
|update_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP ON UPDATE|更新时间|
#### 6. 供应商表 (supplier)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|bigint|20|NOT NULL|-|主键ID|
|supplier_code|varchar|50|NOT NULL|-|供应商编码(唯一)|
|supplier_name|varchar|200|NOT NULL|-|供应商名称|
|contact_person|varchar|50|YES|NULL|联系人|
|phone|varchar|20|YES|NULL|联系电话|
|email|varchar|100|YES|NULL|邮箱|
|address|varchar|500|YES|NULL|地址|
|bank_account|varchar|100|YES|NULL|银行账号|
|tax_no|varchar|50|YES|NULL|税号|
|cooperation_status|varchar|20|NOT NULL|'ACTIVE'|合作状态(ACTIVE/INACTIVE)|
|remark|varchar|500|YES|NULL|备注|
|create_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP|创建时间|
|update_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP ON UPDATE|更新时间|
#### 7. 维护计划表 (maintenance_plan)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|bigint|20|NOT NULL|-|主键ID|
|plan_code|varchar|50|NOT NULL|-|计划编码(唯一)|
|asset_id|bigint|20|NOT NULL|-|资产ID|
|plan_name|varchar|200|NOT NULL|-|计划名称|
|maintenance_type|varchar|20|NOT NULL|-|类型(PREVENTIVE/预测性维护 CORRECTIVE/纠正性维护)|
|cycle_type|varchar|20|NOT NULL|-|周期类型(DAY/MONTH/YEAR)|
|cycle_value|int|11|NOT NULL|0|周期值|
|last_execute_time|datetime|-|YES|NULL|上次执行时间|
|next_execute_time|datetime|-|YES|NULL|下次执行时间|
|status|varchar|20|NOT NULL|'ACTIVE'|状态(ACTIVE/PAUSED/COMPLETED)|
|responsible_person|varchar|50|YES|NULL|负责人|
|remark|varchar|500|YES|NULL|备注|
|create_by|varchar|50|NOT NULL|-|创建人|
|create_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP|创建时间|
|update_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP ON UPDATE|更新时间|
#### 8. 维护执行记录表 (maintenance_record)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|bigint|20|NOT NULL|-|主键ID|
|plan_id|bigint|20|YES|NULL|计划ID(可为空)|
|asset_id|bigint|20|NOT NULL|-|资产ID|
|maintenance_code|varchar|50|NOT NULL|-|维护单号(唯一)|
|maintenance_date|datetime|-|NOT NULL|-|维护日期|
|maintenance_type|varchar|20|NOT NULL|-|维护类型(同维护计划)|
|content|text|-|YES|NULL|维护内容|
|cost|decimal|(10,2)|YES|0|维护费用|
|technician|varchar|50|YES|NULL|维修人员|
|result|varchar|20|NOT NULL|'COMPLETED'|结果(COMPLETED/PENDING/FAILED)|
|next_maintenance_date|date|-|YES|NULL|下次维护建议日期|
|attachments|varchar|500|YES|NULL|附件(图片/文档，存储路径)|
|remark|varchar|500|YES|NULL|备注|
|create_by|varchar|50|NOT NULL|-|创建人|
|create_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP|创建时间|
|update_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP ON UPDATE|更新时间|
#### 9. 工单表 (work_order)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|bigint|20|NOT NULL|-|主键ID|
|order_no|varchar|50|NOT NULL|-|工单编号(唯一)|
|asset_id|bigint|20|NOT NULL|-|资产ID|
|order_type|varchar|20|NOT NULL|-|类型(REPAIR/维修 MAINTENANCE/保养 INSPECTION/巡检)|
|priority|varchar|10|NOT NULL|'MEDIUM'|优先级(HIGH/MEDIUM/LOW)|
|title|varchar|200|NOT NULL|-|工单标题|
|description|text|-|YES|NULL|问题描述|
|reporter|varchar|50|NOT NULL|-|报修人|
|report_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP|报修时间|
|assigned_to|varchar|50|YES|NULL|指派给|
|assign_time|datetime|-|YES|NULL|指派时间|
|status|varchar|20|NOT NULL|'PENDING'|状态(PENDING/ASSIGNED/PROCESSING/COMPLETED/CLOSED/CANCELED)|
|solution|text|-|YES|NULL|解决方案|
|complete_time|datetime|-|YES|NULL|完成时间|
|closed_time|datetime|-|YES|NULL|关闭时间|
|rating|tinyint|1|YES|NULL|满意度评分(1-5)|
|remark|varchar|500|YES|NULL|备注|
|create_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP|创建时间|
|update_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP ON UPDATE|更新时间|
#### 10. 备件分类表 (spare_part_category)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|bigint|20|NOT NULL|-|主键ID|
|parent_id|bigint|20|YES|0|父分类ID|
|category_code|varchar|50|NOT NULL|-|分类编码|
|category_name|varchar|100|NOT NULL|-|分类名称|
|status|tinyint|1|NOT NULL|1|状态(0停用 1启用)|
|sort_order|int|11|NOT NULL|0|排序|
|create_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP|创建时间|
|update_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP ON UPDATE|更新时间|
#### 11. 备件表 (spare_part)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|bigint|20|NOT NULL|-|主键ID|
|part_code|varchar|50|NOT NULL|-|备件编码(唯一)|
|part_name|varchar|200|NOT NULL|-|备件名称|
|category_id|bigint|20|YES|NULL|分类ID|
|model|varchar|100|YES|NULL|规格型号|
|unit|varchar|10|NOT NULL|-|单位(个/套/米等)|
|quantity|decimal|(12,2)|NOT NULL|0|当前库存数量|
|min_quantity|decimal|(12,2)|NOT NULL|0|最低库存预警量|
|max_quantity|decimal|(12,2)|NOT NULL|0|最高库存量|
|location|varchar|100|YES|NULL|仓库存放位置(库位)|
|supplier_id|bigint|20|YES|NULL|首选供应商ID|
|unit_price|decimal|(10,2)|YES|0|参考单价|
|status|varchar|20|NOT NULL|'ACTIVE'|状态(ACTIVE/INACTIVE)|
|remark|varchar|500|YES|NULL|备注|
|create_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP|创建时间|
|update_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP ON UPDATE|更新时间|
#### 12. 备件入库记录表 (part_inbound)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|bigint|20|NOT NULL|-|主键ID|
|inbound_no|varchar|50|NOT NULL|-|入库单号(唯一)|
|part_id|bigint|20|NOT NULL|-|备件ID|
|purchase_order_id|bigint|20|YES|NULL|采购订单ID|
|quantity|decimal|(12,2)|NOT NULL|0|入库数量|
|unit_price|decimal|(10,2)|NOT NULL|0|入库单价|
|total_amount|decimal|(12,2)|NOT NULL|0|总金额|
|inbound_date|datetime|-|NOT NULL|CURRENT_TIMESTAMP|入库日期|
|supplier_id|bigint|20|YES|NULL|供应商ID|
|batch_no|varchar|50|YES|NULL|批次号|
|production_date|date|-|YES|NULL|生产日期|
|expiry_date|date|-|YES|NULL|有效期|
|checker|varchar|50|YES|NULL|验收人|
|remark|varchar|500|YES|NULL|备注|
|create_by|varchar|50|NOT NULL|-|入库人|
|create_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP|创建时间|
|update_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP ON UPDATE|更新时间|
#### 13. 备件出库记录表 (part_outbound)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|bigint|20|NOT NULL|-|主键ID|
|outbound_no|varchar|50|NOT NULL|-|出库单号(唯一)|
|part_id|bigint|20|NOT NULL|-|备件ID|
|work_order_id|bigint|20|YES|NULL|关联工单ID|
|quantity|decimal|(12,2)|NOT NULL|0|出库数量|
|unit_price|decimal|(10,2)|NOT NULL|0|出库单价(参考)|
|total_amount|decimal|(12,2)|NOT NULL|0|总金额|
|outbound_date|datetime|-|NOT NULL|CURRENT_TIMESTAMP|出库日期|
|department_id|bigint|20|YES|NULL|领用部门ID|
|receiver|varchar|50|YES|NULL|领用人|
|purpose|varchar|200|YES|NULL|用途(维修/保养/其他)|
|checker|varchar|50|YES|NULL|审核人|
|remark|varchar|500|YES|NULL|备注|
|create_by|varchar|50|NOT NULL|-|出库经办人|
|create_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP|创建时间|
|update_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP ON UPDATE|更新时间|
#### 14. 工单-备件关联表 (work_order_part)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|bigint|20|NOT NULL|-|主键ID|
|work_order_id|bigint|20|NOT NULL|-|工单ID|
|part_id|bigint|20|NOT NULL|-|备件ID|
|plan_quantity|decimal|(12,2)|NOT NULL|0|计划使用数量|
|actual_quantity|decimal|(12,2)|YES|0|实际消耗数量|
|outbound_id|bigint|20|YES|NULL|出库记录ID|
|remark|varchar|500|YES|NULL|备注|
|create_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP|创建时间|
|update_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP ON UPDATE|更新时间|
#### 15. 采购申请表 (purchase_request)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|bigint|20|NOT NULL|-|主键ID|
|request_no|varchar|50|NOT NULL|-|申请单号(唯一)|
|part_id|bigint|20|NOT NULL|-|备件ID|
|request_quantity|decimal|(12,2)|NOT NULL|0|申请数量|
|estimated_price|decimal|(10,2)|YES|0|预估单价|
|total_estimated_amount|decimal|(12,2)|YES|0|预估总金额|
|urgency|varchar|10|NOT NULL|'NORMAL'|紧急程度(URGENT/NORMAL/LOW)|
|expected_date|date|-|NOT NULL|-|期望到货日期|
|reason|varchar|500|NOT NULL|-|申请原因|
|requester|varchar|50|NOT NULL|-|申请人|
|request_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP|申请时间|
|approver|varchar|50|YES|NULL|审批人|
|approve_time|datetime|-|YES|NULL|审批时间|
|approve_status|varchar|20|NOT NULL|'PENDING'|审批状态(PENDING/APPROVED/REJECTED)|
|approve_remark|varchar|500|YES|NULL|审批意见|
|purchase_order_id|bigint|20|YES|NULL|关联采购订单ID|
|status|varchar|20|NOT NULL|'ACTIVE'|状态(ACTIVE/COMPLETED/CANCELED)|
|create_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP|创建时间|
|update_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP ON UPDATE|更新时间|
#### 16. 采购订单表 (purchase_order)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|bigint|20|NOT NULL|-|主键ID|
|order_no|varchar|50|NOT NULL|-|采购订单号(唯一)|
|request_id|bigint|20|YES|NULL|采购申请ID|
|supplier_id|bigint|20|NOT NULL|-|供应商ID|
|part_id|bigint|20|NOT NULL|-|备件ID|
|order_quantity|decimal|(12,2)|NOT NULL|0|采购数量|
|unit_price|decimal|(10,2)|NOT NULL|0|合同单价|
|total_amount|decimal|(12,2)|NOT NULL|0|订单总金额|
|tax_amount|decimal|(12,2)|YES|0|税额|
|order_date|datetime|-|NOT NULL|CURRENT_TIMESTAMP|下单日期|
|delivery_date|date|-|YES|NULL|预计交货日期|
|actual_delivery_date|date|-|YES|NULL|实际交货日期|
|status|varchar|20|NOT NULL|'PENDING'|状态(PENDING/CONFIRMED/SHIPPED/RECEIVED/CANCELED)|
|receiver|varchar|50|YES|NULL|收货人|
|receive_time|datetime|-|YES|NULL|收货时间|
|payment_status|varchar|20|NOT NULL|'UNPAID'|支付状态(UNPAID/PARTIAL/PAID)|
|payment_time|datetime|-|YES|NULL|支付时间|
|remark|varchar|500|YES|NULL|备注|
|create_by|varchar|50|NOT NULL|-|创建人|
|create_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP|创建时间|
|update_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP ON UPDATE|更新时间|
#### 17. 资产盘点表 (asset_inventory)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|bigint|20|NOT NULL|-|主键ID|
|inventory_no|varchar|50|NOT NULL|-|盘点单号(唯一)|
|inventory_name|varchar|200|NOT NULL|-|盘点名称|
|dept_id|bigint|20|YES|NULL|盘点部门ID(空则全公司)|
|inventory_scope|varchar|20|NOT NULL|'ALL'|范围(ALL/PARTIAL)|
|start_time|datetime|-|NOT NULL|-|盘点开始时间|
|end_time|datetime|-|YES|NULL|盘点结束时间|
|status|varchar|20|NOT NULL|'IN_PROGRESS'|状态(IN_PROGRESS/COMPLETED/CANCELED)|
|total_asset_count|int|11|YES|0|应盘点资产总数|
|actual_count|int|11|YES|0|实际盘点数量|
|mismatch_count|int|11|YES|0|差异数量|
|create_by|varchar|50|NOT NULL|-|创建人|
|create_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP|创建时间|
|update_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP ON UPDATE|更新时间|
#### 18. 资产盘点明细表 (asset_inventory_detail)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|bigint|20|NOT NULL|-|主键ID|
|inventory_id|bigint|20|NOT NULL|-|盘点单ID|
|asset_id|bigint|20|NOT NULL|-|资产ID|
|system_location|varchar|200|YES|NULL|系统记录位置|
|actual_location|varchar|200|YES|NULL|实际盘点位置|
|system_status|varchar|20|YES|NULL|系统记录状态|
|actual_status|varchar|20|YES|NULL|实际盘点状态|
|is_match|tinyint|1|NOT NULL|1|是否一致(0不一致 1一致)|
|inventory_time|datetime|-|YES|NULL|盘点时间|
|inventory_by|varchar|50|YES|NULL|盘点人|
|remark|varchar|500|YES|NULL|差异说明|
|create_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP|创建时间|
|update_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP ON UPDATE|更新时间|
#### 19. 用户表 (sys_user)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|bigint|20|NOT NULL|-|主键ID|
|username|varchar|50|NOT NULL|-|用户名(唯一)|
|password|varchar|255|NOT NULL|-|密码(BCrypt加密)|
|real_name|varchar|50|NOT NULL|-|真实姓名|
|dept_id|bigint|20|YES|NULL|部门ID|
|email|varchar|100|YES|NULL|邮箱|
|phone|varchar|20|YES|NULL|手机号|
|role_id|bigint|20|YES|NULL|角色ID|
|avatar|varchar|255|YES|NULL|头像路径|
|status|tinyint|1|NOT NULL|1|状态(0禁用 1启用)|
|last_login_time|datetime|-|YES|NULL|最后登录时间|
|create_by|varchar|50|YES|NULL|创建人|
|create_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP|创建时间|
|update_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP ON UPDATE|更新时间|
#### 20. 角色表 (sys_role)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|bigint|20|NOT NULL|-|主键ID|
|role_code|varchar|50|NOT NULL|-|角色编码(唯一)|
|role_name|varchar|50|NOT NULL|-|角色名称|
|status|tinyint|1|NOT NULL|1|状态(0禁用 1启用)|
|remark|varchar|500|YES|NULL|备注|
|create_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP|创建时间|
|update_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP ON UPDATE|更新时间|
#### 21. 权限表 (sys_permission)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|bigint|20|NOT NULL|-|主键ID|
|perm_code|varchar|100|NOT NULL|-|权限编码|
|perm_name|varchar|100|NOT NULL|-|权限名称|
|parent_id|bigint|20|YES|0|父权限ID|
|perm_type|varchar|20|NOT NULL|-|权限类型(MENU/BUTTON/API)|
|path|varchar|255|YES|NULL|菜单路径/API路径|
|icon|varchar|50|YES|NULL|菜单图标|
|sort_order|int|11|NOT NULL|0|排序|
|status|tinyint|1|NOT NULL|1|状态(0禁用 1启用)|
|create_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP|创建时间|
|update_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP ON UPDATE|更新时间|
#### 22. 角色-权限关联表 (sys_role_permission)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|bigint|20|NOT NULL|-|主键ID|
|role_id|bigint|20|NOT NULL|-|角色ID|
|perm_id|bigint|20|NOT NULL|-|权限ID|
|create_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP|创建时间|
#### 23. 操作日志表 (sys_operation_log)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|bigint|20|NOT NULL|-|主键ID|
|user_id|bigint|20|YES|NULL|操作用户ID|
|username|varchar|50|YES|NULL|操作用户名|
|operation|varchar|100|NOT NULL|-|操作类型(新增/修改/删除/查询等)|
|module|varchar|50|YES|NULL|操作模块|
|method|varchar|200|YES|NULL|请求方法(类名+方法名)|
|params|text|-|YES|NULL|请求参数(JSON格式)|
|time_consuming|bigint|20|YES|0|耗时(毫秒)|
|ip|varchar|50|YES|NULL|IP地址|
|result|varchar|10|NOT NULL|'SUCCESS'|结果(SUCCESS/FAIL)|
|error_msg|text|-|YES|NULL|错误信息|
|create_time|datetime|-|NOT NULL|CURRENT_TIMESTAMP|操作时间|
### 4.3 索引设计建议

|表名|索引字段|索引类型|说明|
|---|---|---|---|
|asset|asset_code|UNIQUE|资产编码唯一索引|
|asset|dept_id|INDEX|按部门查询资产|
|asset|category_id|INDEX|按分类查询资产|
|asset|status|INDEX|按状态查询资产|
|asset|next_maintenance_date|INDEX|维护提醒查询|
|work_order|order_no|UNIQUE|工单编号唯一索引|
|work_order|asset_id|INDEX|按资产查询工单|
|work_order|status|INDEX|按状态查询工单|
|work_order|assigned_to|INDEX|按负责人查询工单|
|work_order|report_time|INDEX|按报修时间查询|
|spare_part|part_code|UNIQUE|备件编码唯一索引|
|spare_part|category_id|INDEX|按分类查询备件|
|spare_part|quantity|INDEX|库存预警查询|
|maintenance_record|asset_id|INDEX|按资产查询维护记录|
|maintenance_record|maintenance_date|INDEX|按时间查询维护记录|
|purchase_order|order_no|UNIQUE|采购订单号唯一索引|
|purchase_request|request_no|UNIQUE|申请单号唯一索引|
|sys_user|username|UNIQUE|用户名唯一索引|
|sys_user|dept_id|INDEX|按部门查询用户|
|asset_inventory|inventory_no|UNIQUE|盘点单号唯一索引|
|asset_inventory_detail|inventory_id,asset_id|UNIQUE|盘点单+资产唯一索引|
## 五、关键业务规则

### 5.1 资产状态流转

```Plain Text

NEW(新购) → IN_USE(使用中) ⇄ MAINTENANCE(维修中)
IN_USE(使用中) → SCRAP(报废)
IN_USE(使用中) → LOST(遗失)
```

### 5.2 工单状态流转

```Plain Text

PENDING(待处理) → ASSIGNED(已指派) → PROCESSING(处理中) → COMPLETED(已完成) → CLOSED(已关闭)
任意状态 → CANCELED(已取消)
```

### 5.3 自动触发规则

1. 资产下次维护日期 = 当前维护日期 + 维护周期（天）

2. 当备件库存 < 最低库存量时，自动创建采购申请（状态为DRAFT）

3. 每日定时任务：检查到期维护计划，自动生成对应工单

4. 资产调拨审批通过后，自动更新资产的dept_id/user_id，并生成资产变动记录

5. 备件入库/出库后，自动更新备件表的quantity字段

### 5.4 数据校验规则

1. 资产编码、备件编码、工单编号等编码类字段全局唯一

2. 资产净值不能大于购买原值

3. 备件出库数量不能大于当前库存数量

4. 采购订单数量不能大于采购申请数量（审批通过后）

5. 盘点差异需填写差异说明才能完成盘点

## 六、API接口设计概要

|模块|接口示例|请求方式|说明|
|---|---|---|---|
|资产管理|/api/asset/page|GET|资产分页查询|
|资产管理|/api/asset/add|POST|新增资产|
|资产管理|/api/asset/change|POST|资产变动（部门/使用人/状态）|
|资产管理|/api/asset/transfer|POST|资产调拨|
|资产管理|/api/asset/inventory/create|POST|创建盘点单|
|资产管理|/api/asset/inventory/complete|POST|完成盘点|
|设备维护|/api/maintenance/plan/add|POST|新增维护计划|
|设备维护|/api/maintenance/record/add|POST|新增维护记录|
|设备维护|/api/maintenance/plan/remind|GET|维护计划到期提醒|
|工单管理|/api/workorder/create|POST|创建工单|
|工单管理|/api/workorder/assign|POST|指派工单|
|工单管理|/api/workorder/complete|POST|完成工单|
|工单管理|/api/workorder/close|POST|关闭工单|
|备件管理|/api/part/page|GET|备件分页查询|
|备件管理|/api/part/inbound|POST|备件入库|
|备件管理|/api/part/outbound|POST|备件出库|
|备件管理|/api/part/warn|GET|备件库存预警查询|
|采购管理|/api/purchase/request/add|POST|新增采购申请|
|采购管理|/api/purchase/request/approve|POST|审批采购申请|
|采购管理|/api/purchase/order/add|POST|新增采购订单|
|报表分析|/api/report/asset-summary|GET|资产汇总统计（按部门/分类）|
|报表分析|/api/report/maintenance-cost|GET|维护成本统计（按时间/资产）|
|报表分析|/api/report/inventory-summary|GET|库存汇总统计|
|系统管理|/api/system/user/page|GET|用户分页查询|
|系统管理|/api/system/role/perm|POST|角色分配权限|
|系统管理|/api/system/log/page|GET|操作日志分页查询|
## 七、非功能设计

### 7.1 安全设计

1. 身份认证：JWT + Spring Security，token有效期2小时，刷新token机制

2. 权限控制：基于RBAC模型，控制到按钮/接口级别

3. 数据加密：密码BCrypt加密，敏感字段（如银行账号）AES加密

4. 日志审计：所有增删改操作记录操作日志，包含操作人、时间、IP、参数、结果

5. 防护措施：SQL注入防护（JPA参数绑定）、XSS防护（前端过滤+后端校验）、CSRF防护（Token验证）

### 7.2 性能设计

1. 查询优化：核心表建立合理索引，分页查询限制最大页数

2. 缓存优化：常用静态数据（部门、分类、角色）缓存至Redis，缓存时效1小时

3. 异步处理：维护提醒、报表生成等耗时操作异步执行

4. 批量处理：资产盘点、批量导入等支持批量操作，分批提交事务

### 7.3 运维设计

1. 部署方式：支持单机部署或Docker容器化部署

2. 日志管理：ELK集中式日志收集，按级别过滤

3. 数据备份：数据库每日全量备份 + 增量备份，保留30天

4. 监控报警：Prometheus + Grafana监控系统CPU/内存/磁盘/接口响应时间，异常阈值报警

5. 配置管理：核心配置（如缓存时效、自动提醒时间）支持配置化，无需重启服务

## 八、扩展性设计

1. 模块化设计：各模块解耦，后期可平滑拆分为微服务

2. 接口标准化：所有API遵循RESTful规范，便于第三方系统对接

3. 插件化扩展：报表、导出、通知等功能支持插件化开发

4. 多端适配：前端采用响应式设计，支持PC端、平板、移动端

5. 多数据源支持：预留多数据源配置，可扩展支持分库分表

## 九、总结

本设计文档覆盖了EAM系统的核心业务功能，包括资产管理全生命周期、设备维护、备件管理、工单管理、采购管理等模块，同时完善了数据库表结构（补充了调拨、权限、备件分类等缺失表），规范了索引设计、业务规则和API接口。

系统采用单体架构但预留了微服务拆分能力，兼顾了开发效率和后期扩展性。数据库设计遵循第三范式，核心业务表字段完整，索引覆盖常用查询场景，能够支撑企业资产管理的日常业务需求。

如需进一步细化某个模块（如工单流程详细设计、报表SQL、前端页面原型、定时任务配置等），可基于此文档补充完善。
> （注：文档部分内容可能由 AI 生成）