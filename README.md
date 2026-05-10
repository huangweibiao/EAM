# EAM - 企业资产管理系统

Enterprise Asset Management System

## 项目简介

EAM企业资产管理系统是一个基于Spring Boot 3.5.11 + MySQL 8 + Vue 3 + TypeScript构建的单应用系统，提供完整的资产管理全生命周期管理功能，支持OAuth2单点登录功能。

## 系统特性

- **全生命周期资产管理**：从资产采购、入库、使用、维护到报废的全流程管理
- **设备维护管理**：预防性维护计划、维护记录、维护提醒
- **工单管理系统**：维修工单、保养工单、巡检工单的创建、指派、处理、评价
- **备件库存管理**：备件台账、入库出库管理、库存预警
- **采购流程管理**：采购申请、审批、订单管理
- **数据分析报表**：资产汇总、维护成本统计、库存分析
- **权限控制系统**：基于RBAC的角色权限管理，支持细粒度权限控制
- **操作日志审计**：完整的操作日志记录和审计功能

## 技术栈

### 后端
- Spring Boot 3.5.11
- Spring Security + JWT
- Spring Data JPA
- MySQL 8
- JWT (io.jsonwebtoken)
- Quartz (定时任务)

### 前端
- Vue 3.4.0
- TypeScript 5.3.3
- Vite 5.0.8
- Element Plus 2.5.0
- Pinia 2.1.7
- Axios 1.6.2

## 项目结构

```
EAM/
├── backend/                    # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/eam/
│   │   │   │   ├── annotation/ # 注解
│   │   │   │   ├── aspect/     # AOP切面
│   │   │   │   ├── config/     # 配置类
│   │   │   │   ├── controller/ # 控制器
│   │   │   │   ├── dto/        # 数据传输对象
│   │   │   │   ├── entity/     # 实体类
│   │   │   │   ├── job/        # 定时任务
│   │   │   │   ├── repository/ # 数据访问层
│   │   │   │   ├── security/   # 安全配置
│   │   │   │   ├── service/    # 服务层
│   │   │   │   │   └── impl/   # 服务实现
│   │   │   │   └── common/     # 公共类
│   │   │   └── resources/
│   │   │       ├── static/      # 前端构建产物（不分离部署）
│   │   │       ├── db/          # 数据库脚本
│   │   │       └── application.yml
│   │   └── test/
│   └── pom.xml
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── api/               # API接口
│   │   ├── assets/            # 静态资源
│   │   ├── components/        # 组件
│   │   ├── router/            # 路由配置
│   │   ├── stores/            # 状态管理
│   │   ├── views/             # 页面
│   │   │   ├── asset/         # 资产管理页面
│   │   │   ├── maintenance/   # 维护管理页面
│   │   │   ├── purchase/      # 采购管理页面
│   │   │   ├── report/        # 报表分析页面
│   │   │   ├── spare/         # 备件管理页面
│   │   │   ├── system/        # 系统管理页面
│   │   │   └── workorder/     # 工单管理页面
│   │   ├── App.vue
│   │   └── main.ts
│   ├── package.json
│   ├── tsconfig.json
│   └── vite.config.ts
├── 企业资产管理（EAM）单体软件系统详细设计文档.md
└── README.md
```

## 快速开始

### 前置要求

- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Maven 3.8+

### 1. 数据库初始化

执行数据库初始化脚本：

```bash
mysql -u root -p < backend/src/main/resources/db/init.sql
```

或手动执行 `backend/src/main/resources/db/init.sql` 文件中的SQL语句。

默认管理员账号：
- 用户名：admin
- 密码：admin123

### 2. 配置数据库

修改 `backend/src/main/resources/application.yml` 中的数据库配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/eam?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: your_password
```

### 3 启动后端服务

```bash
cd backend
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080/eam` 启动

### 4. 启动前端开发服务器

```bash
cd frontend
npm install
npm run dev
```

前端开发服务器将在 `http://localhost:5173` 启动

### 5. 构建生产版本

构建前端并打包到后端：

```bash
cd frontend
npm install
npm run build
cd ../backend
mvn clean package
```

运行打包后的应用：

```bash
java -jar backend/target/eam-system-1.0.0.jar
```

访问 `http://localhost:8080/eam` 即可使用系统。

