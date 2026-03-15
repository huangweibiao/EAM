# EAM - 企业资产管理系统

Enterprise Asset Management System

## 项目简介

EAM企业资产管理系统是一个基于Spring Boot 3.5.11 + MySQL 8 + Vue 3 + TypeScript构建的单应用系统，支持OAuth2单点登录功能。

## 技术栈

### 后端
- Spring Boot 3.5.11
- Spring Security + OAuth2
- Spring Data JPA
- MyBatis 3.0.3
- MySQL 8
- JWT (io.jsonwebtoken)
- Hutool 5.8.24

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
│   │   │   │   ├── config/    # 配置类
│   │   │   │   ├── controller/# 控制器
│   │   │   │   ├── service/   # 服务层
│   │   │   │   ├── entity/    # 实体类
│   │   │   │   ├── mapper/    # 数据访问层
│   │   │   │   ├── security/  # 安全配置
│   │   │   │   ├── dto/       # 数据传输对象
│   │   │   │   ├── common/    # 公共类
│   │   │   │   └── EamApplication.java
│   │   │   └── resources/
│   │   │       ├── static/    # 前端构建产物（不分离部署）
│   │   │       ├── db/        # 数据库脚本
│   │   │       └── application.yml
│   │   └── test/
│   └── pom.xml
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── assets/            # 静态资源
│   │   ├── components/        # 组件
│   │   ├── views/             # 页面
│   │   ├── router/            # 路由配置
│   │   ├── stores/            # 状态管理
│   │   ├── api/               # API接口
│   │   ├── App.vue
│   │   └── main.ts
│   ├── package.json
│   ├── tsconfig.json
│   └── vite.config.ts
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

### 2. 配置数据库

修改 `backend/src/main/resources/application.yml` 中的数据库配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/eam?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: your_password
```

### 3. 配置OAuth2服务端

修改 `backend/src/main/resources/application.yml` 中的OAuth2配置，根据您的OAuth2服务端进行调整：

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          oauth2-server:
            client-id: your-client-id
            client-secret: your-client-secret
            authorization-grant-type: authorization_code
            redirect-uri: http://localhost:8080/eam/login/oauth2/code/oauth2-server
            scope: read,write
        provider:
          oauth2-server:
            authorization-uri: http://your-oauth2-server/oauth2/authorize
            token-uri: http://your-oauth2-server/oauth2/token
            user-info-uri: http://your-oauth2-server/oauth2/userinfo
            user-name-attribute: username
```

### 4. 启动后端服务

```bash
cd backend
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080/eam` 启动

### 5. 启动前端开发服务器

```bash
cd frontend
npm install
npm run dev
```

前端开发服务器将在 `http://localhost:5173` 启动

### 6. 构建生产版本

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

## OAuth2单点登录流程

1. 用户访问系统首页 `/`
2. 系统检测到未登录，重定向到登录页 `/login`
3. 用户点击"OAuth2单点登录"按钮
4. 重定向到OAuth2服务端的授权页面
5. 用户在OAuth2服务端完成授权
6. OAuth2服务端回调到系统的回调地址，携带授权码
7. 系统使用授权码向OAuth2服务端换取访问令牌
8. 系统使用访问令牌获取用户信息
9. 创建或更新本地用户记录
10. 登录成功，重定向到系统首页

## 数据库设计

### 核心表结构

- **sys_user**: 用户表
- **sys_role**: 角色表
- **sys_user_role**: 用户角色关联表
- **sys_menu**: 菜单表
- **sys_role_menu**: 角色菜单关联表
- **sys_dept**: 部门表
- **eam_asset**: 资产表
- **eam_asset_category**: 资产分类表

详细的表结构请参考 `backend/src/main/resources/db/init.sql`

## 功能模块

### 已实现
- [x] 用户认证与授权
- [x] OAuth2单点登录集成
- [x] 基础权限框架
- [x] 数据库基础表结构
- [x] 前端页面框架
- [x] 路由与状态管理

### 待开发
- [ ] 用户管理（增删改查）
- [ ] 角色管理（增删改查）
- [ ] 菜单管理（增删改查）
- [ ] 部门管理（增删改查）
- [ ] 资产台账管理（增删改查）
- [ ] 资产分类管理（增删改查）
- [ ] 资产借用/归还
- [ ] 资产维修记录
- [ ] 资产报废管理
- [ ] 资产报表统计

## API接口

### 认证相关
- `GET /eam/api/auth/user` - 获取当前用户信息
- `GET /eam/api/auth/check` - 检查认证状态
- `GET /eam/api/auth/oidc-user` - 获取OAuth2用户用户信息

### 测试接口
- `GET /eam/api/hello` - 测试接口
- `GET /eam/api/whoami` - 获取当前登录用户名

### OAuth2登录
- `GET /eam/oauth2/authorization/{registrationId}` - OAuth2授权入口
- `GET /eam/logout` - 登出

## 开发说明

### 前后端不分离部署

本项目采用前后端不分离的方式部署，前端构建产物会被放置到后端的 `static` 目录中，由Spring Boot统一提供服务。

在开发阶段，可以使用Vite的开发服务器进行前后端联调，通过代理将API请求转发到后端服务。

生产部署时，前端代码会被构建到 `backend/src/main/resources/static` 目录，实现单应用部署。

## 注意事项

1. 确保OAuth2服务端已正确配置并运行
2. 确保MySQL数据库已正确创建并初始化
3. 生产环境部署前，请修改默认的配置和密钥
4. 建议使用环境变量或配置中心管理敏感信息

## 许可证

MIT License

## 联系方式

如有问题或建议，请联系项目维护者。