## 功能模块

### 已实现功能

#### 系统管理模块
- [x] 用户管理（增删改查）
- [x] 角色管理（增删改查）
- [x] 权限管理（增删改查）
- [x] 部门管理（增删改查）
- [x] 操作日志（查询统计）
- [x] OAuth2单点登录集成
- [x] JWT Token认证

#### 资产管理模块
- [x] 资产台账管理（增删改查、分页查询）
- [x] 资产分类管理（树形结构）
- [x] 资产变动记录
- [x] 资产调拨管理
- [x] 资产盘点管理

#### 设备维护模块
- [x] 维护计划管理
- [x] 维护记录管理
- [x] 维护到期提醒（定时任务）

#### 工单管理模块
- [x] 工单创建与编辑
- [x] 工单指派与处理
- [x] 工单完成与关闭
- [x] 工单评价功能
- [x] 工单状态流转

#### 备件管理模块
- [x] 备件台账管理（增删改查）
- [x] 备件分类管理
- [x] 备件入库管理
- [x] 备件出库管理
- [x] 库存预警功能

#### 采购管理模块
- [x] 采购申请管理
- [x] 采购申请审批
- [x] 采购订单管理
- [x] 订单执行跟踪

#### 报表分析模块
- [x] 资产汇总统计（按部门/分类）
- [x] 维护成本统计
- [x] 库存汇总统计
- [x] 出入库统计

### 待优化功能
- [ ] 资产二维码/条码生成与扫描
- [ ] 移动端适配
- [ ] 更多图表展示
- [ ] 导出功能（Excel/PDF）
- [ ] 消息通知功能
- [ ] 工作流引擎集成
- [ ] 性能优化与缓存策略

## 数据库设计

### 核心表结构

**系统管理**
- `sys_user`: 用户表
- `sys_role`: 角色表
- `sys_user_role`: 用户角色关联表
- `sys_permission`: 权限表
- `sys_role_permission`: 角色权限关联表
- `sys_department`: 部门表
- `sys_operation_log`: 操作日志表

**资产管理**
- `asset`: 资产主表
- `asset_category`: 资产分类表
- `asset_change_log`: 资产变动记录表
- `asset_transfer`: 资产调拨表
- `asset_inventory`: 资产盘点表
- `asset_inventory_detail`: 资产盘点明细表

**设备维护**
- `maintenance_plan`: 维护计划表
- `maintenance_record`: 维护执行记录表

**工单管理**
- `work_order`: 工单表
- `work_order_part`: 工单备件关联表

**备件管理**
- `spare_part`: 备件表
- `spare_part_category`: 备件分类表
- `part_inbound`: 备件入库记录表
- `part_outbound`: 备件出库记录表

**采购管理**
- `purchase_request`: 采购申请表
- `purchase_order`: 采购订单表

**供应商管理**
- `supplier`: 供应商表

详细的表结构和字段定义请参考 `backend/src/main/resources/db/init.sql` 和 `企业资产管理（EAM）单体软件系统详细设计文档.md`

## 业务规则

### 资产状态流转
```
NEW(新购) → IN_USE(使用中) ⇄ MAINTENANCE(维修中)
IN_USE(使用中) → SCRAP(报废)
IN_USE(使用中) → LOST(遗失)
```

### 工单状态流转
```
PENDING(待处理) → ASSIGNED(已指派) → PROCESSING(处理中) → COMPLETED(已完成) → CLOSED(已关闭)
任意状态 → CANCELED(已取消)
```

### 自动触发规则
1. 资产下次维护日期 = 当前维护日期 + 维护周期（天）
2. 当备件库存 < 最低库存量时，自动创建采购申请
3. 每日定时任务：检查到期维护计划，自动生成对应工单
4. 资产调拨审批通过后，自动更新资产的dept_id/user_id，并生成资产变动记录
5. 备件入库/出库后，自动更新备件表的quantity字段

## API接口

### 系统管理
- `GET /api/system/user/page` - 用户分页查询
- `GET /api/system/role/page` - 角色分页查询
- `GET /api/system/dept/list` - 部门列表
- `GET /api/system/permission/tree` - 权限树
- `GET /api/system/log/page` - 操作日志分页

### 资产管理
- `GET /api/asset/page` - 资产分页查询
- `POST /api/asset/add` - 新增资产
- `PUT /api/asset/update` - 修改资产
- `DELETE /api/asset/{id}` - 删除资产
- `POST /api/asset/change` - 资产变动
- `POST /api/asset/transfer` - 资产调拨

### 设备维护
- `GET /api/maintenance/plan/page` - 维护计划分页
- `POST /api/maintenance/plan/add` - 新增维护计划
- `GET /api/maintenance/record/page` - 维护记录分页
- `POST /api/maintenance/record/add` - 新增维护记录

### 工单管理
- `GET /api/workorder/page` - 工单分页查询
- `POST /api/workorder/create` - 创建工单
- `POST /api/workorder/assign` - 指派工单
- `POST /api/workorder/process` - 开始处理
- `POST /api/workorder/complete` - 完成工单
- `POST /api/workorder/close` - 关闭工单

### 备件管理
- `GET /api/part/page` - 备件分页查询
- `POST /api/part/inbound` - 备件入库
- `POST /api/part/outbound` - 备件出库
- `GET /api/part/warn` - 库存预警查询

### 采购管理
- `GET /api/purchase/request/page` - 采购申请分页
- `POST /api/purchase/request/approve` - 审批采购申请
- `GET /api/purchase/order/page` - 采购订单分页
- `POST /api/purchase/order/add` - 新增采购订单

### 报表分析
- `GET /api/report/asset-summary/dept` - 资产汇总（按部门）
- `GET /api/report/asset-summary/category` - 资产汇总（按分类）
- `GET /api/report/maintenance-cost` - 维护成本统计
- `GET /api/report/inventory-summary` - 库存汇总统计
- `GET /api/report/inbound-outbound` - 出入库统计

## 开发说明

### 前后端不分离部署

本项目采用前后端不分离的方式部署，前端构建产物会被放置到后端的 `static` 目录中，由Spring Boot统一提供服务。

在开发阶段，可以使用Vite的开发服务器进行前后端联调，通过代理将API请求转发到后端服务。

生产部署时，前端代码会被构建到 `backend/src/main/resources/static` 目录，实现单应用部署。

### 代码规范

#### 后端
- 遵循阿里巴巴Java开发手册
- Controller层负责请求处理和响应
- Service层负责业务逻辑处理
- Repository层负责数据访问
- 使用AOP处理操作日志记录

#### 前端
- 遵循Vue3官方风格指南
- 使用TypeScript进行类型检查
- 组件采用组合式API（Composition API）
- 使用Pinia进行状态管理

### 架构设计

系统采用分层架构设计：
- **表现层**：负责接收用户请求和返回响应
- **业务逻辑层**：负责业务逻辑处理和事务控制
- **数据访问层**：负责数据库操作
- **切面层**：负责日志记录、权限控制等横切关注点

## 部署说明

### Docker部署（推荐）

项目包含Docker部署脚本，可以一键部署：

```bash
./deploy.sh
```

### 传统部署

1. 配置生产环境的数据库连接
3. 构建前端并打包后端
4. 部署到应用服务器
5. 配置Nginx反向代理（可选）

## 注意事项

1. 确保MySQL数据库已正确创建并初始化
2. 生产环境部署前，请修改默认的配置和密钥
3. 建议使用环境变量或配置中心管理敏感信息
4. 定期备份数据库数据
5. 建议配置监控和告警机制

## 性能优化

- 数据库查询优化：合理使用索引，避免N+1查询
- 接口缓存：对频繁访问的数据进行缓存
- 分页查询：所有列表接口支持分页
- 异步处理：耗时操作采用异步处理
- 前端优化：代码分割、懒加载、图片压缩

## 安全建议

1. 定期更新依赖包版本
2. 配置HTTPS访问
3. 设置强密码策略
4. 限制登录尝试次数
5. 定期审计操作日志
6. 数据敏感字段加密存储
7. 防范SQL注入、XSS等攻击

## 许可证

MIT License

## 联系方式

如有问题或建议，请联系项目维护者。

## 更新日志

### v1.0.0 (2026-05-10)
- 完成系统核心功能开发
- 实现资产管理全生命周期
- 完成设备维护、工单管理功能
- 实现备件管理和采购管理
- 完成报表分析和权限管理